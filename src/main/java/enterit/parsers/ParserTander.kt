package enterit.parsers

import enterit.PassTander
import enterit.UserTander
import enterit.formatterOnlyDate
import enterit.tenders.TenderTander
import enterit.tools.getDateFromString
import enterit.tools.logger
import enterit.tools.regExpTest
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.util.*
import java.util.logging.Level


class ParserTander : Iparser {
    init {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog")
        java.util.logging.Logger.getLogger("org.openqa.selenium").level = Level.OFF
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver")
    }

    companion object WebCl {
        const val BaseUrl = "https://srm.tander.ru/uniapp#app=srm&token=9934904"
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
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='submit-btn']")))
            val usr = driver.findElementById("login-field")
            val pass = driver.findElementById("pwd-field")
            val inp = driver.findElementById("submit-btn")
            //Thread.sleep(10000)
            usr.sendKeys(UserTander)
            pass.sendKeys(PassTander)
            inp.submit()
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(self::node(), 'Период проведения')]")))
            val periods = driver.findElements(By.xpath("//tr[@height = '20']/td[@class = 'xs-table-cell']//div[contains(self::node(), 'Период проведения')]"))

            val titles = driver.findElements(By.xpath("//tr[@height = '20']/td[@class = 'xs-table-cell']//div[contains(self::node(), 'Сбор коммерческих предложений')]"))

            val hrefs = driver.findElements(By.xpath("//tr[@height = '1']/td[@class = 'xs-table-cell']//a[contains(self::node(), 'Скачать файл')]"))
            if (periods.size != titles.size || titles.size != hrefs.size) {
                logger("can not compare 3 list with data")
                return
            } else {
                try {
                    parserList(periods, titles, hrefs)
                } catch (e: Exception) {
                    logger("error in ${this::class.simpleName}.parserList()", e.stackTrace, e)
                }
            }
        } catch (e: Exception) {
            logger("Error in parser function", e.stackTrace, e)
        } finally {
            driver.quit()
        }


    }

    private fun parserList(periods: List<WebElement>, titles: List<WebElement>, hrefs: List<WebElement>) {
        for (i in 0..periods.lastIndex) {
            val purName = titles[i].text.trim { it <= ' ' }
            val datePubT = periods[i].text.trim({ it <= ' ' }).regExpTest("""с.*?(\d{2}.\d{2}.\d{4})""")
            val pubDate = datePubT.getDateFromString(formatterOnlyDate)
            val dateEndT = periods[i].text.trim({ it <= ' ' }).regExpTest("""по.*?(\d{2}.\d{2}.\d{4})""")
            val endDate = dateEndT.getDateFromString(formatterOnlyDate)
            val href = hrefs[i].getAttribute("href").trim { it <= ' ' }
            val purNum = href.regExpTest("""DFFILE_OBJ=(.*)""")
            if (purNum == "" || pubDate == Date(0L) || endDate == Date(0L)) {
                logger("can not find dates or purNum in tender")
                continue
            }
            val tt = TenderTander(purNum, href, purName, pubDate, endDate)
            try {
                tt.parsing()
            } catch (e: Exception) {
                logger("error in TenderTander.parsing()", e.stackTrace, e)
            }
        }

    }
}