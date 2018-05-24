package enterit.tenders

import enterit.PassDb
import enterit.Prefix
import enterit.UrlConnect
import enterit.UserDb
import enterit.dataclasses.TalanT
import enterit.tools.downloadFromUrl
import enterit.tools.logger
import org.jsoup.Jsoup
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Timestamp
import java.util.*

class TenderTalan(val tn: TalanT) : TenderAbstract(), ITender {
    companion object TypeFz {
        val typeFz = 38
    }

    init {
        etpName = "ЭТП Талан"
        etpUrl = "http://тендеры.талан.рф"
    }

    override fun parsing() {
        val pageLot = downloadFromUrl(tn.hrefL)
        if (pageLot == "") {
            logger("Gets empty string ${this::class.simpleName}", tn.hrefL)
            return
        }
        val htmlLot = Jsoup.parse(pageLot)
        val pageTen = downloadFromUrl(tn.hrefT)
        if (pageTen == "") {
            logger("Gets empty string ${this::class.simpleName}", tn.hrefT)
            return
        }
        val htmlTen = Jsoup.parse(pageTen)
        val dateVer = Date()
        DriverManager.getConnection(UrlConnect, UserDb, PassDb).use(fun(con: Connection) {
            val stmt0 = con.prepareStatement("SELECT id_tender FROM ${Prefix}tender WHERE purchase_number = ? AND doc_publish_date = ? AND type_fz = ? AND end_date = ? AND notice_version = ?").apply {
                setString(1, tn.purNum)
                setTimestamp(2, Timestamp(tn.pubDate.time))
                setInt(3, typeFz)
                setTimestamp(4, Timestamp(tn.endDate.time))
                setString(5, tn.status)
            }
            val r = stmt0.executeQuery()
            if (r.next()) {
                r.close()
                stmt0.close()
                return
            }
            r.close()
            stmt0.close()
            var cancelstatus = 0
            val stmt = con.prepareStatement("SELECT id_tender, date_version FROM ${Prefix}tender WHERE purchase_number = ? AND cancel=0 AND type_fz = ?").apply {
                setString(1, tn.purNum)
                setInt(2, typeFz)
            }
            val rs = stmt.executeQuery()
            while (rs.next()) {
                val idT = rs.getInt(1)
                val dateB: Timestamp = rs.getTimestamp(2)
                if (dateVer.after(dateB) || dateB == Timestamp(dateVer.time)) {
                    val preparedStatement = con.prepareStatement("UPDATE ${Prefix}tender SET cancel=1 WHERE id_tender = ?").apply {
                        setInt(1, idT)
                        execute()
                        close()
                    }
                } else {
                    cancelstatus = 1
                }
            }
            rs.close()
            stmt.close()
            var IdOrganizer = 0
            val urlOrgT = htmlTen.selectFirst("label:containsOwn(Организатор) + div > div > a")?.attr("href")?.trim { it <= ' ' }
                    ?: ""
            if (urlOrgT != "") {
                val urlOrg = "$etpUrl$urlOrgT"
                val pageOrg = downloadFromUrl(urlOrg)
                if (pageOrg == "") {
                    logger("Gets empty string ${this::class.simpleName}", urlOrg)
                    return
                }
                val htmlOrg = Jsoup.parse(pageOrg)
                val fullnameOrg = htmlOrg.selectFirst("label:containsOwn(Полное наименование) + div > div")?.ownText()?.trim { it <= ' ' }
                        ?: ""
                if (fullnameOrg != "") {
                    val stmto = con.prepareStatement("SELECT id_organizer FROM ${Prefix}organizer WHERE full_name = ?")
                    stmto.setString(1, fullnameOrg)
                    val rso = stmto.executeQuery()
                    if (rso.next()) {
                        IdOrganizer = rso.getInt(1)
                        rso.close()
                        stmto.close()
                    } else {
                        rso.close()
                        stmto.close()
                        val postalAdr = htmlOrg.selectFirst("label:containsOwn(Почтовый адрес) + div > div")?.ownText()?.trim { it <= ' ' }
                                ?: ""
                        val factAdr = htmlOrg.selectFirst("label:containsOwn(Юридический адрес) + div > div")?.ownText()?.trim { it <= ' ' }
                                ?: ""
                    }
                }
            }
        })
    }
}