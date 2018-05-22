package enterit.parsers

import enterit.data_classes.SafmargT
import enterit.tenders.TenderSafmarg
import enterit.tools.findElementWithoutException
import enterit.tools.logger
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.util.logging.Level

class ParserSafmarg : Iparser {
    init {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog")
        java.util.logging.Logger.getLogger("org.openqa.selenium").level = Level.OFF
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver")
    }

    companion object WebCl {
        const val BaseUrl = "http://tender.safmargroup.ru/trades?page=trades"
        const val timeoutB = 120L
    }

    private val tendersS = mutableListOf<SafmargT<String>>()
    override fun parser() {
        val options = ChromeOptions()
        options.addArguments("headless")
        options.addArguments("disable-gpu")
        options.addArguments("no-sandbox")
        val driver = ChromeDriver(options)
        try {
            driver.get(BaseUrl)
            val wait = WebDriverWait(driver, timeoutB)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@ng-transclude = 'tradeListItem']")))
            val tenders = driver.findElements(By.xpath("//div[@ng-transclude = 'tradeListItem']"))
            tenders.forEach { addToList(it) }
            tendersS.forEach {
                try {
                    val t = TenderSafmarg(it, driver)
                    t.parsing()
                } catch (e: Exception) {
                    logger("error in TenderSafmarg.parsing()", e.stackTrace, e, it.href)
                }
            }
        } catch (e: Exception) {
            logger("Error in parser function", e.stackTrace, e)
        } finally {
            driver.quit()
        }
    }

    private fun addToList(el: WebElement) {
        val purNum = el.findElementWithoutException(By.xpath(".//span[contains(@class, 'registered-number')]"))?.text?.trim({ it <= ' ' })
                ?: ""
        if (purNum == "") {
            logger("can not find dates or purNum in tender")
            return
        }
        val href = el.findElementWithoutException(By.xpath(".//span[contains(@class, 'header-title')]//a"))?.getAttribute("href")?.trim({ it <= ' ' })
                ?: ""
        val tn = SafmargT(purNum, href)
        tendersS.add(tn)
    }
}