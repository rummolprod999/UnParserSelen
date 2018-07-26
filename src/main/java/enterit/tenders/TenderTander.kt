package enterit.tenders

import enterit.*
import enterit.tools.addVNum
import enterit.tools.logger
import enterit.tools.tenderKwords
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.sql.Timestamp
import java.util.*

class TenderTander(val purNum: String, var urlDoc: String, val purObj: String, private val pubDate: Date, private val endDate: Date) : TenderAbstract(), ITender {
    companion object TypeFz {
        val typeFz = 32
    }

    init {
        etpName = "АО «Тандер»"
        etpUrl = "https://srm.tander.ru"
    }

    override fun parsing() {
        val dateVer = Date()
        DriverManager.getConnection(UrlConnect, UserDb, PassDb).use(fun(con: Connection) {
            val stmt0 = con.prepareStatement("SELECT id_tender FROM ${Prefix}tender WHERE purchase_number = ? AND doc_publish_date = ? AND type_fz = ? AND end_date = ?")
            stmt0.setString(1, purNum)
            stmt0.setTimestamp(2, Timestamp(pubDate.time))
            stmt0.setInt(3, typeFz)
            stmt0.setTimestamp(4, Timestamp(endDate.time))
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
            val stmt = con.prepareStatement("SELECT id_tender, date_version FROM ${Prefix}tender WHERE purchase_number = ? AND cancel=0 AND type_fz = ?")
            stmt.setString(1, purNum)
            stmt.setInt(2, typeFz)
            val rs = stmt.executeQuery()
            while (rs.next()) {
                updated = true
                val idT = rs.getInt(1)
                val dateB: Timestamp = rs.getTimestamp(2)
                if (dateVer.after(dateB) || dateB == Timestamp(dateVer.time)) {
                    val preparedStatement = con.prepareStatement("UPDATE ${Prefix}tender SET cancel=1 WHERE id_tender = ?")
                    preparedStatement.setInt(1, idT)
                    preparedStatement.execute()
                    preparedStatement.close()
                } else {
                    cancelstatus = 1
                }
            }
            rs.close()
            stmt.close()
            var IdOrganizer = 0
            val fullnameOrg = etpName
            val innOrg = "2310031475"
            val kppOrg = "231001001"
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
                val postalAdr = "350002, КРАСНОДАРСКИЙ КРАЙ, КРАСНОДАР Г, ИМ ЛЕВАНЕВСКОГО УЛ, ДОМ 185"
                val email = "barmotina_ob@magnit.ru"
                val phone = "8 (800) 200-90-02"
                val fax = ""
                val stmtins = con.prepareStatement("INSERT INTO ${Prefix}organizer SET full_name = ?, inn = ?, kpp = ?, post_address = ?, contact_email = ?, contact_phone = ?, contact_fax = ?", Statement.RETURN_GENERATED_KEYS)
                stmtins.setString(1, fullnameOrg)
                stmtins.setString(2, innOrg)
                stmtins.setString(3, kppOrg)
                stmtins.setString(4, postalAdr)
                stmtins.setString(5, email)
                stmtins.setString(6, phone)
                stmtins.setString(7, fax)
                stmtins.executeUpdate()
                val rsoi = stmtins.generatedKeys
                if (rsoi.next()) {
                    IdOrganizer = rsoi.getInt(1)
                }
                rsoi.close()
                stmtins.close()
            }
            val idEtp = getEtp(con)
            val idPlacingWay = 0
            var idTender = 0
            val idRegion = 0
            val insertTender = con.prepareStatement("INSERT INTO ${Prefix}tender SET id_xml = ?, purchase_number = ?, doc_publish_date = ?, href = ?, purchase_object_info = ?, type_fz = ?, id_organizer = ?, id_placing_way = ?, id_etp = ?, end_date = ?, cancel = ?, date_version = ?, num_version = ?, notice_version = ?, xml = ?, print_form = ?, id_region = ?", Statement.RETURN_GENERATED_KEYS)
            insertTender.setString(1, purNum)
            insertTender.setString(2, purNum)
            insertTender.setTimestamp(3, Timestamp(pubDate.time))
            insertTender.setString(4, urlDoc)
            insertTender.setString(5, purObj)
            insertTender.setInt(6, typeFz)
            insertTender.setInt(7, IdOrganizer)
            insertTender.setInt(8, idPlacingWay)
            insertTender.setInt(9, idEtp)
            insertTender.setTimestamp(10, Timestamp(endDate.time))
            insertTender.setInt(11, cancelstatus)
            insertTender.setTimestamp(12, Timestamp(dateVer.time))
            insertTender.setInt(13, 1)
            insertTender.setString(14, "")
            insertTender.setString(15, urlDoc)
            insertTender.setString(16, urlDoc)
            insertTender.setInt(17, idRegion)
            insertTender.executeUpdate()
            val rt = insertTender.generatedKeys
            if (rt.next()) {
                idTender = rt.getInt(1)
            }
            rt.close()
            insertTender.close()
            if (updated) {
                UpdateTenderTander++
            } else {
                AddTenderTander++
            }
            val insertDoc = con.prepareStatement("INSERT INTO ${Prefix}attachment SET id_tender = ?, file_name = ?, url = ?")
            insertDoc.setInt(1, idTender)
            insertDoc.setString(2, "Скачать файл")
            insertDoc.setString(3, urlDoc)
            insertDoc.executeUpdate()
            insertDoc.close()
            var idLot = 0
            val LotNumber = 1
            val insertLot = con.prepareStatement("INSERT INTO ${Prefix}lot SET id_tender = ?, lot_number = ?", Statement.RETURN_GENERATED_KEYS)
            insertLot.setInt(1, idTender)
            insertLot.setInt(2, LotNumber)
            insertLot.executeUpdate()
            val rl = insertLot.generatedKeys
            if (rl.next()) {
                idLot = rl.getInt(1)
            }
            rl.close()
            insertLot.close()
            var idCustomer = 0
            val stmtoc = con.prepareStatement("SELECT id_customer FROM ${Prefix}customer WHERE full_name = ? LIMIT 1")
            stmtoc.setString(1, fullnameOrg)
            val rsoc = stmtoc.executeQuery()
            if (rsoc.next()) {
                idCustomer = rsoc.getInt(1)
                rsoc.close()
                stmtoc.close()
            } else {
                rsoc.close()
                stmtoc.close()
                val stmtins = con.prepareStatement("INSERT INTO ${Prefix}customer SET full_name = ?, is223=1, reg_num = ?, inn = ?", Statement.RETURN_GENERATED_KEYS)
                stmtins.setString(1, fullnameOrg)
                stmtins.setString(2, java.util.UUID.randomUUID().toString())
                stmtins.setString(3, innOrg)
                stmtins.executeUpdate()
                val rsoi = stmtins.generatedKeys
                if (rsoi.next()) {
                    idCustomer = rsoi.getInt(1)
                }
                rsoi.close()
                stmtins.close()
            }
            val insertPurObj = con.prepareStatement("INSERT INTO ${Prefix}purchase_object SET id_lot = ?, id_customer = ?, name = ?")
            insertPurObj.setInt(1, idLot)
            insertPurObj.setInt(2, idCustomer)
            insertPurObj.setString(3, purObj)
            insertPurObj.executeUpdate()
            insertPurObj.close()
            try {
                tenderKwords(idTender, con)
            } catch (e: Exception) {
                logger("Ошибка добавления ключевых слов", e.stackTrace, e)
            }

            try {
                addVNum(con, purNum, typeFz)
            } catch (e: Exception) {
                logger("Ошибка добавления версий", e.stackTrace, e)
            }
        })

    }
}