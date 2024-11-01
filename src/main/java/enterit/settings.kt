package enterit

import org.w3c.dom.Node
import java.io.File
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

val executePath: String =
    File(
        Class
            .forName("enterit.AppKt")
            .protectionDomain.codeSource.location.path,
    ).parentFile.toString()
const val arguments =
    "tander, safmarg, talan, mvideo, mosreg, ugmk, imptorgov, sibprime, crimeabt, belmarket, bico, rostov, simferop, kostroma, tomsk, zmo, goszakaz, eurotrans, rhtorg, tsm, pnsh, slaveco, brusnika, etpdon, sminex, orelstroy, level, glavstroy"
lateinit var arg: Arguments
var Database: String? = null
var tempDirTenders: String? = null
var logDirTenders: String? = null
var tempDirTendersTander: String? = null
var logDirTendersTander: String? = null
var tempDirTendersSafmarg: String? = null
var logDirTendersSafmarg: String? = null
var tempDirTendersTalan: String? = null
var logDirTendersTalan: String? = null
var tempDirTendersMvideo: String? = null
var logDirTendersMvideo: String? = null
var tempDirTendersMosreg: String? = null
var logDirTendersMosreg: String? = null
var tempDirTendersUgmk: String? = null
var logDirTendersUgmk: String? = null
var tempDirTendersImpTorgov: String? = null
var logDirTendersImpTorgov: String? = null
var tempDirTendersSibPrime: String? = null
var logDirTendersSibPrime: String? = null
var tempDirTendersCrimeaBt: String? = null
var logDirTendersCrimeaBt: String? = null
var tempDirTendersBelMarket: String? = null
var logDirTendersBelMarket: String? = null
var tempDirTendersBico: String? = null
var logDirTendersBico: String? = null
var tempDirTendersRostov: String? = null
var logDirTendersRostov: String? = null
var tempDirTendersSimferop: String? = null
var logDirTendersSimferop: String? = null
var tempDirTendersKostroma: String? = null
var logDirTendersKostroma: String? = null
var tempDirTendersTomsk: String? = null
var logDirTendersTomsk: String? = null
var tempDirTendersZmo: String? = null
var logDirTendersZmo: String? = null
var tempDirTendersGosZakaz: String? = null
var logDirTendersGosZakaz: String? = null
var tempDirTendersEuroTrans: String? = null
var logDirTendersEuroTrans: String? = null
var tempDirTendersRhTorg: String? = null
var logDirTendersRhTorg: String? = null
var tempDirTendersTsm: String? = null
var logDirTendersTsm: String? = null
var tempDirTendersMedsi: String? = null
var logDirTendersMedsi: String? = null
var tempDirTendersPnsh: String? = null
var logDirTendersPnsh: String? = null
var tempDirTendersSlaveco: String? = null
var logDirTendersSlaveco: String? = null
var tempDirTendersBrusnika: String? = null
var logDirTendersBrusnika: String? = null
var tempDirTendersEtpDon: String? = null
var logDirTendersEtpDon: String? = null
var tempDirTendersSminex: String? = null
var logDirTendersSminex: String? = null
var tempDirTendersOrelStroy: String? = null
var logDirTendersOrelStroy: String? = null
var tempDirTendersLevel: String? = null
var logDirTendersLevel: String? = null
var tempDirTendersGlavstroy: String? = null
var logDirTendersGlavstroy: String? = null
var tempDirTendersItMetal: String? = null
var logDirTendersItMetal: String? = null
var tempDirTendersAbsGroup: String? = null
var logDirTendersAbsGroup: String? = null
var UserTander: String? = null
var UserAbsGroup: String? = null
var UserMvideo: String? = null
var PassTander: String? = null
var PassAbsGroup: String? = null
var UserSafmar: String? = null
var PassSafmar: String? = null
var Prefix: String? = null
var UserDb: String? = null
var PassDb: String? = null
var Server: String? = null
var Port: Int = 0
var logPath: String? = null
val DateNow = Date()
var AddTenderTander: Int = 0
var UpdateTenderTander: Int = 0
var AddTenderSafmarg: Int = 0
var UpdateTenderSafmarg: Int = 0
var AddTenderAbsGroup: Int = 0
var UpdateTenderAbsGroup: Int = 0
var AddTenderMvideo: Int = 0
var UpdateTenderMvideo: Int = 0
var AddTenderTalan: Int = 0
var UpdateTenderTalan: Int = 0
var AddTenderMosreg: Int = 0
var UpdateTenderMosreg: Int = 0
var AddTenderUgmk: Int = 0
var UpdateTenderUgmk: Int = 0
var AddTenderImpTorgov: Int = 0
var UpdateTenderImpTorgov: Int = 0
var AddTenderSibPrime: Int = 0
var UpdateTenderSibPrime: Int = 0
var AddTenderCrimeaBt: Int = 0
var UpdateTenderCrimeaBt: Int = 0
var AddTenderBelMarket: Int = 0
var UpdateTenderBelMarket: Int = 0
var AddTenderBico: Int = 0
var UpdateTenderBico: Int = 0
var AddTenderRostov: Int = 0
var UpdateTenderRostov: Int = 0
var AddTenderSimferop: Int = 0
var UpdateTenderSimferop: Int = 0
var AddTenderKostroma: Int = 0
var UpdateTenderKostroma: Int = 0
var AddTenderTomsk: Int = 0
var UpdateTenderTomsk: Int = 0
var AddTenderZmo: Int = 0
var UpdateTenderZmo: Int = 0
var AddTenderGosZakaz: Int = 0
var UpdateTenderGosZakaz: Int = 0
var AddTenderEuroTrans: Int = 0
var UpdateTenderEuroTrans: Int = 0
var AddTenderRhTorg: Int = 0
var UpdateTenderRhTorg: Int = 0
var AddTenderTsm: Int = 0
var UpdateTenderTsm: Int = 0
var AddTenderMedsi: Int = 0
var UpdateTenderMedsi: Int = 0
var AddTenderPnsh: Int = 0
var UpdateTenderPnsh: Int = 0
var AddTenderSlaveco: Int = 0
var UpdateTenderSlaveco: Int = 0
var AddTenderBrusnika: Int = 0
var UpdateTenderBrusnika: Int = 0
var AddTenderEtpDon: Int = 0
var UpdateTenderEtpDon: Int = 0
var AddTenderSminex: Int = 0
var UpdateTenderSminex: Int = 0
var AddTenderOrelStroy: Int = 0
var UpdateTenderOrelStroy: Int = 0
var AddTenderLevel: Int = 0
var UpdateTenderLevel: Int = 0
var AddTenderGlavstroy: Int = 0
var UpdateTenderGlavstroy: Int = 0
var AddTenderItMetal: Int = 0
var UpdateTenderItMetal: Int = 0
var UrlConnect: String? = null
var formatter: Format = SimpleDateFormat("dd.MM.yyyy kk:mm:ss")
var formatterGpn: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy kk:mm")
var formatterOnlyDate: Format = SimpleDateFormat("dd.MM.yyyy")
var formatterEtpRf: Format = SimpleDateFormat("dd.MM.yyyy kk:mm:ss (XXX)")
var formatterEtpRfN: Format = SimpleDateFormat("dd.MM.yyyy kk:mm (XXX)")

