package enterit

import enterit.parsers.ParserSafmarg
import enterit.parsers.ParserTander
import enterit.tools.logger

fun main(args: Array<String>) {
    init(args)
    when (arg) {
        Arguments.TANDER -> parserTander()
        Arguments.SAFMARG -> parserSafmarg()
    }

}

fun parserTander() {
    logger("Начало парсинга")
    val p = ParserTander()
    var tr = 0
    while (true) {
        try {
            p.parser()
            break
        } catch (e: Exception) {
            tr++
            if (tr > 1) {
                logger("Количество попыток истекло, выходим из программы")
                break
            }
            logger("Error in parserTander function", e.stackTrace, e)
            e.printStackTrace()
        }
    }
    logger("Добавили тендеров $AddTenderTander")
    logger("Конец парсинга")
}

fun parserSafmarg() {
    logger("Начало парсинга")
    val p = ParserSafmarg()
    var tr = 0
    while (true) {
        try {
            p.parser()
            break
        } catch (e: Exception) {
            tr++
            if (tr > 4) {
                logger("Количество попыток истекло, выходим из программы")
                break
            }
            logger("Error in parserSafmarg function", e.stackTrace, e)
            e.printStackTrace()
        }
    }
    logger("Добавили тендеров $AddTenderSafmarg")
    logger("Конец парсинга")
}