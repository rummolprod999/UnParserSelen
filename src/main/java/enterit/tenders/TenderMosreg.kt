package enterit.tenders

import enterit.*
import enterit.tools.addVNum
import enterit.tools.downloadFromUrlMosreg
import enterit.tools.logger
import enterit.tools.tenderKwords
import org.json.JSONArray
import org.jsoup.Jsoup
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.sql.Timestamp
import java.util.*

data class TenderMosreg(val status: String, var purNum: String, val purObj: String, val nmck: String, val pubDate: Date, val endDate: Date, val url: String) : TenderAbstract(), ITender {
    companion object TypeFz {
        const val typeFz = 53
    }

    init {
        etpName = "Электронный магазин ЕАСУЗ МО"
        etpUrl = "https://market.mosreg.ru/"
    }

    override fun parsing() {
        val dateVer = Date()
        DriverManager.getConnection(UrlConnect, UserDb, PassDb).use(fun(con: Connection) {
            val stmt0 = con.prepareStatement("SELECT id_tender FROM ${Prefix}tender WHERE purchase_number = ? AND doc_publish_date = ? AND type_fz = ? AND end_date = ? AND notice_version = ?").apply {
                setString(1, purNum)
                setTimestamp(2, Timestamp(pubDate.time))
                setInt(3, typeFz)
                setTimestamp(4, Timestamp(endDate.time))
                setString(5, status)
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
            val stmt = con.prepareStatement("SELECT id_tender, date_version FROM ${Prefix}tender WHERE purchase_number = ? AND cancel=0 AND type_fz = ?").apply {
                setString(1, purNum)
                setInt(2, typeFz)
            }
            val rs = stmt.executeQuery()
            while (rs.next()) {
                updated = true
                val idT = rs.getInt(1)
                val dateB: Timestamp = rs.getTimestamp(2)
                if (dateVer.after(dateB) || dateB == Timestamp(dateVer.time)) {
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
            val pageTen = downloadFromUrlMosreg(url)
            if (pageTen == "") {
                logger("Gets empty string ${this::class.simpleName}", url)
                return
            }
            val htmlTen = Jsoup.parse(pageTen)
            var IdOrganizer = 0
            var inn = ""
            val fullnameOrg = htmlTen.selectFirst("span:contains(Полное наименование:) + a")?.ownText()?.trim { it <= ' ' }
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
                    val postalAdr = htmlTen.selectFirst("span:contains(Адрес места нахождения:) + p")?.ownText()?.trim { it <= ' ' }
                            ?: ""
                    val factAdr = ""
                    inn = htmlTen.selectFirst("span:contains(ИНН:) + p")?.ownText()?.trim { it <= ' ' }
                            ?: ""
                    val kpp = ""
                    val email = ""
                    val phone = ""
                    val contactPerson = ""
                    val stmtins = con.prepareStatement("INSERT INTO ${Prefix}organizer SET full_name = ?, post_address = ?, contact_email = ?, contact_phone = ?, fact_address = ?, contact_person = ?, inn = ?, kpp = ?", Statement.RETURN_GENERATED_KEYS).apply {
                        setString(1, fullnameOrg)
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
            val idPlacingWay = 0
            var idTender = 0
            val idRegion = 0
            val insertTender = con.prepareStatement("INSERT INTO ${Prefix}tender SET id_xml = ?, purchase_number = ?, doc_publish_date = ?, href = ?, purchase_object_info = ?, type_fz = ?, id_organizer = ?, id_placing_way = ?, id_etp = ?, end_date = ?, cancel = ?, date_version = ?, num_version = ?, notice_version = ?, xml = ?, print_form = ?, id_region = ?", Statement.RETURN_GENERATED_KEYS)
            insertTender.setString(1, purNum)
            insertTender.setString(2, purNum)
            insertTender.setTimestamp(3, Timestamp(pubDate.time))
            insertTender.setString(4, url)
            insertTender.setString(5, purObj)
            insertTender.setInt(6, typeFz)
            insertTender.setInt(7, IdOrganizer)
            insertTender.setInt(8, idPlacingWay)
            insertTender.setInt(9, idEtp)
            insertTender.setTimestamp(10, Timestamp(endDate.time))
            insertTender.setInt(11, cancelstatus)
            insertTender.setTimestamp(12, Timestamp(dateVer.time))
            insertTender.setInt(13, 1)
            insertTender.setString(14, status)
            insertTender.setString(15, url)
            insertTender.setString(16, url)
            insertTender.setInt(17, idRegion)
            insertTender.executeUpdate()
            val rt = insertTender.generatedKeys
            if (rt.next()) {
                idTender = rt.getInt(1)
            }
            rt.close()
            insertTender.close()
            if (updated) {
                UpdateTenderMosreg++
            } else {
                AddTenderMosreg++
            }
            //val documents: Elements = htmlTen.select("h1:containsOwn(Документы закупки) + div ")
            var idLot = 0
            val LotNumber = 1
            val currency = ""
            val insertLot = con.prepareStatement("INSERT INTO ${Prefix}lot SET id_tender = ?, lot_number = ?, currency = ?, max_price = ?", Statement.RETURN_GENERATED_KEYS).apply {
                setInt(1, idTender)
                setInt(2, LotNumber)
                setString(3, currency)
                setString(4, nmck)
                executeUpdate()
            }
            val rl = insertLot.generatedKeys
            if (rl.next()) {
                idLot = rl.getInt(1)
            }
            rl.close()
            insertLot.close()
            var idCustomer = 0
            if (fullnameOrg != "") {
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
                    stmtins.setString(3, inn)
                    stmtins.executeUpdate()
                    val rsoi = stmtins.generatedKeys
                    if (rsoi.next()) {
                        idCustomer = rsoi.getInt(1)
                    }
                    rsoi.close()
                    stmtins.close()
                }
            }
            val delivPlace = htmlTen.selectFirst("span:contains(Место поставки:) + p")?.ownText()?.trim { it <= ' ' }
                    ?: ""
            val delivTerm = htmlTen.selectFirst("span:contains(Сроки поставки:) + p")?.ownText()?.trim { it <= ' ' }
                    ?: ""
            if (delivPlace != "" || delivTerm != "") {
                val insertCusRec = con.prepareStatement("INSERT INTO ${Prefix}customer_requirement SET id_lot = ?, id_customer = ?, delivery_place = ?, delivery_term = ?").apply {
                    setInt(1, idLot)
                    setInt(2, idCustomer)
                    setString(3, delivPlace)
                    setString(4, delivTerm)
                    executeUpdate()
                    close()
                }
            }
            val purobj1 = htmlTen.select("div.outputResults__oneResult")
            purobj1.forEach {
                val name = it.selectFirst("div.leftPart > p:eq(0)")?.ownText()?.trim { it <= ' ' }
                        ?: ""
                val s = it.selectFirst("td:eq(1)")
                val p = it.selectFirst("td:eq(0)")
                val okei = it.selectFirst("div.centerPart > div > p:eq(0)")?.ownText()?.trim { it <= ' ' }
                        ?: ""
                val quantity = it.selectFirst("div.centerPart > div > p:eq(1)")?.ownText()?.replace(',', '.')?.trim { it <= ' ' }
                        ?: ""
                val price = it.selectFirst("div.rightPart > div > p:eq(0)")?.ownText()?.replace(',', '.')?.trim { it <= ' ' }
                        ?: ""
                val sum = it.selectFirst("div.rightPart > div > p:eq(1)")?.ownText()?.replace(',', '.')?.trim { it <= ' ' }
                        ?: ""
                val fullOkpd = it.selectFirst("div.leftPart a")?.ownText()?.trim { it <= ' ' }
                        ?: ""
                val okpd2 = fullOkpd
                val okpdName = ""
                con.prepareStatement("INSERT INTO ${Prefix}purchase_object SET id_lot = ?, id_customer = ?, name = ?, okei = ?, quantity_value = ?, customer_quantity_value = ?, price = ?, sum = ?, okpd2_code = ?, okpd_name = ?").apply {
                    setInt(1, idLot)
                    setInt(2, idCustomer)
                    setString(3, name)
                    setString(4, okei)
                    setString(5, quantity)
                    setString(6, quantity)
                    setString(7, price)
                    setString(8, sum)
                    setString(9, okpd2)
                    setString(10, okpdName)
                    executeUpdate()
                    close()
                }
            }
            try {
                addAttachments(con, idTender)
            } catch (e: Exception) {
                logger("Ошибка добавления attachments", e.stackTrace, e)
            }

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

    private fun addAttachments(con: Connection, idTender: Int) {
        val urlDoc = "https://api.market.mosreg.ru/api/Trade/$purNum/GetTradeDocuments"
        val docString = downloadFromUrlMosreg(urlDoc, 1, 5000, url)
        if (docString == "") {
            logger("Gets empty docString ${this::class.simpleName}", url)
            return
        }
        val jsonArray = JSONArray(docString)
        for (i in 0 until jsonArray.length()) {
            val fName = jsonArray.getJSONObject(i).getString("FileName")
            val Url = jsonArray.getJSONObject(i).getString("Url")
            if (fName != "" && Url != "") {
                con.prepareStatement("INSERT INTO ${Prefix}attachment SET id_tender = ?, file_name = ?, url = ?").apply {
                    setInt(1, idTender)
                    setString(2, fName)
                    setString(3, Url)
                    executeUpdate()
                    close()
                }
            }
        }
    }
}