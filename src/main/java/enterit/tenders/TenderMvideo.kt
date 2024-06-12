package enterit.tenders

import enterit.*
import enterit.mvideosoup.DTGetProcListResponse
import enterit.tools.addVNum
import enterit.tools.getDateFromString
import enterit.tools.logger
import enterit.tools.tenderKwords
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.sql.Timestamp
import java.util.*

class TenderMvideo(
    val tn: DTGetProcListResponse.Tenders.Item,
    val comp: DTGetProcListResponse.Header.Company,
) : TenderAbstract(),
    ITender {
    companion object TypeFz {
        const val typeFz = 39
    }

    init {
        etpName = "ЭТП E-tender \"М.Видео\""
        etpUrl = "https://ep.mvideo.ru"
    }

    override fun parsing() {
        val pubDate = tn.purchaseStartDate.getDateFromString(formatterOnlyDate)
        val endDate = tn.purchaseEndDate.getDateFromString(formatterOnlyDate)
        var dateVer = tn.changeDate.getDateFromString(formatterOnlyDate)
        if (pubDate == Date(0L) || endDate == Date(0L) || tn.id == "") {
            logger("cannot find dates or purNum in tender", tn.purchaseStartDate, tn.purchaseEndDate, tn.id)
            return
        }
        if (dateVer == Date(0L)) {
            dateVer = pubDate
        }
        DriverManager.getConnection(UrlConnect, UserDb, PassDb).use(
            fun(con: Connection) {
                val stmt0 =
                    con
                        .prepareStatement(
                            "SELECT id_tender FROM ${Prefix}tender WHERE purchase_number = ? AND doc_publish_date = ? AND type_fz = ? AND date_version = ?",
                        ).apply {
                            setString(1, tn.id)
                            setTimestamp(2, Timestamp(pubDate.time))
                            setInt(3, typeFz)
                            setTimestamp(4, Timestamp(dateVer.time))
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
                var updated = false
                val stmt =
                    con
                        .prepareStatement(
                            "SELECT id_tender, date_version FROM ${Prefix}tender WHERE purchase_number = ? AND cancel=0 AND type_fz = ?",
                        ).apply {
                            setString(1, tn.id)
                            setInt(2, typeFz)
                        }
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    updated = true
                    val idT = rs.getInt(1)
                    val dateB: Timestamp = rs.getTimestamp(2)
                    if (dateVer.after(dateB) || dateB == Timestamp(dateVer.time)) {
                        val preparedStatement =
                            con.prepareStatement("UPDATE ${Prefix}tender SET cancel=1 WHERE id_tender = ?").apply {
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
                if (comp.name != "") {
                    val stmto = con.prepareStatement("SELECT id_organizer FROM ${Prefix}organizer WHERE full_name = ?")
                    stmto.setString(1, comp.name)
                    val rso = stmto.executeQuery()
                    if (rso.next()) {
                        IdOrganizer = rso.getInt(1)
                        rso.close()
                        stmto.close()
                    } else {
                        rso.close()
                        stmto.close()
                        val postalAdr = comp.address
                        val factAdr = comp.address
                        val inn = comp.inn
                        val kpp = comp.kpp
                        val email = comp.email
                        val phone = comp.phone
                        val contactPerson = ""
                        val stmtins =
                            con
                                .prepareStatement(
                                    "INSERT INTO ${Prefix}organizer SET full_name = ?, post_address = ?, contact_email = ?, contact_phone = ?, fact_address = ?, contact_person = ?, inn = ?, kpp = ?",
                                    Statement.RETURN_GENERATED_KEYS,
                                ).apply {
                                    setString(1, comp.name)
                                    setString(2, postalAdr)
                                    setString(3, email)
                                    setString(4, phone)
                                    setString(5, factAdr)
                                    setString(6, contactPerson)
                                    setString(7, inn)
                                    setString(8, kpp)
                                    executeUpdate()
                                }
                        val rsoi = stmtins.generatedKeys
                        if (rsoi.next()) {
                            IdOrganizer = rsoi.getInt(1)
                        }
                        rsoi.close()
                        stmtins.close()
                    }
                }
                val idEtp = getEtp(con)
                var idPlacingWay = 0
                var idTender = 0
                if (tn.processTypeText != "") {
                    idPlacingWay = getPlacingWay(con, tn.processTypeText)
                }
                val idRegion = 0
                val insertTender =
                    con.prepareStatement(
                        "INSERT INTO ${Prefix}tender SET id_xml = ?, purchase_number = ?, doc_publish_date = ?, href = ?, purchase_object_info = ?, type_fz = ?, id_organizer = ?, id_placing_way = ?, id_etp = ?, end_date = ?, cancel = ?, date_version = ?, num_version = ?, notice_version = ?, xml = ?, print_form = ?, id_region = ?",
                        Statement.RETURN_GENERATED_KEYS,
                    )
                insertTender.setString(1, tn.id)
                insertTender.setString(2, tn.id)
                insertTender.setTimestamp(3, Timestamp(pubDate.time))
                insertTender.setString(4, tn.link)
                insertTender.setString(5, tn.text)
                insertTender.setInt(6, typeFz)
                insertTender.setInt(7, IdOrganizer)
                insertTender.setInt(8, idPlacingWay)
                insertTender.setInt(9, idEtp)
                insertTender.setTimestamp(10, Timestamp(endDate.time))
                insertTender.setInt(11, cancelstatus)
                insertTender.setTimestamp(12, Timestamp(dateVer.time))
                insertTender.setInt(13, 1)
                insertTender.setString(14, tn.comment)
                insertTender.setString(15, tn.link)
                insertTender.setString(16, tn.link)
                insertTender.setInt(17, idRegion)
                insertTender.executeUpdate()
                val rt = insertTender.generatedKeys
                if (rt.next()) {
                    idTender = rt.getInt(1)
                }
                rt.close()
                insertTender.close()
                if (updated) {
                    UpdateTenderMvideo++
                } else {
                    AddTenderMvideo++
                }
                var idLot = 0
                val LotNumber = 1
                val maxPrice = tn.price.replace(",", ".").replace(Regex("\\s+"), "")
                val currency = ""
                val insertLot =
                    con
                        .prepareStatement(
                            "INSERT INTO ${Prefix}lot SET id_tender = ?, lot_number = ?, currency = ?, max_price = ?",
                            Statement.RETURN_GENERATED_KEYS,
                        ).apply {
                            setInt(1, idTender)
                            setInt(2, LotNumber)
                            setString(3, currency)
                            setString(4, maxPrice)
                            executeUpdate()
                        }
                val rl = insertLot.generatedKeys
                if (rl.next()) {
                    idLot = rl.getInt(1)
                }
                rl.close()
                insertLot.close()
                var idCustomer = 0
                if (comp.name != "") {
                    val stmtoc =
                        con.prepareStatement("SELECT id_customer FROM ${Prefix}customer WHERE full_name = ? LIMIT 1")
                    stmtoc.setString(1, comp.name)
                    val rsoc = stmtoc.executeQuery()
                    if (rsoc.next()) {
                        idCustomer = rsoc.getInt(1)
                        rsoc.close()
                        stmtoc.close()
                    } else {
                        rsoc.close()
                        stmtoc.close()
                        val stmtins =
                            con.prepareStatement(
                                "INSERT INTO ${Prefix}customer SET full_name = ?, is223=1, reg_num = ?, inn = ?",
                                Statement.RETURN_GENERATED_KEYS,
                            )
                        stmtins.setString(1, comp.name)
                        stmtins.setString(
                            2,
                            java.util.UUID
                                .randomUUID()
                                .toString(),
                        )
                        stmtins.setString(3, comp.inn)
                        stmtins.executeUpdate()
                        val rsoi = stmtins.generatedKeys
                        if (rsoi.next()) {
                            idCustomer = rsoi.getInt(1)
                        }
                        rsoi.close()
                        stmtins.close()
                    }
                }
                val insertPurObj =
                    con
                        .prepareStatement("INSERT INTO ${Prefix}purchase_object SET id_lot = ?, id_customer = ?, name = ?, sum = ?")
                        .apply {
                            setInt(1, idLot)
                            setInt(2, idCustomer)
                            setString(3, tn.text)
                            setString(4, maxPrice)
                            executeUpdate()
                            close()
                        }
                try {
                    tenderKwords(idTender, con)
                } catch (e: Exception) {
                    logger("Ошибка добавления ключевых слов", e.stackTrace, e)
                }

                try {
                    addVNum(con, tn.id, typeFz)
                } catch (e: Exception) {
                    logger("Ошибка добавления версий", e.stackTrace, e)
                }
            },
        )
    }
}
