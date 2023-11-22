package enterit.parsers

import enterit.PassSafmar
import enterit.UserSafmar
import enterit.dataclasses.SafmargT
import enterit.tenders.TenderSlaveco
import enterit.tools.findElementWithoutException
import enterit.tools.logger
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.PageLoadStrategy
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.util.logging.Level

class ParserSlaveco : Iparser {
    init {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog")
        java.util.logging.Logger.getLogger("org.openqa.selenium").level = Level.OFF
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver")
    }

    companion object WebCl {
        const val BaseUrl = "https://tender.slaveco.ru/trades?page=purchases.trades.search&documents=N4IgLgTghgJgpgZTlCBjAFgbQA4Fc3pQDOcRAdJLKWSShgLogBcoEpuANmALID28zUADcoHXHGYhUKGCAC%2BAGhC8I8CIJABLWUwBMSsJrAcJTEIBQQQAwggIRBLIJQFsoYAJKpeAO0koIvAO4A%2BjD%2BHn4y9iDYEJpOEACeAKIAHlGkRJqeGsSocB4wmh4A5swAZqIkSlG82HAQYHGSAEoJAOIuCAAqCc0AIgE9AIJd8kpwKWxE6Z5EzJig2bn5RaXlcJW%2BNXUNZs1tnd0JfYPDiqBVm%2FWSLj0RC3kFxUxlHCRy9HKK4NDwAGKaXLUAApQDxwDg4fAYYjUSjwci0AiMFggIhgZy4IgAYX4phAog4ERUag02mY%2BnARhMkistgiTlc7i8Zh8%2FiCITCqgiURiKES4zSGSZ8yIOXuyyeq3W1Vqlx2rXaXV6%2FSGCRGIDGqUmgpmTDmeJFiweKxea0iGxl2xAuwVByOKpGZ3NWyuNyUdyWj2er3eUpykwQaLAGNIs3eciAA"
        const val timeoutB = 60L
    }

    private val tendersS = mutableListOf<SafmargT<String>>()
    override fun parser() {
        val options = ChromeOptions()
        //options.addArguments("headless")
        options.addArguments("disable-gpu")
        options.addArguments("no-sandbox")
        options.addArguments("ignore-certificate-errors")
        options.addArguments("window-size=1920,1080")
        options.addArguments("disable-infobars")
        options.addArguments("lang=ru, ru-RU")
        options.addArguments("disable-blink-features=AutomationControlled")
        options.addArguments("disable-dev-shm-usage")
        options.addArguments("disable-browser-side-navigation")
        options.addArguments("start-maximized")
        options.setPageLoadStrategy(PageLoadStrategy.NONE)
        options.setAcceptInsecureCerts(true)
        val driver = ChromeDriver(options)
        try {
            driver.get(BaseUrl)
            val wait = WebDriverWait(driver, timeoutB)
            auth(wait, driver)
            driver.get(BaseUrl)
            addTenders(wait, driver)
            (1..20).forEach {
                try {
                    parserPageN(driver, wait)
                } catch (e: Exception) {
                    logger("Error in parser function", e.stackTrace, e)
                }
            }
            tendersS.forEach {
                try {
                    val t = TenderSlaveco(it, driver)
                    t.parsing()
                } catch (e: Exception) {
                    logger("error in TenderSlaveco.parsing()", e.stackTrace, e, it.href)
                }
            }
        } catch (e: Exception) {
            logger("Error in parser function", e.stackTrace, e)
        } finally {
            driver.quit()
        }
    }

    private fun auth(
        wait: WebDriverWait,
        driver: ChromeDriver
    ) {
        try {
            driver.switchTo().defaultContent()
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(., 'ДАЛЕЕ')]")))
            driver.switchTo().defaultContent()
            val usr = driver.findElement(By.xpath("//input[@formcontrolname = 'login']"))
            val pass = driver.findElement(By.xpath("//input[@formcontrolname = 'password']"))
            val inp = driver.findElement(By.xpath("//button[contains(., 'ДАЛЕЕ')]"))
            //Thread.sleep(10000)
            usr.sendKeys(UserSafmar)
            pass.sendKeys(PassSafmar)
            inp.click()
        } catch (e: Exception) {
            logger("Error in auth function", e.stackTrace, e)
        }
        Thread.sleep(5000)
        driver.switchTo().defaultContent()
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