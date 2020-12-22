package enterit.parsers

import enterit.dataclasses.UgmkT
import enterit.formatterOnlyDate
import enterit.tenders.TenderUgmk
import enterit.tools.findElementWithoutException
import enterit.tools.getDateFromFormat
import enterit.tools.logger
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.Level

class ParserUgmk : Iparser {
    init {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog")
        java.util.logging.Logger.getLogger("org.openqa.selenium").level = Level.OFF
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver")
    }

    companion object WebCl {
        const val BaseUrl = "https://zakupki.ugmk.com/"
        const val timeoutB = 120L
    }

    private val tendersS = mutableListOf<UgmkT>()
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
            val wait = WebDriverWait(driver, timeoutB)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'grid-canvas']//div[contains(@class, 'slick-row')]")))
            val js = driver as JavascriptExecutor
            js.executeScript("""document.querySelectorAll('div[title*="Дата окончания приема заявок"]')[0].click()""")
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'grid-canvas']//div[contains(@class, 'slick-row')]")))
            js.executeScript("""document.querySelectorAll('div[title*="Дата окончания приема заявок"]')[0].click()""")
            Thread.sleep(5000)
            (1..50).forEach {
                try {
                    //Thread.sleep(5000)
                    driver.switchTo().defaultContent()
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'grid-canvas']//div[contains(@class, 'slick-row')][18]/div[7]")))
                    getListTenders(driver, wait)
                    val jse = driver as JavascriptExecutor
                    jse.executeScript("document.querySelectorAll('div.slick-viewport')[0].scrollTop += 10;")
                } catch (e: Exception) {
                    logger("getListTenders", e.stackTrace, e)
                }
            }

            tendersS.forEach {
                try {
                    //println(it)
                    TenderUgmk(it, driver, wait).parsing()
                } catch (e: Exception) {
                    logger("error in TenderUgmk.parsing()", e.stackTrace, e, it.hrefT)
                }
            }
        } catch (e: Exception) {
            logger("Error in parser function", e.stackTrace, e)
        } finally {
            driver.quit()
        }
    }

    private fun getListTenders(driver: ChromeDriver, wait: WebDriverWait) {
        driver.switchTo().defaultContent()
        //Thread.sleep(1000)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'grid-canvas']//div[contains(@class, 'slick-row')][18]/div[7]")))
        (1..18).forEach { wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'grid-canvas']//div[contains(@class, 'slick-row')][$it]/div[contains(., 'T-') and position() = 1]"))) }

        val tenders = driver.findElements(By.xpath("//div[@class = 'grid-canvas']//div[contains(@class, 'slick-row')]"))
        tenders.forEach {
            driver.switchTo().defaultContent()
            //Thread.sleep(2000)
            val purNum = it.findElementWithoutException(By.xpath("./div[1]"))?.text?.trim { it <= ' ' }
                ?: ""
            if (purNum == "") {
                logger("cannot purNum in tender")
                return@forEach
            }
            //purNum = purNum.regExpTest("""(.+)/""")
            if (tendersS.any { it.purNum == purNum }) return@forEach
            val urlT = it.findElementWithoutException(By.xpath("./div[2]/a"))?.getAttribute("href")?.trim { it <= ' ' }
                ?: ""
            if (urlT == "") {
                logger("cannot urlT in tender", purNum)
                return@forEach
            }
            val urlL = it.findElementWithoutException(By.xpath("./div[4]/a"))?.getAttribute("href")?.trim { it <= ' ' }
                ?: ""
            if (urlL == "") {
                logger("cannot urlL in tender", purNum)
                return@forEach
            }
            val purObj = it.findElementWithoutException(By.xpath("./div[2]/a"))?.text?.trim { it <= ' ' }
                ?: ""
            val dateEndTmp = it.findElementWithoutException(By.xpath("./div[5]"))?.text?.trim()?.trim { it <= ' ' }
                ?: ""
            val dateEnd = getDateFromFormat(dateEndTmp, formatterOnlyDate)
            if (dateEnd == Date(0L)) {
                logger("cannot find dateEnd on page", urlT, purNum)
                return@forEach
            }
            val status = it.findElementWithoutException(By.xpath("./div[6]"))?.text?.trim { it <= ' ' } ?: ""
            val pWay = it.findElementWithoutException(By.xpath("./div[3]"))?.text?.trim { it <= ' ' } ?: ""
            val cusName = it.findElementWithoutException(By.xpath("./div[7]"))?.text?.trim { it <= ' ' } ?: ""
            val tt = UgmkT(purNum, urlT, urlL, purObj, dateEnd, status, pWay, cusName)
            tendersS.add(tt)
        }

    }
}