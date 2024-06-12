package enterit.parsers

import enterit.dataclasses.CrimeaBT
import enterit.formatterOnlyDate
import enterit.tenders.TenderGosZakaz
import enterit.tools.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.*

class ParserGosZakaz : Iparser {
    private val BaseUrl = "http://goszakaz.by"
    private val StartUrl = "http://goszakaz.by/catalogue.html"
    private val listUrls = mutableListOf<String>()
    private val countPage = 25

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
        val urls = html.select("h2:contains(Тендеры Республики Беларусь по отраслям) + table ul li a")
        if (urls.isEmpty()) logger("Gets empty list urls ${this::class.simpleName}", StartUrl)
        urls.forEach<Element> {
            var tmp = it.attr("href").trim { gg -> gg <= ' ' }
            if (!tmp.startsWith("/")) tmp = "/$tmp"
            val u = "$BaseUrl$tmp"
            listUrls.add(u)
        }
    }

    private fun parserListTenders() {
        listUrls.forEach {
            (1..countPage).forEach ret@{ c ->
                try {
                    val size = parserList("$it?page=$c")
                    if (size == 0) return@ret
                    Thread.sleep(1000)
                } catch (e: Exception) {
                    logger("error in ${this::class.simpleName}.parserList()", e.stackTrace, e)
                }
            }
        }
    }

    private fun parserList(url: String): Int {
        val stPage = downloadFromUrl(url, i = 3, wt = 3000)
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

    private fun parsingTender(
        el: Element,
        url: String,
    ) {
        val purObj =
            el.selectFirst("td:eq(0) a")?.ownText()?.trim { it <= ' ' }
                ?: throw IllegalArgumentException("purObj required $url")
        val urlT =
            el.selectFirst("td:eq(0) a")?.attr("href")?.trim { it <= ' ' }
                ?: ""
        if (urlT == "") {
            run {
                logger("get empty urlT")
                return
            }
        }
        val urlTend = "$BaseUrl$urlT"
        val typeT =
            el.selectFirst("td:eq(1)")?.ownText()?.trim { it <= ' ' }
                ?: ""
        val purNum = typeT.getDataFromRegexp("#(\\d+)")
        if (purNum == "") {
            run {
                logger("get empty purNum $urlTend")
                return
            }
        }
        val pwName = typeT.getDataFromRegexp("^(.+)#").deleteDoubleWhiteSpace()
        val priceT =
            el.selectFirst("td:eq(2) span")?.text()?.trim { it <= ' ' }
                ?: ""
        val currency = priceT.getDataFromRegexp("(\\w+)\$").deleteDoubleWhiteSpace()
        val price = priceT.extractPrice()
        val dateS =
            el
                .selectFirst("td:eq(3)")
                ?.text()
                ?.deleteDoubleWhiteSpace()
                ?.trim { it <= ' ' }
                ?: ""
        val pubDateT = dateS.getDataFromRegexp("^(\\d{2}\\.\\d{2}\\.\\d{4})")
        val endDateT = dateS.getDataFromRegexp("(\\d{2}\\.\\d{2}\\.\\d{4})$")
        val datePub = getDateFromFormat(pubDateT, formatterOnlyDate)
        val dateEnd = getDateFromFormat(endDateT, formatterOnlyDate)
        if (datePub == Date(0L) || dateEnd == Date(0L)) {
            run {
                logger("bad dates", urlTend, dateS)
                return
            }
        }
        val tn = CrimeaBT(purNum, urlTend, purObj, datePub, dateEnd, pwName, price, currency)
        try {
            val t = TenderGosZakaz(tn)
            t.parsing()
        } catch (e: Exception) {
            logger("error in TenderCrimeaBt.parsing()", e.stackTrace, e, urlT)
        }
    }
}
