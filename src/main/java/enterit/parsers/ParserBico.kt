package enterit.parsers

import enterit.dataclasses.BicoT
import enterit.formatterOnlyDate
import enterit.tenders.TenderBico
import enterit.tools.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.*

class ParserBico : Iparser {
    private val BaseUrl = "https://www.bicotender.ru"
    private val StartUrl = "https://www.bicotender.ru/katalog/otrasly.html"
    private val listUrls = mutableListOf<String>()
    private val countPage = 500

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
        listUrls.forEach {
            (1..countPage).forEach ret@{ c ->
                try {
                    val size = parserList("$it/page/$c//?page=1")
                    if (size == 0) return@ret
                    //Thread.sleep(1000)
                } catch (e: Exception) {
                    logger("error in ${this::class.simpleName}.parserList()", e.stackTrace, e)
                }
            }
        }
    }

    private fun parserList(url: String): Int {
        val stPage = downloadFromUrl(url)
        if (stPage == "") {
            logger("Gets empty string ${this::class.simpleName}.parserList()", url)
            return 0
        }
        val html = Jsoup.parse(stPage)
        val tenders = html.select("table.tender-list  tbody  tr")
        tenders.forEach<Element> {
            try {
                parsingTender(it, url)
            } catch (e: Exception) {
                logger("error in ${this::class.simpleName}.parserTender()", e.stackTrace, e)
            }
        }
        return tenders.size
    }

    private fun parsingTender(el: Element, url: String) {
        val purObj = el.selectFirst("td:eq(0) a")?.ownText()?.trim { it <= ' ' }
            ?: throw IllegalArgumentException("purObj required $url")
        val urlT = el.selectFirst("td:eq(0) a")?.attr("href")?.trim { it <= ' ' }
            ?: ""
        if (urlT == "") run { logger("get empty urlT"); return }
        val urlTend = "$BaseUrl$urlT"
        val typeT = el.selectFirst("td:eq(1)")?.ownText()?.trim { it <= ' ' }
            ?: ""
        val purNum = typeT.getDataFromRegexp("#(\\d+)")
        if (purNum == "") {
            run { logger("get empty purNum $urlTend"); return }
        }
        val pwName = typeT.getDataFromRegexp("^(.+)#").deleteDoubleWhiteSpace()
        val priceT = el.selectFirst("td:eq(2)")?.text()?.trim { it <= ' ' }
            ?: ""
        val currency = priceT.getDataFromRegexp("(\\w+)\$").deleteDoubleWhiteSpace()
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
            val t = TenderBico(tn)
            t.parsing()
        } catch (e: Exception) {
            logger("error in TenderBico.parsing()", e.stackTrace, e, urlT)
        }
    }
}