package enterit.tools

import org.openqa.selenium.By
import org.openqa.selenium.SearchContext
import org.openqa.selenium.WebElement
import java.util.regex.Matcher
import java.util.regex.Pattern

fun <T> T.findElementWithoutException(by: By): WebElement?
        where T : SearchContext =
    try {
        this.findElement(by)
    } catch (e: Exception) {
        null
    }

fun <T> T.clickertWithoutException(
    by: By,
    count: Int = 5,
)
        where T : SearchContext {
    (1 until count).forEach { _ ->
        try {
            val el = this.findElement(by)
            el.click()
            return@forEach
        } catch (e: Exception) {
        }
    }
}

fun String.getDataFromRegexp(reg: String): String {
    var st = ""
    try {
        val pattern: Pattern = Pattern.compile(reg)
        val matcher: Matcher = pattern.matcher(this)
        if (matcher.find()) {
            st = matcher.group(1)
        }
    } catch (e: Exception) {
    }
    return st.trim { it <= ' ' }
}

fun String.deleteDoubleWhiteSpace(): String {
    val pattern: Pattern = Pattern.compile("""\s+""")
    val matcher: Matcher = pattern.matcher(this)
    var ss: String = matcher.replaceAll(" ")
    ss = ss.trim { it <= ' ' }
    return ss
}

fun String.deleteAllWhiteSpace(): String {
    val pattern: Pattern = Pattern.compile("""\s+""")
    val matcher: Matcher = pattern.matcher(this)
    var ss: String = matcher.replaceAll("")
    ss = ss.trim { it <= ' ' }
    return ss
}
