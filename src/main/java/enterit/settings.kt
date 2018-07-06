package enterit

import org.w3c.dom.Node
import java.io.File
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

val executePath: String = File(Class.forName("enterit.AppKt").protectionDomain.codeSource.location.path).parentFile.toString()
const val arguments = "tander, safmarg, talan, mvideo, mosreg, ugmk"
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
var UserTander: String? = null
var UserMvideo: String? = null
var PassTander: String? = null
var Prefix: String? = null
var UserDb: String? = null
var PassDb: String? = null
var Server: String? = null
var Port: Int = 0
var logPath: String? = null
val DateNow = Date()
var AddTenderTander: Int = 0
var AddTenderSafmarg: Int = 0
var AddTenderMvideo: Int = 0
var AddTenderTalan: Int = 0
var AddTenderMosreg: Int = 0
var AddTenderUgmk: Int = 0
var UrlConnect: String? = null
var formatter: Format = SimpleDateFormat("dd.MM.yyyy kk:mm:ss")
var formatterGpn: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy kk:mm")
var formatterOnlyDate: Format = SimpleDateFormat("dd.MM.yyyy")
var formatterEtpRf: Format = SimpleDateFormat("dd.MM.yyyy kk:mm:ss (XXX)")
var formatterEtpRfN: Format = SimpleDateFormat("dd.MM.yyyy kk:mm (XXX)")

fun getSettings() = try {
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
            }
            .forEach {
                when (it.nodeName) {
                    "database" -> Database = it.childNodes.item(0).textContent
                    "tempdir_tenders_tander" -> tempDirTendersTander = executePath + File.separator + it.childNodes.item(0).textContent
                    "logdir_tenders_tander" -> logDirTendersTander = executePath + File.separator + it.childNodes.item(0).textContent
                    "tempdir_tenders_safmar" -> tempDirTendersSafmarg = executePath + File.separator + it.childNodes.item(0).textContent
                    "logdir_tenders_safmar" -> logDirTendersSafmarg = executePath + File.separator + it.childNodes.item(0).textContent
                    "tempdir_tenders_talan" -> tempDirTendersTalan = executePath + File.separator + it.childNodes.item(0).textContent
                    "logdir_tenders_talan" -> logDirTendersTalan = executePath + File.separator + it.childNodes.item(0).textContent
                    "tempdir_tenders_mvideo" -> tempDirTendersMvideo = executePath + File.separator + it.childNodes.item(0).textContent
                    "logdir_tenders_mvideo" -> logDirTendersMvideo = executePath + File.separator + it.childNodes.item(0).textContent
                    "tempdir_tenders_mosreg" -> tempDirTendersMosreg = executePath + File.separator + it.childNodes.item(0).textContent
                    "logdir_tenders_mosreg" -> logDirTendersMosreg = executePath + File.separator + it.childNodes.item(0).textContent
                    "tempdir_tenders_ugmk" -> tempDirTendersUgmk = executePath + File.separator + it.childNodes.item(0).textContent
                    "logdir_tenders_ugmk" -> logDirTendersUgmk = executePath + File.separator + it.childNodes.item(0).textContent
                    "prefix" -> Prefix = try {
                        it.childNodes.item(0).textContent
                    } catch (e: Exception) {
                        ""
                    }

                    "usertander" -> UserTander = it.childNodes.item(0).textContent
                    "usermvideo" -> UserMvideo = it.childNodes.item(0).textContent
                    "passtander" -> PassTander = it.childNodes.item(0).textContent
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
            else -> run { println("Неверно указаны аргументы, используйте $arguments, выходим из программы"); System.exit(0) }

        }
    }
    getSettings()
    when (arg) {
        Arguments.TANDER -> run { tempDirTenders = tempDirTendersTander; logDirTenders = logDirTendersTander }
        Arguments.SAFMARG -> run { tempDirTenders = tempDirTendersSafmarg; logDirTenders = logDirTendersSafmarg }
        Arguments.TALAN -> run { tempDirTenders = tempDirTendersTalan; logDirTenders = logDirTendersTalan }
        Arguments.MVIDEO -> run { tempDirTenders = tempDirTendersMvideo; logDirTenders = logDirTendersMvideo }
        Arguments.MOSREG -> run { tempDirTenders = tempDirTendersMosreg; logDirTenders = logDirTendersMosreg }
        Arguments.UGMK -> run { tempDirTenders = tempDirTendersUgmk; logDirTenders = logDirTendersUgmk }
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
    UrlConnect = "jdbc:mysql://$Server:$Port/$Database?jdbcCompliantTruncation=false&useUnicode=true&characterEncoding=utf-8&useLegacyDatetimeCode=false&serverTimezone=Europe/Moscow&connectTimeout=5000&socketTimeout=30000"
}