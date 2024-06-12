package enterit.parsers

import enterit.dataclasses.SafmargT
import enterit.tenders.TenderTsm
import enterit.tools.findElementWithoutException
import enterit.tools.logger
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.util.logging.Level

class ParserTsm : Iparser {
    init {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog")
        java.util.logging.Logger
            .getLogger("org.openqa.selenium")
            .level = Level.OFF
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver")
    }

    companion object WebCl {
        const val BaseUrl = "https://tender.tsm.ru/trades?page=purchases"
        const val timeoutB = 60L
    }

    private val tendersS = mutableListOf<SafmargT<String>>()

    override fun parser() {
        val options = ChromeOptions()
        options.addArguments("headless")
        options.addArguments("disable-gpu")
        options.addArguments("no-sandbox")
        options.addArguments("ignore-certificate-errors")
        options.setAcceptInsecureCerts(true)
        val driver = ChromeDriver(options)
        try {
            driver.get(BaseUrl)
            val wait = WebDriverWait(driver, timeoutB)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//um-trade-search-card/um-card")))
            val tenders = driver.findElements(By.xpath("//um-trade-search-card/um-card"))
            tenders.forEach { addToList(it) }
            repeat((1..3).count()) {
                try {
                    parserPageN(driver, wait)
                } catch (e: Exception) {
                    logger("Error in parser function", e.stackTrace, e)
                }
            }
            tendersS.forEach {
                try {
                    val t = TenderTsm(it, driver)
                    t.parsing()
                } catch (e: Exception) {
                    logger("error in TenderRhTorg.parsing()", e.stackTrace, e, it.href)
                }
            }
        } catch (e: Exception) {
            logger("Error in parser function", e.stackTrace, e)
        } finally {
            driver.quit()
        }
    }

    private fun getTenderList(
        wait: WebDriverWait,
        driver: ChromeDriver,
    ) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//um-trade-search-card/um-card")))
        driver.switchTo().defaultContent()
        val tenders = driver.findElements(By.xpath("//um-trade-search-card/um-card"))
        tenders.forEach {
            try {
                addToList(it)
            } catch (e: Exception) {
                logger("Error in parser function", e.stackTrace, e)
            }
        }
    }

    private fun parserPageN(
        driver: ChromeDriver,
        wait: WebDriverWait,
    ) {
        driver.switchTo().defaultContent()
        Thread.sleep(5000)
        driver.switchTo().defaultContent()
        val js = driver as JavascriptExecutor
        js.executeScript("document.querySelectorAll('button.mat-focus-indicator.mat-primary.mat-icon-button')[1].click()")
        Thread.sleep(3000)
        driver.switchTo().defaultContent()
        getTenderList(wait, driver)
    }

    private fun addToList(el: WebElement) {
        val purNum =
            el.findElementWithoutException(By.xpath(".//div[contains(@class, 'trade-number')]"))?.text?.trim { it <= ' ' }
                ?: ""
        if (purNum == "") {
            logger("cannot find dates or purNum in tender")
            return
        }
        val href =
            el
                .findElementWithoutException(By.xpath(".//a[contains(@class, 'trade-title')]"))
                ?.getAttribute("href")
                ?.trim { it <= ' ' }
                ?: ""
        val purName =
            el.findElementWithoutException(By.xpath(".//a[contains(@class, 'trade-title')]"))?.text?.trim { it <= ' ' }
                ?: ""
        val tn = SafmargT(purNum, href, purName)
        tendersS.add(tn)
    }
}
