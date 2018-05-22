package enterit.tenders

import enterit.data_classes.SafmargT
import enterit.formatterGpn
import enterit.parsers.ParserSafmarg
import enterit.tools.findElementWithoutException
import enterit.tools.getDateFromString
import enterit.tools.logger
import enterit.tools.regExpTest
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.util.*

class TenderSafmarg(val tn: SafmargT<String>, val driver: ChromeDriver) : TenderAbstract(), ITender {
    companion object TypeFz {
        val typeFz = 37
    }

    init {
        etpName = "ЭТП ФГ САФМАР"
        etpUrl = "http://tender.safmargroup.ru/"
    }

    override fun parsing() {
        //driver.close()

        driver.get(tn.href)
        val wait = WebDriverWait(driver, ParserSafmarg.timeoutB)
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.xpath("//iframe")))
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//td[contains(preceding-sibling::td, 'Начало приема предложений')]//div[contains(@class, 'translate-text-')]")))
        //Thread.sleep(10000)
        //driver.switchTo().frame(0)
        val datePubT = driver.findElementWithoutException(By.xpath("//td[contains(preceding-sibling::td, 'Начало приема предложений')]//div[contains(@class, 'translate-text-')]"))?.text?.trim({ it <= ' ' })
                ?: ""
        val pubDate = datePubT.getDateFromString(formatterGpn)
        var endDateT = driver.findElementWithoutException(By.xpath("//td[contains(preceding-sibling::td, 'Окончание приема предложений')]//div[contains(@class, 'translate-text-')]"))?.text?.trim({ it <= ' ' })
                ?: ""
        println(endDateT)
        endDateT = endDateT.regExpTest("""(\d{2}.\d{2}.\d{4} \d{2}:\d{2})""")
        val endDate = endDateT.getDateFromString(formatterGpn)
        if (pubDate == Date(0L) || endDate == Date(0L)) {
            logger("can not find dates in tender", tn.href, datePubT, endDateT)
            return
        }
        val status = driver.findElementWithoutException(By.xpath("//td[contains(preceding-sibling::td, 'Статус торгов')]//div[contains(@class, 'translate-text-')]"))?.text?.trim({ it <= ' ' })


    }
}