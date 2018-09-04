package enterit.parsers

import enterit.dataclasses.TalanT
import enterit.formatterGpn
import enterit.tenders.TenderTalan
import enterit.tools.findElementWithoutException
import enterit.tools.getDateFromString
import enterit.tools.logger
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.util.*
import java.util.logging.Level

class ParserTalan : Iparser {
    init {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog")
        java.util.logging.Logger.getLogger("org.openqa.selenium").level = Level.OFF
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver")
    }

    companion object WebCl {
        const val BaseUrl = "http://тендеры.талан.рф/Tenders#page="
        const val timeoutB = 120L
    }

    override fun parser() {
        val options = ChromeOptions()
        options.addArguments("headless")
        options.addArguments("disable-gpu")
        options.addArguments("no-sandbox")
        val driver = ChromeDriver(options)
        try {
            for (i in 1..5) {
                val urlT = "$BaseUrl$i"
                try {
                    parserList(urlT, driver)
                } catch (e: Exception) {
                    logger("Error in parserList function", e.stackTrace, e)
                }
            }

        } catch (e: Exception) {
            logger("Error in parser function", e.stackTrace, e)
        } finally {
            driver.quit()
        }
    }

    private fun parserList(urlT: String, driver: ChromeDriver) {
        driver.get(urlT)
        driver.switchTo().defaultContent()
        val wait = WebDriverWait(driver, timeoutB)
        Thread.sleep(15000)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@aria-describedby = 'grid_TenderGridViewModel_info']/tbody/tr[contains(@class, 'notoggle') and @id][10]")))
        val tenders = driver.findElements(By.xpath("//table[@aria-describedby = 'grid_TenderGridViewModel_info']/tbody/tr[contains(@class, 'notoggle') and @id]"))
        tenders.forEach {
            try {
                parserTender(it)
            } catch (e: Exception) {
                logger("error in parserTender", e.stackTrace, e, urlT)
            }
        }
    }

    private fun parserTender(el: WebElement) {
        val purNum = el.findElementWithoutException(By.xpath(".//td[1]/a"))?.text?.trim { it <= ' ' }
                ?: ""
        //println(purNum)
        if (purNum == "") {
            logger("can not find purNum in tender")
            return
        }
        val hrefL = el.findElementWithoutException(By.xpath(".//td[1]/a"))?.getAttribute("href")?.trim({ it <= ' ' })
                ?: ""
        val hrefT = el.findElementWithoutException(By.xpath(".//td[2]/a"))?.getAttribute("href")?.trim({ it <= ' ' })
                ?: ""
        if (hrefL == "" || hrefT == "") {
            logger("can not find hrefs in tender", purNum)
            return
        }
        val purName = el.findElementWithoutException(By.xpath(".//td[2]/a"))?.text?.trim({ it <= ' ' })
                ?: ""
        val datePubT = el.findElementWithoutException(By.xpath(".//td[5]"))?.text?.trim({ it <= ' ' })
                ?: ""
        val pubDate = datePubT.getDateFromString(formatterGpn)
        val endDateT = el.findElementWithoutException(By.xpath(".//td[6]"))?.text?.trim({ it <= ' ' })
                ?: ""
        val endDate = endDateT.getDateFromString(formatterGpn)
        if (pubDate == Date(0L) && endDate == Date(0L)) {
            logger("can not find dates in tender", hrefL, datePubT, endDateT)
            return
        }
        val status = el.findElementWithoutException(By.xpath(".//td[8]"))?.text?.trim({ it <= ' ' })
                ?: ""
        val placingWayName = el.findElementWithoutException(By.xpath(".//td[9]"))?.text?.trim({ it <= ' ' })
                ?: ""
        val urlOrg = el.findElementWithoutException(By.xpath(".//td[7]/a"))?.getAttribute("href")?.trim({ it <= ' ' })
                ?: ""
        val tn = TalanT(purNum, hrefT, hrefL, purName, pubDate, endDate, status, placingWayName, urlOrg)
        try {
            val t = TenderTalan(tn)
            t.parsing()
        } catch (e: Exception) {
            logger("error in TenderTalan.parsing()", e.stackTrace, e, hrefT)
        }
    }
}