fun getSettings() =
    try {
        val filePathSetting = executePath + File.separator + "setting_tenders.xml"
        val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val document = documentBuilder.parse(filePathSetting)
        val root = document.documentElement
        val settings = root.childNodes
        (0 until settings.length)
            .asSequence()
            .map { settings.item(it) }
            .filter {
                @Suppress("DEPRECATED_IDENTITY_EQUALS")
                it.nodeType !== Node.TEXT_NODE
            }.forEach {
                when (it.nodeName) {
                    "database" -> Database = it.childNodes.item(0).textContent
                    "tempdir_tenders_tander" ->
                        tempDirTendersTander =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_tander" ->
                        logDirTendersTander =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_safmar" ->
                        tempDirTendersSafmarg =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_safmar" ->
                        logDirTendersSafmarg =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_talan" ->
                        tempDirTendersTalan =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_talan" ->
                        logDirTendersTalan =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_mvideo" ->
                        tempDirTendersMvideo =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_mvideo" ->
                        logDirTendersMvideo =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_mosreg" ->
                        tempDirTendersMosreg =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_mosreg" ->
                        logDirTendersMosreg =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_ugmk" ->
                        tempDirTendersUgmk =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_ugmk" ->
                        logDirTendersUgmk =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_imptorgov" ->
                        tempDirTendersImpTorgov =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_imptorgov" ->
                        logDirTendersImpTorgov =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_sibprime" ->
                        tempDirTendersSibPrime =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_sibprime" ->
                        logDirTendersSibPrime =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_crimeabt" ->
                        tempDirTendersCrimeaBt =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_crimeabt" ->
                        logDirTendersCrimeaBt =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_belmarket" ->
                        tempDirTendersBelMarket =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_belmarket" ->
                        logDirTendersBelMarket =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_bico" ->
                        tempDirTendersBico =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_bico" ->
                        logDirTendersBico =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_rostov" ->
                        tempDirTendersRostov =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_rostov" ->
                        logDirTendersRostov =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_simferop" ->
                        tempDirTendersSimferop =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_simferop" ->
                        logDirTendersSimferop =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_kostroma" ->
                        tempDirTendersKostroma =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_kostroma" ->
                        logDirTendersKostroma =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_tomsk" ->
                        tempDirTendersTomsk =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_tomsk" ->
                        logDirTendersTomsk =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_zmo" ->
                        tempDirTendersZmo =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_zmo" ->
                        logDirTendersZmo =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_goszakaz" ->
                        tempDirTendersGosZakaz =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_goszakaz" ->
                        logDirTendersGosZakaz =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_eurotrans" ->
                        tempDirTendersEuroTrans =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_eurotrans" ->
                        logDirTendersEuroTrans =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_rhtorg" ->
                        tempDirTendersRhTorg =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_rhtorg" ->
                        logDirTendersRhTorg =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_tsm" ->
                        tempDirTendersTsm =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_tsm" ->
                        logDirTendersTsm =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_medsi" ->
                        tempDirTendersMedsi =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_medsi" ->
                        logDirTendersMedsi =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_pnsh" ->
                        tempDirTendersPnsh =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_pnsh" ->
                        logDirTendersPnsh =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_slaveco" ->
                        tempDirTendersSlaveco =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_slaveco" ->
                        logDirTendersSlaveco =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_brusnika" ->
                        tempDirTendersBrusnika =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_brusnika" ->
                        logDirTendersBrusnika =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_etpdon" ->
                        tempDirTendersEtpDon =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_etpdon" ->
                        logDirTendersEtpDon =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_sminex" ->
                        tempDirTendersSminex =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_sminex" ->
                        logDirTendersSminex =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_orelstroy" ->
                        tempDirTendersOrelStroy =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_orelstroy" ->
                        logDirTendersOrelStroy =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_level" ->
                        tempDirTendersLevel =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_level" ->
                        logDirTendersLevel =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_glavstroy" ->
                        tempDirTendersGlavstroy =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_glavstroy" ->
                        logDirTendersGlavstroy =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "tempdir_tenders_itmetal" ->
                        tempDirTendersItMetal =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_itmetal" ->
                        logDirTendersItMetal =
                            executePath + File.separator + it.childNodes.item(0).textContent
                    "tempdir_tenders_absgroup" ->
                        tempDirTendersAbsGroup =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "logdir_tenders_absgroup" ->
                        logDirTendersAbsGroup =
                            executePath + File.separator + it.childNodes.item(0).textContent

                    "prefix" ->
                        Prefix =
                            try {
                                it.childNodes.item(0).textContent
                            } catch (e: Exception) {
                                ""
                            }

                    "usertander" -> UserTander = it.childNodes.item(0).textContent
                    "userabsgroup" -> UserAbsGroup = it.childNodes.item(0).textContent
                    "usermvideo" -> UserMvideo = it.childNodes.item(0).textContent
                    "passtander" -> PassTander = it.childNodes.item(0).textContent
                    "passabsgroupr" -> PassAbsGroup = it.childNodes.item(0).textContent
                    "usersafmar" -> UserSafmar = it.childNodes.item(0).textContent
                    "passsafmar" -> PassSafmar = it.childNodes.item(0).textContent
                    "userdb" -> UserDb = it.childNodes.item(0).textContent
                    "passdb" -> PassDb = it.childNodes.item(0).textContent
                    "server" -> Server = it.childNodes.item(0).textContent
                    "port" -> Port = Integer.valueOf(it.childNodes.item(0).textContent)
                }
            }
    } catch (e: Exception) {
        e.printStackTrace()
        System.exit(1)
    }

