package enterit.parsers

import enterit.dataclasses.SibPrimeT
import enterit.formatterGpn
import enterit.tenders.TenderSibPrime
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

class ParserSibPrime : Iparser {
    init {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog")
        java.util.logging.Logger.getLogger("org.openqa.selenium").level = Level.OFF
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver")
    }

    companion object WebCl {
        const val BaseUrl = "http://sibprime.ru/Tenders#page="
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
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@aria-describedby = 'grid_TenderGridViewModel_info']/tbody/tr[contains(@role, 'row') and @id][10]")))
        val tenders =
            driver.findElements(By.xpath("//table[@aria-describedby = 'grid_TenderGridViewModel_info']/tbody/tr[contains(@role, 'row') and @id]"))
        for ((index, value) in tenders.withIndex()) {
            try {
                parserTender(value, index + 1)
            } catch (e: Exception) {
                logger("error in parserTender", e.stackTrace, e, urlT)
            }
        }

    }

    private fun parserTender(el: WebElement, ind: Int) {
        val purNum = el.findElementWithoutException(By.xpath(".//td[2]"))?.text?.trim { it <= ' ' }
            ?: ""
        if (purNum == "") {
            logger("cannot find purNum in tender")
            return
        }
        val hrefL = el.findElementWithoutException(By.xpath(".//td[3]/a"))?.getAttribute("href")?.trim { it <= ' ' }
            ?: ""
        val hrefT = el.findElementWithoutException(By.xpath(".//td[1]/a"))?.getAttribute("href")?.trim { it <= ' ' }
            ?: ""
        if (hrefL == "" || hrefT == "") {
            logger("cannot find hrefs in tender", purNum)
            return
        }
        val purName = el.findElementWithoutException(By.xpath(".//td[1]/a"))?.text?.trim { it <= ' ' }
            ?: ""
        var datePubT = el.findElementWithoutException(By.xpath(".//td[5]"))?.text?.trim { it <= ' ' }
            ?: ""
        if (datePubT == "") {
            datePubT =
                el.findElementWithoutException(By.xpath("//table[@aria-describedby = 'grid_TenderGridViewModel_info']/tbody/tr[contains(@class, 'child')][$ind]//span[contains(., 'Начало приема заявок:')]/following-sibling::span"))?.text?.trim { it <= ' ' }
                    ?: ""
        }
        val pubDate = datePubT.getDateFromString(formatterGpn)
        var endDateT = el.findElementWithoutException(By.xpath(".//td[6]"))?.text?.trim { it <= ' ' }
            ?: ""
        if (endDateT == "") {
            endDateT =
                el.findElementWithoutException(By.xpath("//table[@aria-describedby = 'grid_TenderGridViewModel_info']/tbody/tr[contains(@class, 'child')][$ind]//span[contains(., 'Окончание приема заявок:')]/following-sibling::span"))?.text?.trim { it <= ' ' }
                    ?: ""
        }
        val endDate = endDateT.getDateFromString(formatterGpn)
        if (pubDate == Date(0L) && endDate == Date(0L)) {
            logger("cannot find dates in tender", hrefL, datePubT, endDateT)
            return
        }
        var status = el.findElementWithoutException(By.xpath(".//td[9]"))?.text?.trim { it <= ' ' }
            ?: ""
        if (status == "") {
            status =
                el.findElementWithoutException(By.xpath("//table[@aria-describedby = 'grid_TenderGridViewModel_info']/tbody/tr[contains(@class, 'child')][$ind]//span[contains(., 'Статус лота:')]/following-sibling::span"))?.text?.trim { it <= ' ' }
                    ?: ""
        }
        var placingWayName = el.findElementWithoutException(By.xpath(".//td[8]"))?.text?.trim { it <= ' ' }
            ?: ""
        if (placingWayName == "") {
            placingWayName =
                el.findElementWithoutException(By.xpath("//table[@aria-describedby = 'grid_TenderGridViewModel_info']/tbody/tr[contains(@class, 'child')][$ind]//span[contains(., 'Способ проведения:')]/following-sibling::span"))?.text?.trim { it <= ' ' }
                    ?: ""
        }
        var nameCus = el.findElementWithoutException(By.xpath(".//td[7]"))?.text?.trim { it <= ' ' }
            ?: ""
        if (nameCus == "") {
            nameCus =
                el.findElementWithoutException(By.xpath("//table[@aria-describedby = 'grid_TenderGridViewModel_info']/tbody/tr[contains(@class, 'child')][$ind]//span[contains(., 'Заказчик:')]/following-sibling::span"))
                    ?.getAttribute("href")?.trim { it <= ' ' }
                    ?: ""
        }
        val tn = SibPrimeT(purNum, hrefT, hrefL, purName, pubDate, endDate, status, placingWayName, nameCus)
        try {
            val t = TenderSibPrime(tn)
            t.parsing()
        } catch (e: Exception) {
            logger("error in TenderSibPrime.parsing()", e.stackTrace, e, hrefT)
        }
    }
}