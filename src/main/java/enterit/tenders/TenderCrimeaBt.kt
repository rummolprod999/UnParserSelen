package enterit.tenders

import enterit.*
import enterit.dataclasses.CrimeaBT
import enterit.tools.addVNum
import enterit.tools.logger
import enterit.tools.tenderKwords
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.sql.Timestamp
import java.util.*

class TenderCrimeaBt(
    val tn: CrimeaBT,
) : TenderAbstract(),
    ITender {
    companion object TypeFz {
        const val typeFz = 74
    }

    init {
        etpName = "BiCo Крым"
        etpUrl = "http://crimea.bt.su"
    }

    override fun parsing() {
        val dateVer = Date()
        DriverManager.getConnection(UrlConnect, UserDb, PassDb).use(
            fun(con: Connection) {
                val stmt0 =
                    con
                        .prepareStatement(
                            "SELECT id_tender FROM ${Prefix}tender WHERE purchase_number = ? AND doc_publish_date = ? AND type_fz = ? AND end_date = ?",
                        ).apply {
                            setString(1, tn.purNum)
                            setTimestamp(2, Timestamp(tn.pubDate.time))
                            setInt(3, typeFz)
                            setTimestamp(4, Timestamp(tn.endDate.time))
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
                            setString(1, tn.purNum)
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
                val IdOrganizer = 0
                val idEtp = getEtp(con)
                var idPlacingWay = 0
                var idTender = 0
                if (tn.placingWayName != "") {
                    idPlacingWay = getPlacingWay(con, tn.placingWayName)
                }
                val idRegion = getIdRegion(con, "крым")
                val insertTender =
                    con.prepareStatement(
                        "INSERT INTO ${Prefix}tender SET id_xml = ?, purchase_number = ?, doc_publish_date = ?, href = ?, purchase_object_info = ?, type_fz = ?, id_organizer = ?, id_placing_way = ?, id_etp = ?, end_date = ?, cancel = ?, date_version = ?, num_version = ?, notice_version = ?, xml = ?, print_form = ?, id_region = ?",
                        Statement.RETURN_GENERATED_KEYS,
                    )
                insertTender.setString(1, tn.purNum)
                insertTender.setString(2, tn.purNum)
                insertTender.setTimestamp(3, Timestamp(tn.pubDate.time))
                insertTender.setString(4, tn.hrefT)
                insertTender.setString(5, tn.purName)
                insertTender.setInt(6, typeFz)
                insertTender.setInt(7, IdOrganizer)
                insertTender.setInt(8, idPlacingWay)
                insertTender.setInt(9, idEtp)
                insertTender.setTimestamp(10, Timestamp(tn.endDate.time))
                insertTender.setInt(11, cancelstatus)
                insertTender.setTimestamp(12, Timestamp(dateVer.time))
                insertTender.setInt(13, 1)
                insertTender.setString(14, "")
                insertTender.setString(15, tn.hrefT)
                insertTender.setString(16, tn.hrefT)
                insertTender.setInt(17, idRegion)
                insertTender.executeUpdate()
                val rt = insertTender.generatedKeys
                if (rt.next()) {
                    idTender = rt.getInt(1)
                }
                rt.close()
                insertTender.close()
                if (updated) {
                    UpdateTenderCrimeaBt++
                } else {
                    AddTenderCrimeaBt++
                }
                var idLot = 0
                val LotNumber = 1
                val insertLot =
                    con
                        .prepareStatement(
                            "INSERT INTO ${Prefix}lot SET id_tender = ?, lot_number = ?, currency = ?, max_price = ?",
                            Statement.RETURN_GENERATED_KEYS,
                        ).apply {
                            setInt(1, idTender)
                            setInt(2, LotNumber)
                            setString(3, tn.currency)
                            setString(4, tn.nmck)
                            executeUpdate()
                        }
                val rl = insertLot.generatedKeys
                if (rl.next()) {
                    idLot = rl.getInt(1)
                }
                rl.close()
                insertLot.close()
                val idCustomer = 0
                val insertPurObj =
                    con
                        .prepareStatement("INSERT INTO ${Prefix}purchase_object SET id_lot = ?, id_customer = ?, name = ?, sum = ?")
                        .apply {
                            setInt(1, idLot)
                            setInt(2, idCustomer)
                            setString(3, tn.purName)
                            setString(4, tn.nmck)
                            executeUpdate()
                            close()
                        }
                try {
                    tenderKwords(idTender, con)
                } catch (e: Exception) {
                    logger("Ошибка добавления ключевых слов", e.stackTrace, e)
                }

                try {
                    addVNum(con, tn.purNum, typeFz)
                } catch (e: Exception) {
                    logger("Ошибка добавления версий", e.stackTrace, e)
                }
            },
        )
    }
}
