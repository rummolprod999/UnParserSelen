package enterit.parsers

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
        } catch (e: Exception) {
            logger("Error in parser function", e.stackTrace, e)
        } finally {
            driver.quit()
        }
    }

    private fun addToList(el: WebElement) {
        val purNum = el.findElement(By.xpath(".//span[contains(@class, 'registered-number')]")).text.trim({ it <= ' ' })
        println(purNum)
    }
}