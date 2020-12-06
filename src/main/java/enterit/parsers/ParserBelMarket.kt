package enterit.parsers

import enterit.formatterGpn
import enterit.tenders.TenderBelMarket
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

class ParserBelMarket : Iparser {
    init {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog")
        java.util.logging.Logger.getLogger("org.openqa.selenium").level = Level.OFF
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver")
    }

    companion object WebCl {
        const val BaseUrl = "https://belgorodmarket-app.rts-tender.ru/"
        const val timeoutB = 120L
        const val CountPage = 20
    }

    private val tendersS = mutableListOf<TenderBelMarket>()
    override fun parser() {
        val options = ChromeOptions()
        options.addArguments("headless")
        options.addArguments("disable-gpu")
        options.addArguments("no-sandbox")
        val driver = ChromeDriver(options)
        try {
            driver.manage().timeouts().pageLoadTimeout(timeoutB, TimeUnit.SECONDS)
            driver.manage().deleteAllCookies()
            driver.get(BaseUrl)
            driver.switchTo().defaultContent()
            //driver.manage().window().maximize()
            val wait = WebDriverWait(driver, timeoutB)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@id = 'jqGrid']/tbody/tr[not(@class = 'jqgfirstrow')][10]")))
            /*val paginator = driver.findElement(By.xpath("//div[@class = 'wg-selectbox']/div[@class = 'select']"))
            paginator.click()
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'wg-selectbox']/ul/li[. = '100']")))
            val count100 = driver.findElement(By.xpath("//div[@class = 'wg-selectbox']/div[@class = 'select']"))
            count100.click()
            driver.switchTo().defaultContent()*/
            val js = driver as JavascriptExecutor
            js.executeScript("document.querySelectorAll('div.wg-selectbox div.select')[0].click()")
            js.executeScript("document.querySelectorAll('div.wg-selectbox ul li:last-child')[0].click()")
            driver.switchTo().defaultContent()
            getListTenders(driver, wait)
            (1..CountPage).forEach {
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

    private fun getListTenders(driver: ChromeDriver, wait: WebDriverWait) {
        Thread.sleep(5000)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@id = 'jqGrid']/tbody/tr[not(@class = 'jqgfirstrow')][10]")))
        val tenders = driver.findElements(By.xpath("//table[@id = 'jqGrid']/tbody/tr[not(@class = 'jqgfirstrow')]"))
        tenders.forEach {
            try {
                parserTender(it)
            } catch (e: Exception) {
                logger("error in parserTender", e.stackTrace, e)
            }
        }
    }

    private fun parserTender(el: WebElement) {
        val purNum = el.findElementWithoutException(By.xpath("./td[2]/p"))?.text?.trim { it <= ' ' }
            ?: ""
        if (purNum == "") {
            logger("can not purNum in tender")
            return
        }
        val urlT = el.findElementWithoutException(By.xpath("./td[4]/a"))?.getAttribute("href")?.trim { it <= ' ' }
            ?: ""
        if (urlT == "") {
            logger("can not urlT in tender", purNum)
            return
        }
        val url = urlT
        val purObj = el.findElementWithoutException(By.xpath("./td[4]/a"))?.text?.trim { it <= ' ' }
            ?: ""
        val datePubTmp = el.findElementWithoutException(By.xpath("./td[6]/span"))?.text?.trim()?.trim { it <= ' ' }
            ?: ""
        val dateEndTmp = el.findElementWithoutException(By.xpath("./td[7]/span"))?.text?.trim()?.trim { it <= ' ' }
            ?: ""
        val datePub = getDateFromFormat(datePubTmp, formatterGpn)
        val dateEnd = getDateFromFormat(dateEndTmp, formatterGpn)
        val status = el.findElementWithoutException(By.xpath("./td[9]"))?.text?.trim { it <= ' ' } ?: ""
        val nmck = el.findElementWithoutException(By.xpath("./td[5]"))?.text?.replace(',', '.')?.deleteAllWhiteSpace()
            ?.trim { it <= ' ' }
            ?: ""
        if (datePub == Date(0L) || dateEnd == Date(0L)) {
            logger("can not find pubDate or dateEnd on page", urlT, purNum)
            return
        }
        val tt = TenderBelMarket(status, purNum, purObj, nmck, datePub, dateEnd, url)
        tendersS.add(tt)
    }

    private fun parserPageN(driver: ChromeDriver, wait: WebDriverWait) {
        driver.switchTo().defaultContent()
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'paginator__page-selector']/a[contains(@class, 'paginator__next')]")))
        //val pageNum = driver.findElement(By.xpath("//div[@class = 'paginator__page-selector']/a[contains(@class, 'paginator__next')]"))
        //pageNum.click()
        val js = driver as JavascriptExecutor
        js.executeScript("document.getElementsByClassName('paginator__next')[0].click()")
        driver.switchTo().defaultContent()
        getListTenders(driver, wait)
    }
}