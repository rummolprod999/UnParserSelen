package enterit.parsers

import enterit.dataclasses.SafmargT
import enterit.tenders.TenderEuroTrans
import enterit.tools.findElementWithoutException
import enterit.tools.logger
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.util.logging.Level

class ParserEuroTrans : Iparser {
    init {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog")
        java.util.logging.Logger.getLogger("org.openqa.selenium").level = Level.OFF
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver")
    }

    companion object WebCl {
        const val BaseUrl = "https://tender.eurotransstroy.ru/trades"
        const val timeoutB = 60L
    }

    private val tendersS = mutableListOf<SafmargT<String>>()
    override fun parser() {
        val options = ChromeOptions()
        options.addArguments("headless")
        options.addArguments("disable-gpu")
        options.addArguments("no-sandbox")
        options.addArguments("ignore-certificate-errors")
        options.addArguments("window-size=1920,1080")
        options.setAcceptInsecureCerts(true)
        val driver = ChromeDriver(options)
        try {
            driver.get(BaseUrl)
            val wait = WebDriverWait(driver, timeoutB)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'mat-button-toggle-3-button']")))
                .click()
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//um-trade-search-card/um-card")))
            val tenders = driver.findElements(By.xpath("//um-trade-search-card/um-card"))
            tenders.forEach { addToList(it) }
            tendersS.forEach {
                try {
                    val t = TenderEuroTrans(it, driver)
                    t.parsing()
                } catch (e: Exception) {
                    logger("error in TenderEuroTrans.parsing()", e.stackTrace, e, it.href)
                }
            }
        } catch (e: Exception) {
            logger("Error in parser function", e.stackTrace, e)
        } finally {
            driver.quit()
        }
    }

    private fun addToList(el: WebElement) {
        val purNum =
            el.findElementWithoutException(By.xpath(".//div[contains(@class, 'trade-number')]"))?.text?.trim { it <= ' ' }
                ?: ""
        if (purNum == "") {
            logger("cannot find dates or purNum in tender")
            return
        }
        val href = el.findElementWithoutException(By.xpath(".//a[contains(@class, 'trade-title')]"))
            ?.getAttribute("href")?.trim { it <= ' ' }
            ?: ""
        val tn = SafmargT(purNum, href, "")
        tendersS.add(tn)
    }
}