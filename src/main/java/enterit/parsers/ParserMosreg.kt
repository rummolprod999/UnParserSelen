package enterit.parsers

import enterit.formatterGpn
import enterit.formatterOnlyDate
import enterit.tenders.TenderMosreg
import enterit.tools.deleteAllWhiteSpace
import enterit.tools.findElementWithoutException
import enterit.tools.getDateFromFormat
import enterit.tools.logger
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.Level

class ParserMosreg : Iparser {
    init {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog")
        java.util.logging.Logger.getLogger("org.openqa.selenium").level = Level.OFF
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver")
    }

    companion object WebCl {
        const val BaseUrl = "https://market.mosreg.ru/Trade"
        const val timeoutB = 30L
        const val CountPage = 1
    }

    private val tendersS = mutableListOf<TenderMosreg>()
    override fun parser() {
        val options = ChromeOptions()
        options.addArguments("headless")
        options.addArguments("disable-gpu")
        options.addArguments("no-sandbox")
        options.addArguments("ignore-certificate-errors")
        options.setAcceptInsecureCerts(true)
        val driver = ChromeDriver(options)
        try {
            driver.manage().timeouts().pageLoadTimeout(timeoutB, TimeUnit.SECONDS)
            driver.manage().deleteAllCookies()
            driver.get(BaseUrl)
            driver.switchTo().defaultContent()
            //driver.manage().window().maximize()
            val wait = WebDriverWait(driver, timeoutB)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(@class, 'paginator_last')  and  .  =  '>>']")))
            //driver.clickertWithoutException(By.xpath("(//span[contains(@class, 'select2-container--default') and @dir = 'ltr'])[2]"))
            /*val js = driver as JavascriptExecutor
            js.executeScript("document.querySelectorAll('span.select2-selection__rendered')[0].click()")
            js.executeScript("document.querySelectorAll('#select2-selectPagination-result-ai2a-100')[0].click()")*/
            driver.switchTo().defaultContent()
            getListTenders(driver, wait)
            (1..CountPage).forEach { _ ->
                try {
                    parserPageN(driver, wait)
                } catch (e: Exception) {
                    logger("Error in parserE function", e.stackTrace, e)
                }
            }
            tendersS.forEach {
                try {
                    //println(it)
                    it.parsing()
                } catch (e: Exception) {
                    logger("error in TenderMosreg.parsing()", e.stackTrace, e, it.url)
                }
            }
        } catch (e: Exception) {
            logger("Error in parser function", e.stackTrace, e)
        } finally {
            driver.quit()
        }
    }

    private fun parserPageN(driver: ChromeDriver, wait: WebDriverWait) {
        driver.switchTo().defaultContent()
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(@class, 'paginator_last')  and  .  =  '>>']")))
        val js = driver as JavascriptExecutor
        js.executeScript("document.querySelectorAll('a.paginator_next')[0].click()")
        driver.switchTo().defaultContent()
        getListTenders(driver, wait)
    }

    private fun getListTenders(driver: ChromeDriver, wait: WebDriverWait) {
        Thread.sleep(2000)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'registerProcurement__resultSearch-blockResult')][10]")))
        val tenders = driver.findElements(By.xpath("//div[contains(@class, 'registerProcurement__resultSearch-blockResult')]"))
        tenders.forEach {
            try {
                parserTender(it)
            } catch (e: Exception) {
                logger("error in parserTender", e.stackTrace, e)
            }
        }
    }

    private fun parserTender(el: WebElement) {
        //driver.switchTo().defaultContent()
        val purNum = el.findElementWithoutException(By.xpath(".//h4[contains(@class, 'blockResult__leftContent-topLotNumber')]/span[@data-bind = 'text: Id']"))?.text?.trim { it <= ' ' }
                ?: ""
        if (purNum == "") {
            logger("can not purNum in tender")
            return
        }
        val urlT = el.findElementWithoutException(By.xpath(".//a[contains(@data-bind , 'text: TradeName')]"))?.getAttribute("href")?.trim { it <= ' ' }
                ?: ""
        if (urlT == "") {
            logger("can not urlT in tender", purNum)
            return
        }
        val url = urlT
        val purObj = el.findElementWithoutException(By.xpath(".//a[contains(@data-bind , 'text: TradeName')]"))?.text?.trim { it <= ' ' }
                ?: ""
        val datePubTmp = el.findElementWithoutException(By.xpath(".//span[. = 'Начало приема заявок:']/following-sibling::span"))?.text?.trim()?.trim { it <= ' ' }
                ?: ""
        val dateEndTmp = el.findElementWithoutException(By.xpath("..//span[. = 'Окончание приема заявок:']/following-sibling::span"))?.text?.trim()?.trim { it <= ' ' }
                ?: ""
        val datePub = getDateFromFormat(datePubTmp, formatterOnlyDate)
        val dateEnd = getDateFromFormat(dateEndTmp, formatterGpn)
        val status = el.findElementWithoutException(By.xpath(".//span[@data-bind = 'text: TradeStateName']"))?.text?.trim { it <= ' ' }
                ?: ""
        val nmck = el.findElementWithoutException(By.xpath(".//p[@class = 'blockResult__rightContent-price']"))?.text?.replace(',', '.')?.replace("\u20BD", "")?.deleteAllWhiteSpace()?.trim { it <= ' ' }
                ?: ""
        if (datePub == Date(0L) || dateEnd == Date(0L)) {
            logger("can not find pubDate or dateEnd on page", urlT, purNum)
            return
        }
        val tt = TenderMosreg(status, purNum, purObj, nmck, datePub, dateEnd, url)
        tendersS.add(tt)
    }
}