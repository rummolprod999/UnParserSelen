package enterit.tenders

import enterit.*
import enterit.dataclasses.SafmargT
import enterit.parsers.ParserEuroTrans
import enterit.tools.*
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.sql.Timestamp
import java.util.*

class TenderEuroTrans(val tn: SafmargT<String>, val driver: ChromeDriver) : TenderAbstract(), ITender {

    init {
        etpName = "ООО «ЕТС»"
        etpUrl = "http://tender.eurotransstroy.ru/"
    }

    override fun parsing() {
        //driver.close()
        driver.get(tn.href)
        val wait = WebDriverWait(driver, ParserEuroTrans.timeoutB)
        /*wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.xpath("//iframe")))*/
        Thread.sleep(10000)
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(., 'Начало представления предложений (этап №1)')]/following-sibling::div/div")))
        } catch (e: Exception) {
            logger("cannot find expected startDate", driver.pageSource)
            return
        }
        val datePubT =
            driver.findElementWithoutException(By.xpath("//div[contains(., 'Начало представления предложений (этап №1)')]/following-sibling::div/div"))?.text?.trim { it <= ' ' }
                ?: ""
        val datePubTT = datePubT.regExpTest("""(\d{2}.\d{2}.\d{4} \d{2}:\d{2})""")
        val pubDate = datePubTT.getDateFromString(formatterGpn)
        val endDateT =
            driver.findElementWithoutException(By.xpath("//div[contains(., 'Окончание представления предложений')]/following-sibling::div/div"))?.text?.trim { it <= ' ' }
                ?: ""
        val endDateTT = endDateT.regExpTest("""(\d{2}.\d{2}.\d{4} \d{2}:\d{2})""")
        val endDate = endDateTT.getDateFromString(formatterGpn)
        if (pubDate == Date(0L) || endDate == Date(0L)) {
            logger("cannot find dates in tender", tn.href, datePubT, endDateT)
            return
        }
        val status =
            driver.findElementWithoutException(By.xpath("//div[@class = 'page-status-title']"))?.text?.trim { it <= ' ' }
                ?: ""
        DriverManager.getConnection(UrlConnect, UserDb, PassDb).use(fun(con: Connection) {
            val dateVer = Date()
            val stmt0 =
                con.prepareStatement("SELECT id_tender FROM ${Prefix}tender WHERE purchase_number = ? AND doc_publish_date = ? AND type_fz = ? AND end_date = ? AND notice_version = ?")
                    .apply {
                        setString(1, tn.purNum)
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
            val stmt =
                con.prepareStatement("SELECT id_tender, date_version FROM ${Prefix}tender WHERE purchase_number = ? AND cancel=0 AND type_fz = ?")
                    .apply {
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
            var IdOrganizer = 0
            val fullnameOrg =
                driver.findElementWithoutException(By.xpath("//div[contains(@class, 'organization__info__title')]"))?.text?.trim { it <= ' ' }
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
                    val postalAdr =
                        driver.findElementWithoutException(By.xpath("//td[contains(preceding-sibling::td, 'Почтовый адрес')]//div[contains(@class, 'translate-text-')]"))?.text?.trim { it <= ' ' }
                            ?: ""
                    val factAdr =
                        driver.findElementWithoutException(By.xpath("//td[contains(preceding-sibling::td, 'Юридический адрес (место нахождения)')]//div[contains(@class, 'translate-text-')]"))?.text?.trim { it <= ' ' }
                            ?: ""
                    val email =
                        driver.findElementWithoutException(By.xpath("//td[contains(preceding-sibling::td, 'Адрес электронной почты')]//div[contains(@class, 'translate-text-')]"))?.text?.trim { it <= ' ' }
                            ?: ""
                    val phone =
                        driver.findElementWithoutException(By.xpath("//div[contains(., 'Адрес электронной почты')]/following-sibling::div/div"))?.text?.trim { it <= ' ' }
                            ?: ""
                    val contactPerson =
                        driver.findElementWithoutException(By.xpath("//div[contains(., 'Контактное лицо')]/following-sibling::div/div"))?.text?.trim { it <= ' ' }
                            ?: ""
                    val stmtins = con.prepareStatement(
                        "INSERT INTO ${Prefix}organizer SET full_name = ?, post_address = ?, contact_email = ?, contact_phone = ?, fact_address = ?, contact_person = ?",
                        Statement.RETURN_GENERATED_KEYS
                    ).apply {
                        setString(1, fullnameOrg)
                        setString(2, postalAdr)
                        setString(3, email)
                        setString(4, phone)
                        setString(5, factAdr)
                        setString(6, contactPerson)
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
            val placingWayName =
                driver.findElementWithoutException(By.xpath("//div[contains(., 'Способ закупки')]/following-sibling::div"))?.text?.trim { it <= ' ' }
                    ?: ""
            if (placingWayName != "") {
                idPlacingWay = getPlacingWay(con, placingWayName)
            }
            val idRegion = 0
            val purObj1 =
                driver.findElementWithoutException(By.xpath("//div[@class = 'mat-tooltip-trigger title flex-um']"))?.text?.trim { it <= ' ' }
                    ?: ""
            val purObj2 =
                driver.findElementWithoutException(By.xpath("//div[contains(., 'Краткое описание предмета договора')]/following-sibling::div/div"))?.text?.trim { it <= ' ' }
                    ?: ""
            val purObj = "$purObj1 $purObj2".trim { it <= ' ' }
            val insertTender = con.prepareStatement(
                "INSERT INTO ${Prefix}tender SET id_xml = ?, purchase_number = ?, doc_publish_date = ?, href = ?, purchase_object_info = ?, type_fz = ?, id_organizer = ?, id_placing_way = ?, id_etp = ?, end_date = ?, cancel = ?, date_version = ?, num_version = ?, notice_version = ?, xml = ?, print_form = ?, id_region = ?",
                Statement.RETURN_GENERATED_KEYS
            )
            insertTender.setString(1, tn.purNum)
            insertTender.setString(2, tn.purNum)
            insertTender.setTimestamp(3, Timestamp(pubDate.time))
            insertTender.setString(4, tn.href)
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
            insertTender.setString(15, tn.href)
            insertTender.setString(16, tn.href)
            insertTender.setInt(17, idRegion)
            insertTender.executeUpdate()
            val rt = insertTender.generatedKeys
            if (rt.next()) {
                idTender = rt.getInt(1)
            }
            rt.close()
            insertTender.close()
            if (updated) {
                UpdateTenderEuroTrans++
            } else {
                AddTenderEuroTrans++
            }
            var idLot = 0
            val LotNumber = 1
            val currency =
                driver.findElementWithoutException(By.xpath("//div[contains(., 'Валюта')]/following-sibling::div/div"))?.text?.trim { it <= ' ' }
                    ?: ""
            val insertLot = con.prepareStatement(
                "INSERT INTO ${Prefix}lot SET id_tender = ?, lot_number = ?, currency = ?",
                Statement.RETURN_GENERATED_KEYS
            ).apply {
                setInt(1, idTender)
                setInt(2, LotNumber)
                setString(3, currency)
                executeUpdate()
            }
            val rl = insertLot.generatedKeys
            if (rl.next()) {
                idLot = rl.getInt(1)
            }
            rl.close()
            insertLot.close()
            val attachments = driver.findElements(By.xpath("//um-files-dropdown/a"))
            attachments.forEach {
                val urlAtt = it.getAttribute("href")?.trim { it <= ' ' } ?: ""
                val nameAtt = it.text?.trim { it <= ' ' } ?: ""
                if (urlAtt != "") {
                    con.prepareStatement("INSERT INTO ${Prefix}attachment SET id_tender = ?, file_name = ?, url = ?")
                        .apply {
                            setInt(1, idTender)
                            setString(2, nameAtt)
                            setString(3, urlAtt)
                            executeUpdate()
                            close()
                        }
                }
            }
            val requareList = mutableListOf<String>()
            val rec1 =
                driver.findElementWithoutException(By.xpath("//td[contains(preceding-sibling::td, 'Требования к участникам торгов')]//div[contains(@class, 'translate-text-')]"))?.text?.trim { it <= ' ' }
                    ?: ""
            if (rec1 != "") requareList.add(rec1)
            val rec2 =
                driver.findElementWithoutException(By.xpath("//div[contains(., 'Перечень представляемых участниками закупки документов и требования к их оформлению')]/following-sibling::div/div"))?.text?.trim { it <= ' ' }
                    ?: ""
            if (rec2 != "") requareList.add(rec2)
            requareList.forEach {
                val insertPref = con.prepareStatement("INSERT INTO ${Prefix}requirement SET id_lot = ?, content = ?")
                insertPref.setInt(1, idLot)
                insertPref.setString(2, it)
                with(insertPref) {
                    executeUpdate()
                    close()
                }
            }
            var idCustomer = 0
            if (fullnameOrg != "") {
                val stmtoc =
                    con.prepareStatement("SELECT id_customer FROM ${Prefix}customer WHERE full_name = ? LIMIT 1")
                stmtoc.setString(1, fullnameOrg)
                val rsoc = stmtoc.executeQuery()
                if (rsoc.next()) {
                    idCustomer = rsoc.getInt(1)
                    rsoc.close()
                    stmtoc.close()
                } else {
                    rsoc.close()
                    stmtoc.close()
                    val stmtins = con.prepareStatement(
                        "INSERT INTO ${Prefix}customer SET full_name = ?, is223=1, reg_num = ?",
                        Statement.RETURN_GENERATED_KEYS
                    )
                    stmtins.setString(1, fullnameOrg)
                    stmtins.setString(2, java.util.UUID.randomUUID().toString())
                    stmtins.executeUpdate()
                    val rsoi = stmtins.generatedKeys
                    if (rsoi.next()) {
                        idCustomer = rsoi.getInt(1)
                    }
                    rsoi.close()
                    stmtins.close()
                }
            }
            val delivPlace1 =
                driver.findElementWithoutException(By.xpath("//div[contains(., 'Регион')]/following-sibling::div"))?.text?.trim { it <= ' ' }
                    ?: ""
            val delivPlace2 =
                driver.findElementWithoutException(By.xpath("//div[contains(., 'Адрес объекта')]/following-sibling::div/div"))?.text?.trim { it <= ' ' }
                    ?: ""
            val delivPlace = "$delivPlace1 $delivPlace2".trim { it <= ' ' }
            var delivTerm1 =
                driver.findElementWithoutException(By.xpath("//div[contains(., 'Условия оплаты')]/following-sibling::div/div"))?.text?.trim { it <= ' ' }
                    ?: ""
            if (delivTerm1 != "") delivTerm1 = "Условия оплаты: $delivTerm1"
            var delivTerm2 =
                driver.findElementWithoutException(By.xpath("//div[contains(., 'Отсрочка платежа')]/following-sibling::div/div"))?.text?.trim { it <= ' ' }
                    ?: ""
            if (delivTerm2 != "") delivTerm2 = "Отсрочка платежа: $delivTerm2"
            var delivTerm3 =
                driver.findElementWithoutException(By.xpath("//div[contains(., 'Сроки поставки')]/following-sibling::div/div"))?.text?.trim { it <= ' ' }
                    ?: ""
            if (delivTerm3 != "") delivTerm3 =
                "Сроки поставки: $delivTerm3"
            var delivTerm4 =
                driver.findElementWithoutException(By.xpath("//div[contains(., 'Проект')]/following-sibling::div"))?.text?.trim { it <= ' ' }
                    ?: ""
            if (delivTerm4 != "") delivTerm4 = "Проект: $delivTerm4"
            var delivTerm = ""
            if (delivTerm1 != "" || delivTerm2 != "" || delivTerm3 != "" || delivTerm4 != "") {
                delivTerm = "$delivTerm1\n $delivTerm2\n $delivTerm3\n $delivTerm4".trim { it <= ' ' }
            }
            if (delivPlace != "" || delivTerm != "") {
                val insertCusRec =
                    con.prepareStatement("INSERT INTO ${Prefix}customer_requirement SET id_lot = ?, id_customer = ?, delivery_place = ?, delivery_term = ?")
                        .apply {
                            setInt(1, idLot)
                            setInt(2, idCustomer)
                            setString(3, delivPlace)
                            setString(4, delivTerm)
                            executeUpdate()
                            close()
                        }
            }
            val purobj1 =
                driver.findElements(By.xpath("//table[preceding-sibling::div[contains(., 'Позиции спецификации:')] and  contains(@class, 'competitive-table-data')]//tr[position() > 1]"))
            purobj1.forEach {
                val name = it.findElementWithoutException(By.xpath(".//td[3]"))?.text?.trim { it <= ' ' }
                    ?: ""
                val okei = it.findElementWithoutException(By.xpath(".//td[4]"))?.text?.trim { it <= ' ' }
                    ?: ""
                val quantity = it.findElementWithoutException(By.xpath(".//td[5]"))?.text?.trim { it <= ' ' }
                    ?: ""
                val insertPurObj =
                    con.prepareStatement("INSERT INTO ${Prefix}purchase_object SET id_lot = ?, id_customer = ?, name = ?, okei = ?, quantity_value = ?, customer_quantity_value = ?")
                        .apply {
                            setInt(1, idLot)
                            setInt(2, idCustomer)
                            setString(3, name)
                            setString(4, okei)
                            setString(5, quantity)
                            setString(6, quantity)
                            executeUpdate()
                            close()
                        }
            }
            var purobj2 = mutableListOf<WebElement>()
            val toggler =
                driver.findElementWithoutException(By.xpath("//tr[@class = 'purchase-items-toggler-row' and @onclick]"))
            if (toggler != null) {
                toggler.click()
                Thread.sleep(3000)
                purobj2 =
                    driver.findElements(By.xpath("//tr[contains(@class, 'purchase-items-table')]//table/tbody/tr[position() > 1]"))
            }

            purobj2.forEach {
                val name1 = it.findElementWithoutException(By.xpath(".//td[2]"))?.text?.trim { it <= ' ' }
                    ?: ""
                val name2 = it.findElementWithoutException(By.xpath(".//td[6]"))?.text?.trim { it <= ' ' }
                    ?: ""
                val name = "$name1 $name2".trim { it <= ' ' }
                val okei = it.findElementWithoutException(By.xpath(".//td[5]"))?.text?.trim { it <= ' ' }
                    ?: ""
                val quantity = it.findElementWithoutException(By.xpath(".//td[4]"))?.text?.trim { it <= ' ' }
                    ?: ""
                val insertPurObj =
                    con.prepareStatement("INSERT INTO ${Prefix}purchase_object SET id_lot = ?, id_customer = ?, name = ?, okei = ?, quantity_value = ?, customer_quantity_value = ?")
                        .apply {
                            setInt(1, idLot)
                            setInt(2, idCustomer)
                            setString(3, name)
                            setString(4, okei)
                            setString(5, quantity)
                            setString(6, quantity)
                            executeUpdate()
                            close()
                        }
            }
            if (purobj1.isEmpty() && purobj2.isEmpty()) {
                val insertPurObj =
                    con.prepareStatement("INSERT INTO ${Prefix}purchase_object SET id_lot = ?, id_customer = ?, name = ?")
                insertPurObj.setInt(1, idLot)
                insertPurObj.setInt(2, idCustomer)
                insertPurObj.setString(3, purObj)
                insertPurObj.executeUpdate()
                insertPurObj.close()
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
        })


    }

    companion object TypeFz {
        const val typeFz = 292
    }
}