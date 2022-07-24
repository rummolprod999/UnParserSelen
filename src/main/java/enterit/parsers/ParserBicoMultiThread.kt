package enterit.parsers

import enterit.dataclasses.BicoT
import enterit.formatterOnlyDate
import enterit.tenders.TenderBico
import enterit.tools.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.*

data class PageBico(val page: String, val url: String)

class PageProducer(val bicoStore: BicoStore, val st: String, val countPage: Int) : Runnable {
    override fun run() {
        bicoStore.put(st, countPage)
    }

}

class PageConsumer(val bicoStore: BicoStore, val tr: Int, val fn: (PageBico) -> Unit) : Runnable {
    override fun run() {
        (0 until tr).forEach {
            val d = bicoStore.get()
            fn(d)
        }

    }
}

class BicoStore(val dec: ArrayDeque<PageBico>) {
    fun get(): PageBico {
        synchronized(this) {
            while (dec.size < 1) {
                (this as Object).wait()
            }
            val d = dec.pop()
            (this as Object).notify()
            return d
        }
    }

    fun put(url: String, countPage: Int) {
        (1..countPage).forEach { c ->
            val url = "$url?page=$c"
            val stPage = downloadFromUrl(url, i = 5, wt = 3000)
            if (stPage == "") {
                logger("Gets empty string ${this::class.simpleName}", url)
            }
            synchronized(this) {
                while (dec.size >= 5) {
                    (this as Object).wait()
                }
                dec.add(PageBico(stPage, url))
                (this as Object).notify()
            }
        }
    }
}

class ParserBicoMultiThread : Iparser {
    private val BaseUrl = "https://www.bicotender.ru"
    private val StartUrl = "https://www.bicotender.ru/catalog/by-field/"
    private val listUrls = mutableListOf<String>()
    private val countPage = 500
    val commonDequePages = ArrayDeque<PageBico>()

    override fun parser() {
        try {
            getUrlsFromStartPage()
        } catch (e: Exception) {
            logger("error in ${this::class.simpleName}.getUrlsFromStartPage()", e.stackTrace, e)
        }
        try {
            parserListTenders()
        } catch (e: Exception) {
            logger("error in ${this::class.simpleName}.parserListTenders()", e.stackTrace, e)
        }
    }

    private fun getUrlsFromStartPage() {
        val stPage = downloadFromUrl(StartUrl, i = 5, wt = 3000)
        if (stPage == "") {
            logger("Gets empty string ${this::class.simpleName}", StartUrl)
            return
        }
        val html = Jsoup.parse(stPage)
        val urls = html.select("div.content h3 > a")
        if (urls.isEmpty()) logger("Gets empty list urls ${this::class.simpleName}", StartUrl)
        urls.forEach<Element> ret@{
            var tmp = it.attr("href").trim { gg -> gg <= ' ' }
            if (!tmp.contains("by-field")) return@ret
            if (!tmp.startsWith("/")) tmp = "/$tmp"
            tmp = tmp.replace(".html", "")
            val u = "$BaseUrl$tmp"
            listUrls.add(u)
        }
    }

    private fun parserListTenders() {
        val st = BicoStore(commonDequePages)
        listUrls.forEach {
            val t = Thread(PageProducer(st, it, countPage))
            t.start()
            //t.join()
        }
        val tt = Thread(PageConsumer(st, listUrls.size * countPage, ::parserList))
        tt.start()
        tt.join()

    }

    private fun parserList(tnd: PageBico) {
        val url = tnd.url
        val stPage = tnd.page
        val html = Jsoup.parse(stPage)
        val tenders = html.select("table.tender-list  tbody  tr")
        tenders.forEach<Element> {
            try {
                parsingTender(it, url)
            } catch (e: Exception) {
                logger("error in ${this::class.simpleName}.parserTender()", e.stackTrace, e)
            }
        }
    }

    private fun parsingTender(el: Element, url: String) {
        val purObj = el.selectFirst("td:eq(0) a")?.ownText()?.trim { it <= ' ' }
            ?: throw IllegalArgumentException("purObj required $url")
        val urlT = el.selectFirst("td:eq(0) a")?.attr("href")?.trim { it <= ' ' }
            ?: ""
        if (urlT == "") run { logger("get empty urlT"); return }
        val urlTend = "$BaseUrl$urlT"
        val typeT = el.selectFirst("td:eq(1)")?.text()?.trim { it <= ' ' }
            ?: ""
        val purNum = typeT.getDataFromRegexp("#(\\d+)")
        if (purNum == "") {
            run { logger("get empty purNum $urlTend"); return }
        }
        val pwName = typeT.getDataFromRegexp("^(.+)#").deleteDoubleWhiteSpace()
        val priceT = el.selectFirst("td:eq(2)")?.text()?.trim { it <= ' ' }
            ?: ""
        val currency = priceT.getDataFromRegexp("\\s+([\\p{L}.]+)$").deleteDoubleWhiteSpace()
        val price = priceT.extractPrice()
        val dateS = el.selectFirst("td:eq(3)")?.text()?.deleteDoubleWhiteSpace()?.trim { it <= ' ' }
            ?: ""
        val pubDateT = dateS.getDataFromRegexp("(\\d{2}\\.\\d{2}\\.\\d{4})")
        val endDateT = dateS.getDataFromRegexp("(\\d{2}\\.\\d{2}\\.\\d{4})$")
        val datePub = getDateFromFormat(pubDateT, formatterOnlyDate)
        var dateEnd = getDateFromFormat(endDateT, formatterOnlyDate)
        if (dateEnd == Date(0L)) dateEnd = datePub
        if (datePub == Date(0L)) run { logger("bad datePub", urlTend, dateS); return }
        val region = el.selectFirst("td:eq(4)")?.text()?.deleteDoubleWhiteSpace()?.trim { it <= ' ' }
            ?: ""
        val otr = el.selectFirst("td:eq(5)")?.text()?.deleteDoubleWhiteSpace()?.trim { it <= ' ' }
            ?: ""
        val tn = BicoT(purNum, urlTend, purObj, datePub, dateEnd, pwName, price, currency, region, otr)
        try {
            Thread.sleep(30)
            val t = TenderBico(tn)
            t.parsing()
        } catch (e: Exception) {
            logger("error in TenderBico.parsing()", e.stackTrace, e, urlTend)
        }
    }
}