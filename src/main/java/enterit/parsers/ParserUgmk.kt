package enterit.parsers

import enterit.dataclasses.SafmargT
import enterit.tenders.TenderUgmk
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

class ParserUgmk : Iparser {
    init {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog")
        java.util.logging.Logger.getLogger("org.openqa.selenium").level = Level.OFF
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver")
    }

    companion object WebCl {
        const val BaseUrl = "https://etp.ugmk.com/trades?page=purchases"
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
            Thread.sleep(5000)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'mat-button-toggle-3-button']")))
            driver.switchTo().defaultContent()
            val js = driver as JavascriptExecutor
            js.executeScript("document.querySelectorAll('#mat-button-toggle-3-button')[0].click()")
            addTenders(wait, driver)
            (1..10).forEach {
                try {
                    parserPageN(driver, wait)
                } catch (e: Exception) {
                    logger("Error in parser function", e.stackTrace, e)
                }
            }
            tendersS.forEach {
                try {
                    val t = TenderUgmk(it, driver, wait)
                    t.parsing()
                } catch (e: Exception) {
                    logger("error in TenderUgmk.parsing()", e.stackTrace, e, it.href)
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
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button.mat-button-base.mat-icon-button.mat-primary")))
        Thread.sleep(5000)
        driver.switchTo().defaultContent()
        val js = driver as JavascriptExecutor
        js.executeScript("document.querySelectorAll('button.mat-button-base.mat-icon-button.mat-primary')[1].click()")
        Thread.sleep(3000)
        driver.switchTo().defaultContent()
        addTenders(wait, driver)
    }

    private fun addTenders(
        wait: WebDriverWait,
        driver: ChromeDriver
    ) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//um-trade-search-card/um-card")))
        val tenders = driver.findElements(By.xpath("//um-trade-search-card/um-card"))
        tenders.forEach {
            try {
                addToList(it)
            } catch (e: Exception) {
                logger("Error in parser function", e.stackTrace, e)
            }
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