fun init(args: Array<String>) {
    if (args.isEmpty()) {
        println("Недостаточно агрументов для запуска, используйте $arguments для запуска")
        System.exit(0)
    } else {
        when (args[0]) {
            "tander" -> arg = Arguments.TANDER
            "safmarg" -> arg = Arguments.SAFMARG
            "talan" -> arg = Arguments.TALAN
            "mvideo" -> arg = Arguments.MVIDEO
            "mosreg" -> arg = Arguments.MOSREG
            "ugmk" -> arg = Arguments.UGMK
            "imptorgov" -> arg = Arguments.IMPTORGOV
            "sibprime" -> arg = Arguments.SIBPRIME
            "crimeabt" -> arg = Arguments.CRIMEABT
            "belmarket" -> arg = Arguments.BELMARKET
            "bico" -> arg = Arguments.BICO
            "rostov" -> arg = Arguments.ROSTOV
            "simferop" -> arg = Arguments.SIMFEROP
            "kostroma" -> arg = Arguments.KOSTROMA
            "tomsk" -> arg = Arguments.TOMSK
            "zmo" -> arg = Arguments.ZMO
            "goszakaz" -> arg = Arguments.GOSZAKAZ
            "eurotrans" -> arg = Arguments.EUROTRANS
            "rhtorg" -> arg = Arguments.RHTORG
            "tsm" -> arg = Arguments.TSM
            "medsi" -> arg = Arguments.MEDSI
            "pnsh" -> arg = Arguments.PNSH
            "slaveco" -> arg = Arguments.SLAVECO
            "brusnika" -> arg = Arguments.BRUSNIKA
            "etpdon" -> arg = Arguments.ETPDON
            "sminex" -> arg = Arguments.SMINEX
            "orelstroy" -> arg = Arguments.ORELSTROY
            "level" -> arg = Arguments.LEVEL
            "glavstroy" -> arg = Arguments.GLAVSTROY
            "itmetal" -> arg = Arguments.ITMETAL
            "absgroup" -> arg = Arguments.ABSGROUP
            else ->
                run {
                    println("Неверно указаны аргументы, используйте $arguments, выходим из программы")
                    System.exit(
                        0,
                    )
                }
        }
    }
    getSettings()
    when (arg) {
        Arguments.TANDER ->
            run {
                tempDirTenders = tempDirTendersTander
                logDirTenders = logDirTendersTander
            }

        Arguments.SAFMARG ->
            run {
                tempDirTenders = tempDirTendersSafmarg
                logDirTenders = logDirTendersSafmarg
            }

        Arguments.TALAN ->
            run {
                tempDirTenders = tempDirTendersTalan
                logDirTenders = logDirTendersTalan
            }

        Arguments.MVIDEO ->
            run {
                tempDirTenders = tempDirTendersMvideo
                logDirTenders = logDirTendersMvideo
            }

        Arguments.MOSREG ->
            run {
                tempDirTenders = tempDirTendersMosreg
                logDirTenders = logDirTendersMosreg
            }

        Arguments.UGMK ->
            run {
                tempDirTenders = tempDirTendersUgmk
                logDirTenders = logDirTendersUgmk
            }

        Arguments.IMPTORGOV ->
            run {
                tempDirTenders = tempDirTendersImpTorgov
                logDirTenders = logDirTendersImpTorgov
            }

        Arguments.SIBPRIME ->
            run {
                tempDirTenders = tempDirTendersSibPrime
                logDirTenders = logDirTendersSibPrime
            }

        Arguments.CRIMEABT ->
            run {
                tempDirTenders = tempDirTendersCrimeaBt
                logDirTenders = logDirTendersCrimeaBt
            }

        Arguments.BELMARKET ->
            run {
                tempDirTenders = tempDirTendersBelMarket
                logDirTenders = logDirTendersBelMarket
            }

        Arguments.BICO ->
            run {
                tempDirTenders = tempDirTendersBico
                logDirTenders = logDirTendersBico
            }

        Arguments.ROSTOV ->
            run {
                tempDirTenders = tempDirTendersRostov
                logDirTenders = logDirTendersRostov
            }

        Arguments.SIMFEROP ->
            run {
                tempDirTenders = tempDirTendersSimferop
                logDirTenders = logDirTendersSimferop
            }

        Arguments.KOSTROMA ->
            run {
                tempDirTenders = tempDirTendersKostroma
                logDirTenders = logDirTendersKostroma
            }

        Arguments.TOMSK ->
            run {
                tempDirTenders = tempDirTendersTomsk
                logDirTenders = logDirTendersTomsk
            }

        Arguments.ZMO ->
            run {
                tempDirTenders = tempDirTendersZmo
                logDirTenders = logDirTendersZmo
            }

        Arguments.GOSZAKAZ ->
            run {
                tempDirTenders = tempDirTendersGosZakaz
                logDirTenders = logDirTendersGosZakaz
            }

        Arguments.EUROTRANS ->
            run {
                tempDirTenders = tempDirTendersEuroTrans
                logDirTenders = logDirTendersEuroTrans
            }

        Arguments.RHTORG ->
            run {
                tempDirTenders = tempDirTendersRhTorg
                logDirTenders = logDirTendersRhTorg
            }

        Arguments.TSM ->
            run {
                tempDirTenders = tempDirTendersTsm
                logDirTenders = logDirTendersTsm
            }

        Arguments.MEDSI ->
            run {
                tempDirTenders = tempDirTendersMedsi
                logDirTenders = logDirTendersMedsi
            }

        Arguments.PNSH ->
            run {
                tempDirTenders = tempDirTendersPnsh
                logDirTenders = logDirTendersPnsh
            }

        Arguments.SLAVECO ->
            run {
                tempDirTenders = tempDirTendersSlaveco
                logDirTenders = logDirTendersSlaveco
            }

        Arguments.BRUSNIKA ->
            run {
                tempDirTenders = tempDirTendersBrusnika
                logDirTenders = logDirTendersBrusnika
            }

        Arguments.ETPDON ->
            run {
                tempDirTenders = tempDirTendersEtpDon
                logDirTenders = logDirTendersEtpDon
            }

        Arguments.SMINEX ->
            run {
                tempDirTenders = tempDirTendersSminex
                logDirTenders = logDirTendersSminex
            }

        Arguments.ORELSTROY ->
            run {
                tempDirTenders = tempDirTendersOrelStroy
                logDirTenders = logDirTendersOrelStroy
            }

        Arguments.LEVEL ->
            run {
                tempDirTenders = tempDirTendersLevel
                logDirTenders = logDirTendersLevel
            }

        Arguments.GLAVSTROY ->
            run {
                tempDirTenders = tempDirTendersGlavstroy
                logDirTenders = logDirTendersGlavstroy
            }

        Arguments.ITMETAL ->
            run {
                tempDirTenders = tempDirTendersItMetal
                logDirTenders = logDirTendersItMetal
            }
        Arguments.ABSGROUP ->
            run {
                tempDirTenders = tempDirTendersAbsGroup
                logDirTenders = logDirTendersAbsGroup
            }
    }
    if (tempDirTenders == null || tempDirTenders == "") {
        println("Не задана папка для временных файлов, выходим из программы")
        System.exit(0)
    }
    if (logDirTenders == null || logDirTenders == "") {
        println("Не задана папка для логов, выходим из программы")
        System.exit(0)
    }
    val tmp = File(tempDirTenders)
    if (tmp.exists()) {
        tmp.delete()
        tmp.mkdir()
    } else {
        tmp.mkdir()
    }
    val log = File(logDirTenders)
    if (!log.exists()) {
        log.mkdir()
    }
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    logPath = "$logDirTenders${File.separator}log_parsing_${arg}_${dateFormat.format(DateNow)}.log"
    UrlConnect =
        "jdbc:mysql://$Server:$Port/$Database?jdbcCompliantTruncation=false&useUnicode=true&characterEncoding=utf-8&useLegacyDatetimeCode=false&serverTimezone=Europe/Moscow&connectTimeout=5000&socketTimeout=30000"
}
