package enterit.tenders

import enterit.dataclasses.UgmkT
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.WebDriverWait

class TenderUgmk(val tt: UgmkT, val driver: ChromeDriver, val wait: WebDriverWait) : TenderAbstract(), ITender {
    companion object TypeFz {
        val typeFz = 62
    }

    init {
        etpName = "\"УГМК-Холдинг\""
        etpUrl = "https://zakupki.ugmk.com/"
    }

    override fun parsing() {
        println(tt)
    }
}