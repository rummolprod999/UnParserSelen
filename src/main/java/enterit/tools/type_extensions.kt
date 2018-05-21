package enterit.tools

import org.openqa.selenium.By
import org.openqa.selenium.WebElement

fun WebElement.findElementWithoutException(by: By): WebElement? {
    return try {
        this.findElement(by)
    } catch (e: Exception) {
        null
    }
}
