package enterit

import enterit.parsers.*
import enterit.tools.logger

fun main(args: Array<String>) {
    init(args)
    when (arg) {
        Arguments.TANDER -> parserTander()
        Arguments.SAFMARG -> parserSafmarg()
        Arguments.TALAN -> parserTalan()
        Arguments.MVIDEO -> parserMvideo()
        Arguments.MOSREG -> parserMosreg()
        Arguments.UGMK -> parserUgmk()
        Arguments.IMPTORGOV -> parserImpTorgov()
        Arguments.SIBPRIME -> parserSibPrime()
        Arguments.CRIMEABT -> parserCrimeaBt()
        Arguments.BELMARKET -> parserBelMarket()
        Arguments.BICO -> parserBico()
        Arguments.ROSTOV -> parserRostov()
        Arguments.SIMFEROP -> parserSimferop()
        Arguments.KOSTROMA -> parserKostroma()
        Arguments.TOMSK -> parserTomsk()
        Arguments.ZMO -> parserZmo()
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
    logger("Обновили тендеров $UpdateTenderTander")
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
    logger("Обновили тендеров $UpdateTenderSafmarg")
    logger("Конец парсинга")
}

fun parserTalan() {
    logger("Начало парсинга")
    val p = ParserTalan()
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
            logger("Error in parserTalan function", e.stackTrace, e)
            e.printStackTrace()
        }
    }
    logger("Добавили тендеров $AddTenderTalan")
    logger("Обновили тендеров $UpdateTenderTalan")
    logger("Конец парсинга")
}

fun parserMvideo() {
    logger("Начало парсинга")
    val p = ParserMvideo()
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
            logger("Error in parserMvideo function", e.stackTrace, e)
            e.printStackTrace()
        }
    }
    logger("Добавили тендеров $AddTenderMvideo")
    logger("Обновили тендеров $UpdateTenderMvideo")
    logger("Конец парсинга")
}

fun parserMosreg() {
    logger("Начало парсинга")
    val p = ParserMosreg()
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
            logger("Error in parserMosreg function", e.stackTrace, e)
            e.printStackTrace()
        }
    }
    logger("Добавили тендеров $AddTenderMosreg")
    logger("Обновили тендеров $UpdateTenderMosreg")
    logger("Конец парсинга")
}

fun parserUgmk() {
    logger("Начало парсинга")
    val p = ParserUgmk()
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
            logger("Error in parserUgmk function", e.stackTrace, e)
            e.printStackTrace()
        }
    }
    logger("Добавили тендеров $AddTenderUgmk")
    logger("Обновили тендеров $UpdateTenderUgmk")
    logger("Конец парсинга")
}

fun parserImpTorgov() {
    logger("Начало парсинга")
    val p = ParserImpTorgov()
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
            logger("Error in parserImpTorgov function", e.stackTrace, e)
            e.printStackTrace()
        }
    }
    logger("Добавили тендеров $AddTenderImpTorgov")
    logger("Обновили тендеров $UpdateTenderImpTorgov")
    logger("Конец парсинга")
}

fun parserSibPrime() {
    logger("Начало парсинга")
    val p = ParserSibPrime()
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
            logger("Error in parserSibPrime function", e.stackTrace, e)
            e.printStackTrace()
        }
    }
    logger("Добавили тендеров $AddTenderSibPrime")
    logger("Обновили тендеров $UpdateTenderSibPrime")
    logger("Конец парсинга")
}

fun parserCrimeaBt() {
    logger("Начало парсинга")
    val p = ParserCrimeaBt()
    p.parser()
    logger("Добавили тендеров $AddTenderCrimeaBt")
    logger("Обновили тендеров $UpdateTenderCrimeaBt")
    logger("Конец парсинга")
}

fun parserBelMarket() {
    logger("Начало парсинга")
    val p = ParserBelMarket()
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
            logger("Error in parserBelMarket function", e.stackTrace, e)
            e.printStackTrace()
        }
    }
    logger("Добавили тендеров $AddTenderBelMarket")
    logger("Обновили тендеров $UpdateTenderBelMarket")
    logger("Конец парсинга")
}

fun parserBico() {
    logger("Начало парсинга")
    val p = ParserBicoMultiThread()
    p.parser()
    logger("Добавили тендеров $AddTenderBico")
    logger("Обновили тендеров $UpdateTenderBico")
    logger("Конец парсинга")
}

fun parserRostov() {
    logger("Начало парсинга")
    val p = ParserRostov()
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
            logger("Error in parserRostov function", e.stackTrace, e)
            e.printStackTrace()
        }
    }
    logger("Добавили тендеров $AddTenderRostov")
    logger("Обновили тендеров $UpdateTenderRostov")
    logger("Конец парсинга")
}

fun parserSimferop() {
    logger("Начало парсинга")
    val p = ParserSimferop()
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
            logger("Error in parserSimferop function", e.stackTrace, e)
            e.printStackTrace()
        }
    }
    logger("Добавили тендеров $AddTenderSimferop")
    logger("Обновили тендеров $UpdateTenderSimferop")
    logger("Конец парсинга")
}

fun parserKostroma() {
    logger("Начало парсинга")
    val p = ParserKostroma()
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
            logger("Error in parserKostroma function", e.stackTrace, e)
            e.printStackTrace()
        }
    }
    logger("Добавили тендеров $AddTenderKostroma")
    logger("Обновили тендеров $UpdateTenderKostroma")
    logger("Конец парсинга")
}

fun parserTomsk() {
    logger("Начало парсинга")
    val p = ParserTomsk()
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
            logger("Error in parserTomsk function", e.stackTrace, e)
            e.printStackTrace()
        }
    }
    logger("Добавили тендеров $AddTenderTomsk")
    logger("Обновили тендеров $UpdateTenderTomsk")
    logger("Конец парсинга")
}

fun parserZmo() {
    logger("Начало парсинга")
    val p = ParserZmo()
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
            logger("Error in parserZmo function", e.stackTrace, e)
            e.printStackTrace()
        }
    }
    logger("Добавили тендеров $AddTenderZmo")
    logger("Обновили тендеров $UpdateTenderZmo")
    logger("Конец парсинга")
}