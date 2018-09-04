package enterit.tenders

import enterit.*
import enterit.dataclasses.UgmkT
import enterit.tools.*
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.sql.Timestamp
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class TenderUgmk(val tt: UgmkT, val driver: ChromeDriver, val wait: WebDriverWait) : TenderAbstract(), ITender {
    inner class Doc(val DocName: String, val Href: String)

    val docList = mutableListOf<Doc>()

    companion object TypeFz {
        const val typeFz = 62
    }

    init {
        etpName = "\"УГМК-Холдинг\""
        etpUrl = "https://zakupki.ugmk.com/"
    }

    override fun parsing() {
        val dateVer = Date()
        driver.get(tt.hrefT)
        driver.switchTo().defaultContent()
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[. = 'Организатор']/following-sibling::div//a")))
        driver.switchTo().defaultContent()
        DriverManager.getConnection(UrlConnect, UserDb, PassDb).use(fun(con: Connection) {
            var IdOrganizer = 0
            var inn = ""
            val fullnameOrg = driver.findElementWithoutException(By.xpath("//label[. = 'Организатор']/following-sibling::div//a"))?.text?.trim { it <= ' ' }
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
                    val postalAdr = ""
                    val factAdr = ""
                    inn = ""
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
            val delivTerm = driver.findElementWithoutException(By.xpath("//label[. = 'Срок заключения договора']/following-sibling::div/div"))?.text?.trim { it <= ' ' }
                    ?: ""
            val docT = driver.findElements(By.xpath("//div[@class = 'doc-group-block']//div[contains(@class, 'doc-block')]"))
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'doc-group-block']//div[contains(@class, 'doc-block')]")))
            driver.switchTo().defaultContent()
            docT.forEach {
                val docName = it.findElementWithoutException(By.xpath(".//a[@class = 'file-download-link']"))?.text?.trim { it <= ' ' }
                        ?: ""
                val docUrl = it.findElementWithoutException(By.xpath(".//a[@class = 'file-download-link']"))?.getAttribute("href")?.trim { it <= ' ' }
                        ?: ""
                if (docUrl != "") {
                    docList.add(Doc(docName, docUrl))
                }
            }
            driver.get(tt.hrefL)
            driver.switchTo().defaultContent()
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[. = 'Дата начала приема заявок']/following-sibling::div/div")))
            driver.switchTo().defaultContent()

            val dateEndTmp = driver.findElementWithoutException(By.xpath("//label[. = 'Дата окончания приема заявок']/following-sibling::div/div"))?.text?.trim()?.trim { it <= ' ' }
                    ?: ""
            var dateEnd = getDateFromFormat(dateEndTmp, formatterGpn)
            if (dateEnd == Date(0L)) {
                logger("can not find dateEnd on page", tt.hrefL)
                return
            }

            val datePubTmp = driver.findElementWithoutException(By.xpath("//label[. = 'Дата начала приема заявок']/following-sibling::div/div"))?.text?.trim()?.trim { it <= ' ' }
                    ?: ""
            var datePub = getDateFromFormat(datePubTmp, formatterGpn)
            if (datePub == Date(0L)) {
                logger("can not find datePub on page", tt.hrefL)
                return
            }
            datePub = Date(datePub.time - 2 * 3600 * 1000)
            dateEnd = Date(dateEnd.time - 2 * 3600 * 1000)

            val dateScorTmp = driver.findElementWithoutException(By.xpath("//label[. = 'Дата и время вскрытия конвертов']/following-sibling::div/div"))?.text?.trim()?.trim { it <= ' ' }
                    ?: ""
            var dateScor = getDateFromFormat(dateScorTmp, formatterGpn)
            if (dateScor != Date(0L)) {
                dateScor = Date(dateScor.time - 2 * 3600 * 1000)
            }
            val stmt0 = con.prepareStatement("SELECT id_tender FROM ${Prefix}tender WHERE purchase_number = ? AND type_fz = ? AND end_date = ? AND notice_version = ? AND doc_publish_date = ? AND scoring_date = ? ").apply {
                setString(1, tt.purNum)
                setInt(2, typeFz)
                setTimestamp(3, Timestamp(dateEnd.time))
                setString(4, tt.status)
                setTimestamp(5, Timestamp(datePub.time))
                setTimestamp(6, Timestamp(dateScor.time))
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
                setString(1, tt.purNum)
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
            val idEtp = getEtp(con)
            var idPlacingWay = 0
            if (tt.placingWayName != "") {
                idPlacingWay = getPlacingWay(con, tt.placingWayName)
            }
            var idTender = 0
            val idRegion = 0
            val insertTender = con.prepareStatement("INSERT INTO ${Prefix}tender SET id_xml = ?, purchase_number = ?, doc_publish_date = ?, href = ?, purchase_object_info = ?, type_fz = ?, id_organizer = ?, id_placing_way = ?, id_etp = ?, end_date = ?, cancel = ?, date_version = ?, num_version = ?, notice_version = ?, xml = ?, print_form = ?, id_region = ?, scoring_date = ?", Statement.RETURN_GENERATED_KEYS)
            insertTender.setString(1, tt.purNum)
            insertTender.setString(2, tt.purNum)
            insertTender.setTimestamp(3, Timestamp(datePub.time))
            insertTender.setString(4, tt.hrefL)
            insertTender.setString(5, tt.purName)
            insertTender.setInt(6, typeFz)
            insertTender.setInt(7, IdOrganizer)
            insertTender.setInt(8, idPlacingWay)
            insertTender.setInt(9, idEtp)
            insertTender.setTimestamp(10, Timestamp(dateEnd.time))
            insertTender.setInt(11, cancelstatus)
            insertTender.setTimestamp(12, Timestamp(dateVer.time))
            insertTender.setInt(13, 1)
            insertTender.setString(14, tt.status)
            insertTender.setString(15, tt.hrefL)
            insertTender.setString(16, tt.hrefL)
            insertTender.setInt(17, idRegion)
            insertTender.setTimestamp(18, Timestamp(dateScor.time))
            insertTender.executeUpdate()
            val rt = insertTender.generatedKeys
            if (rt.next()) {
                idTender = rt.getInt(1)
            }
            rt.close()
            insertTender.close()
            if (updated) {
                UpdateTenderUgmk++
            } else {
                AddTenderUgmk++
            }
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'doc-group-block']//div[contains(@class, 'doc-block')]")))
            driver.switchTo().defaultContent()
            val docL = driver.findElements(By.xpath("//div[@class = 'doc-group-block']//div[contains(@class, 'doc-block')]"))
            docL.forEach {
                val docName = it.findElementWithoutException(By.xpath(".//a[@class = 'file-download-link']"))?.text?.trim { it <= ' ' }
                        ?: ""
                val docUrl = it.findElementWithoutException(By.xpath(".//a[@class = 'file-download-link']"))?.getAttribute("href")?.trim { it <= ' ' }
                        ?: ""
                if (docUrl != "") {
                    docList.add(Doc(docName, docUrl))
                }
            }
            docList.forEach {
                val insertDoc = con.prepareStatement("INSERT INTO ${Prefix}attachment SET id_tender = ?, file_name = ?, url = ?")
                insertDoc.setInt(1, idTender)
                insertDoc.setString(2, it.DocName)
                insertDoc.setString(3, it.Href)
                insertDoc.executeUpdate()
                insertDoc.close()
            }
            var idLot = 0
            var LotNumber = tt.purNum.regExpTest("""/0*(.+)""")
            if (LotNumber == "") LotNumber = "1"
            var nmck = driver.findElementWithoutException(By.xpath("//label[. = 'Начальная цена (без НДС)']/following-sibling::div/div"))?.text?.trim()?.trim { it <= ' ' }
                    ?: ""
            var currency = ""
            if (!nmck.contains("Цена не определена")) {
                nmck = nmck.replace(",", ".").replace("&nbsp;", "")
                val pattern: Pattern = Pattern.compile("\\s+")
                val matcher: Matcher = pattern.matcher(nmck)
                nmck = matcher.replaceAll("")
                currency = nmck
                nmck = nmck.regExpTest("""([\d\.]+)""")
            }
            if (currency != "") {
                currency = currency.regExpTest("""[\d\.]+(.+)""")
            }
            val insertLot = con.prepareStatement("INSERT INTO ${Prefix}lot SET id_tender = ?, lot_number = ?, currency = ?, max_price = ?", Statement.RETURN_GENERATED_KEYS).apply {
                setInt(1, idTender)
                setString(2, LotNumber)
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
            if (tt.nameCus != "") {
                val stmtoc = con.prepareStatement("SELECT id_customer FROM ${Prefix}customer WHERE full_name = ? LIMIT 1")
                stmtoc.setString(1, tt.nameCus)
                val rsoc = stmtoc.executeQuery()
                if (rsoc.next()) {
                    idCustomer = rsoc.getInt(1)
                    rsoc.close()
                    stmtoc.close()
                } else {
                    rsoc.close()
                    stmtoc.close()
                    val stmtins = con.prepareStatement("INSERT INTO ${Prefix}customer SET full_name = ?, is223=1, reg_num = ?, inn = ?", Statement.RETURN_GENERATED_KEYS)
                    stmtins.setString(1, tt.nameCus)
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
            val name = driver.findElementWithoutException(By.xpath("//label[. = 'Лот']/following-sibling::div/div"))?.text?.trim { it <= ' ' }
                    ?: ""
            con.prepareStatement("INSERT INTO ${Prefix}purchase_object SET id_lot = ?, id_customer = ?, name = ?").apply {
                setInt(1, idLot)
                setInt(2, idCustomer)
                setString(3, name)
                executeUpdate()
                close()
            }
            if (delivTerm != "") {
                val insertCusRec = con.prepareStatement("INSERT INTO ${Prefix}customer_requirement SET id_lot = ?, id_customer = ?, delivery_term = ?").apply {
                    setInt(1, idLot)
                    setInt(2, idCustomer)
                    setString(3, delivTerm)
                    executeUpdate()
                    close()
                }
            }
            try {
                tenderKwords(idTender, con)
            } catch (e: Exception) {
                logger("Ошибка добавления ключевых слов", e.stackTrace, e)
            }

            try {
                addVNum(con, tt.purNum, typeFz)
            } catch (e: Exception) {
                logger("Ошибка добавления версий", e.stackTrace, e)
            }
        })
    }
}