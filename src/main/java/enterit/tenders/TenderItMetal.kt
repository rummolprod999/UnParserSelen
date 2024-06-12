package enterit.tenders

import enterit.*
import enterit.dataclasses.SafmargT
import enterit.parsers.ParserItMetal
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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class TenderItMetal(
    val tn: SafmargT<String>,
    val driver: ChromeDriver,
    val wait: WebDriverWait,
) : TenderAbstract(),
    ITender {
    inner class Doc(
        val DocName: String,
        val Href: String,
    )

    val docList = mutableListOf<Doc>()

    companion object TypeFz {
        const val typeFz = 407
    }

    init {
        etpName =
            "Торгово-закупочная площадка горнодобывающих, металлургических и металлообрабатывающих предприятий ЛотЭксперт"
        etpUrl = "https://etp.metal-it.ru/"
    }

    override fun parsing() {
        driver.get(tn.href)
        val wait = WebDriverWait(driver, ParserItMetal.timeoutB)
        Thread.sleep(5000)
        try {
            wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(preceding-sibling::div, 'Начало представления предложений')]"),
                ),
            )
        } catch (e: Exception) {
            logger("cannot find expected startDate", driver.pageSource)
            return
        }
        var datePubT =
            driver
                .findElementWithoutException(
                    By.xpath("//div[contains(preceding-sibling::div, 'Начало представления предложений')]"),
                )?.text
                ?.trim {
                    it <=
                        ' '
                }
                ?: ""
        var pubDate = Date()
        if (datePubT.contains("Вчера в")) {
            datePubT =
                datePubT.replace(
                    "Вчера в",
                    SimpleDateFormat("dd.MM.yyyy").format(
                        Date.from(
                            LocalDate
                                .now()
                                .minusDays(1)
                                .atStartOfDay(
                                    ZoneId.systemDefault(),
                                ).toInstant(),
                        ),
                    ),
                )
        }
        if (datePubT.contains("Сегодня в")) {
            datePubT =
                datePubT.replace(
                    "Сегодня в",
                    SimpleDateFormat("dd.MM.yyyy").format(Date()),
                )
        }
        datePubT = datePubT.getDataFromRegexp("""(\d{2}\.\d{2}\.\d{4}\s+\d{2}:\d{2})""")
        pubDate = datePubT.getDateFromString(formatterGpn)
        var endDateT =
            driver
                .findElementWithoutException(
                    By.xpath("//div[contains(preceding-sibling::div, 'Окончание представления предложений')]"),
                )?.text
                ?.trim {
                    it <=
                        ' '
                }
                ?: ""
        var endDate = Date()
        if (endDateT.contains("Вчера в")) {
            endDateT =
                endDateT.replace(
                    "Вчера в",
                    SimpleDateFormat("dd.MM.yyyy").format(
                        Date.from(
                            LocalDate
                                .now()
                                .minusDays(1)
                                .atStartOfDay(
                                    ZoneId.systemDefault(),
                                ).toInstant(),
                        ),
                    ),
                )
        }
        if (endDateT.contains("Завтра в")) {
            endDateT =
                endDateT.replace(
                    "Завтра в",
                    SimpleDateFormat("dd.MM.yyyy").format(
                        Date.from(
                            LocalDate
                                .now()
                                .plusDays(1)
                                .atStartOfDay(
                                    ZoneId.systemDefault(),
                                ).toInstant(),
                        ),
                    ),
                )
        }
        if (endDateT.contains("Сегодня в")) {
            endDateT =
                endDateT.replace(
                    "Сегодня в",
                    SimpleDateFormat("dd.MM.yyyy").format(Date()),
                )
        }
        endDateT = endDateT.getDataFromRegexp("""(\d{2}\.\d{2}\.\d{4}\s+\d{2}:\d{2})""")
        endDate = endDateT.getDateFromString(formatterGpn)
        if (pubDate == Date(0L) || endDate == Date(0L)) {
            logger("cannot find dates in tender", tn.href, datePubT, endDateT)
            return
        }
        val status =
            driver.findElementWithoutException(By.xpath("//div[contains(@class , 'status-button')]/div/div"))?.text?.trim { it <= ' ' }
                ?: ""
        DriverManager.getConnection(UrlConnect, UserDb, PassDb).use(
            fun(con: Connection) {
                val dateVer = Date()
                val stmt0 =
                    con
                        .prepareStatement(
                            "SELECT id_tender FROM ${Prefix}tender WHERE purchase_number = ? AND doc_publish_date = ? AND type_fz = ? AND end_date = ? AND notice_version = ?",
                        ).apply {
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
                    driver
                        .findElementWithoutException(
                            By.xpath("//div[contains(@class, 'organization__info__title')]"),
                        )?.text
                        ?.trim { it <= ' ' }
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
                            driver
                                .findElementWithoutException(
                                    By.xpath(
                                        "//td[contains(preceding-sibling::td, 'Почтовый адрес')]//div[contains(@class, 'translate-text-')]",
                                    ),
                                )?.text
                                ?.trim {
                                    it <=
                                        ' '
                                }
                                ?: ""
                        val factAdr =
                            driver
                                .findElementWithoutException(
                                    By.xpath(
                                        "//td[contains(preceding-sibling::td, 'Юридический адрес (место нахождения)')]//div[contains(@class, 'translate-text-')]",
                                    ),
                                )?.text
                                ?.trim {
                                    it <=
                                        ' '
                                }
                                ?: ""
                        val email =
                            driver
                                .findElementWithoutException(
                                    By.xpath("//div[contains(preceding-sibling::div, 'Адрес электронной почты')]"),
                                )?.text
                                ?.trim {
                                    it <=
                                        ' '
                                }
                                ?: ""
                        val phone =
                            driver
                                .findElementWithoutException(
                                    By.xpath("//div[contains(preceding-sibling::div, 'Номер контактного телефона')]"),
                                )?.text
                                ?.trim {
                                    it <=
                                        ' '
                                }
                                ?: ""
                        val contactPerson =
                            driver
                                .findElementWithoutException(
                                    By.xpath("//div[contains(preceding-sibling::div, 'Контактное лицо')]"),
                                )?.text
                                ?.trim {
                                    it <=
                                        ' '
                                }
                                ?: ""
                        val stmtins =
                            con
                                .prepareStatement(
                                    "INSERT INTO ${Prefix}organizer SET full_name = ?, post_address = ?, contact_email = ?, contact_phone = ?, fact_address = ?, contact_person = ?",
                                    Statement.RETURN_GENERATED_KEYS,
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
                var scoringDateT =
                    driver
                        .findElementWithoutException(
                            By.xpath("//div[contains(preceding-sibling::div, 'Дата подведения итогов')]/div"),
                        )?.text
                        ?.trim {
                            it <=
                                ' '
                        }
                        ?: ""
                var scoringDate = Date()
                if (scoringDateT.contains("Вчера в")) {
                    scoringDateT =
                        scoringDateT.replace(
                            "Вчера в",
                            SimpleDateFormat("dd.MM.yyyy").format(
                                Date.from(
                                    LocalDate
                                        .now()
                                        .minusDays(1)
                                        .atStartOfDay(
                                            ZoneId.systemDefault(),
                                        ).toInstant(),
                                ),
                            ),
                        )
                }
                if (scoringDateT.contains("Завтра в")) {
                    scoringDateT =
                        scoringDateT.replace(
                            "Завтра в",
                            SimpleDateFormat("dd.MM.yyyy").format(
                                Date.from(
                                    LocalDate
                                        .now()
                                        .plusDays(1)
                                        .atStartOfDay(
                                            ZoneId.systemDefault(),
                                        ).toInstant(),
                                ),
                            ),
                        )
                }
                if (scoringDateT.contains("Сегодня в")) {
                    scoringDateT =
                        scoringDateT.replace(
                            "Сегодня в",
                            SimpleDateFormat("dd.MM.yyyy").format(Date()),
                        )
                }
                scoringDateT = scoringDateT.getDataFromRegexp("""(\d{2}\.\d{2}\.\d{4})""")
                scoringDate = scoringDateT.getDateFromString(formatterOnlyDate)

                val idEtp = getEtp(con)
                var idPlacingWay = 0
                var idTender = 0
                val placingWayName =
                    driver.findElementWithoutException(By.xpath("//div[contains(preceding-sibling::div, 'Способ закупки')]"))?.text?.trim {
                        it <=
                            ' '
                    }
                        ?: ""
                if (placingWayName != "") {
                    idPlacingWay = getPlacingWay(con, placingWayName)
                }
                val delivPlace1 =
                    driver
                        .findElementWithoutException(
                            By.xpath("//div[contains(preceding-sibling::div, 'Регион поставки, выполнения работ')]"),
                        )?.text
                        ?.trim {
                            it <=
                                ' '
                        }
                        ?: ""

                val idRegion = getIdRegion(con, delivPlace1.lowercase())
                val purObj1 =
                    driver.findElementWithoutException(By.xpath("//div[contains(@class, 'title flex-um')]"))?.text?.trim { it <= ' ' }
                        ?: ""
                val purObj2 =
                    driver
                        .findElementWithoutException(
                            By.xpath("//div[contains(preceding-sibling::div, 'Краткое описание предмета договора')]"),
                        )?.text
                        ?.trim {
                            it <=
                                ' '
                        }
                        ?: ""
                val purObj = "${tn.purName} $purObj1 $purObj2".trim { it <= ' ' }
                val insertTender =
                    con.prepareStatement(
                        "INSERT INTO ${Prefix}tender SET id_xml = ?, purchase_number = ?, doc_publish_date = ?, href = ?, purchase_object_info = ?, type_fz = ?, id_organizer = ?, id_placing_way = ?, id_etp = ?, end_date = ?, cancel = ?, date_version = ?, num_version = ?, notice_version = ?, xml = ?, print_form = ?, id_region = ?, scoring_date = ?",
                        Statement.RETURN_GENERATED_KEYS,
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
                insertTender.setTimestamp(18, Timestamp(scoringDate.time))
                insertTender.executeUpdate()
                val rt = insertTender.generatedKeys
                if (rt.next()) {
                    idTender = rt.getInt(1)
                }
                rt.close()
                insertTender.close()
                if (updated) {
                    UpdateTenderItMetal++
                } else {
                    AddTenderItMetal++
                }
                val attachments = driver.findElements(By.xpath("//a[starts-with(@href, '/files/get')]"))
                attachments.forEach {
                    val urlAtt = it.getAttribute("href")?.trim { it <= ' ' } ?: ""
                    if (urlAtt != "") {
                        con
                            .prepareStatement("INSERT INTO ${Prefix}attachment SET id_tender = ?, file_name = ?, url = ?")
                            .apply {
                                setInt(1, idTender)
                                setString(2, "Скачать все приложенные файлы")
                                setString(3, urlAtt)
                                executeUpdate()
                                close()
                            }
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
                        val stmtins =
                            con.prepareStatement(
                                "INSERT INTO ${Prefix}customer SET full_name = ?, is223=1, reg_num = ?",
                                Statement.RETURN_GENERATED_KEYS,
                            )
                        stmtins.setString(1, fullnameOrg)
                        stmtins.setString(
                            2,
                            java.util.UUID
                                .randomUUID()
                                .toString(),
                        )
                        stmtins.executeUpdate()
                        val rsoi = stmtins.generatedKeys
                        if (rsoi.next()) {
                            idCustomer = rsoi.getInt(1)
                        }
                        rsoi.close()
                        stmtins.close()
                    }
                }
                val requareList = mutableListOf<String>()
                val rec1 =
                    driver
                        .findElementWithoutException(
                            By.xpath("//div[contains(preceding-sibling::div, 'Требования к участникам')]"),
                        )?.text
                        ?.trim {
                            it <=
                                ' '
                        }
                        ?: ""
                if (rec1 != "") requareList.add(rec1)
                val rec2 =
                    driver
                        .findElementWithoutException(
                            By.xpath(
                                "//div[contains(preceding-sibling::div, 'Перечень представляемых участниками закупки документов и требования к их оформлению')]",
                            ),
                        )?.text
                        ?.trim {
                            it <=
                                ' '
                        }
                        ?: ""
                if (rec2 != "") requareList.add(rec2)

                val delivPlace2 =
                    driver
                        .findElementWithoutException(
                            By.xpath("//div[contains(., 'Адрес объекта')]/following-sibling::div/div"),
                        )?.text
                        ?.trim {
                            it <=
                                ' '
                        }
                        ?: ""
                val delivPlace = "$delivPlace1 $delivPlace2".trim { it <= ' ' }
                var delivTerm1 =
                    driver
                        .findElementWithoutException(
                            By.xpath("//div[contains(., 'Условия оплаты')]/following-sibling::div/div"),
                        )?.text
                        ?.trim {
                            it <=
                                ' '
                        }
                        ?: ""
                if (delivTerm1 != "") delivTerm1 = "Условия оплаты: $delivTerm1"
                var delivTerm2 =
                    driver
                        .findElementWithoutException(
                            By.xpath("//div[contains(., 'Обеспечение суммы резерва качества')]/following-sibling::div/div"),
                        )?.text
                        ?.trim {
                            it <=
                                ' '
                        }
                        ?: ""
                if (delivTerm2 != "") {
                    delivTerm2 =
                        "Обеспечение суммы резерва качества на гарантийный срок после подписания итогового акта: $delivTerm2"
                }
                var delivTerm3 =
                    driver
                        .findElementWithoutException(
                            By.xpath(
                                "//td[contains(preceding-sibling::td, 'Требования к сроку и объему представления гарантий качества товара, работ, услуг, к обслуживанию товара, к расходам на эксплуатацию товара')]//div[contains(@class, 'translate-text-')]",
                            ),
                        )?.text
                        ?.trim {
                            it <=
                                ' '
                        }
                        ?: ""
                if (delivTerm3 != "") {
                    delivTerm3 =
                        "Требования к сроку и объему представления гарантий качества товара, работ, услуг, к обслуживанию товара, к расходам на эксплуатацию товара: $delivTerm3"
                }
                var delivTerm4 =
                    driver
                        .findElementWithoutException(
                            By.xpath(
                                "//td[contains(preceding-sibling::td, 'Сроки поставки товаров, выполнения работ, оказания услуг')]//div[contains(@class, 'translate-text-')]",
                            ),
                        )?.text
                        ?.trim {
                            it <=
                                ' '
                        }
                        ?: ""
                if (delivTerm4 != "") {
                    delivTerm4 =
                        "Сроки поставки товаров, выполнения работ, оказания услуг: $delivTerm4"
                }
                var delivTerm = ""
                if (delivTerm1 != "" || delivTerm2 != "" || delivTerm3 != "" || delivTerm4 != "") {
                    delivTerm = "$delivTerm1\n $delivTerm2\n $delivTerm3\n $delivTerm4".trim { it <= ' ' }
                }
                val currency =
                    driver.findElementWithoutException(By.xpath("//div[contains(preceding-sibling::div, 'Валюта')]"))?.text?.trim {
                        it <=
                            ' '
                    }
                        ?: ""
                val lots = driver.findElements(By.xpath("//mat-expansion-panel"))
                lots.forEachIndexed { index, lot ->
                    var idLot = 0
                    val LotNumber = index + 1
                    var lotName =
                        lot
                            .findElementWithoutException(
                                By.xpath(".//mat-expansion-panel-header//div[contains(@class, 'column-title')]"),
                            )?.text
                            ?.trim {
                                it <=
                                    ' '
                            }
                            ?: ""
                    if (lotName == "") {
                        lotName =
                            lot
                                .findElementWithoutException(
                                    By.xpath(".//div[. = 'Краткое описание предмета договора']/following-sibling::div/div"),
                                )?.text
                                ?.trim {
                                    it <=
                                        ' '
                                }
                                ?: ""
                    }
                    val nmck =
                        lot
                            .findElementWithoutException(
                                By.xpath(".//div[contains(., 'Расчетная стоимость')]/following-sibling::div/div/span"),
                            )?.text
                            ?.trim {
                                it <=
                                    ' '
                            }?.deleteAllWhiteSpace()
                            ?.replace(",", ".")
                            ?: ""
                    val insertLot =
                        con
                            .prepareStatement(
                                "INSERT INTO ${Prefix}lot SET id_tender = ?, lot_number = ?, currency = ?, lot_name = ?, max_price = ?",
                                Statement.RETURN_GENERATED_KEYS,
                            ).apply {
                                setInt(1, idTender)
                                setInt(2, LotNumber)
                                setString(3, currency)
                                setString(4, lotName)
                                setString(5, nmck)
                                executeUpdate()
                            }
                    val rl = insertLot.generatedKeys
                    if (rl.next()) {
                        idLot = rl.getInt(1)
                    }
                    rl.close()
                    insertLot.close()
                    requareList.forEach {
                        val insertPref =
                            con.prepareStatement("INSERT INTO ${Prefix}requirement SET id_lot = ?, content = ?")
                        insertPref.setInt(1, idLot)
                        insertPref.setString(2, it)
                        with(insertPref) {
                            executeUpdate()
                            close()
                        }
                    }
                    if (delivPlace != "" || delivTerm != "") {
                        con
                            .prepareStatement(
                                "INSERT INTO ${Prefix}customer_requirement SET id_lot = ?, id_customer = ?, delivery_place = ?, delivery_term = ?",
                            ).apply {
                                setInt(1, idLot)
                                setInt(2, idCustomer)
                                setString(3, delivPlace)
                                setString(4, delivTerm)
                                executeUpdate()
                                close()
                            }
                    }
                    val purobj1 =
                        driver.findElements(
                            By.xpath(
                                "//mat-card-title[. = 'Спецификация поставки']/following-sibling::mat-card-content//div[@class = 'k-grid-table-wrap' and @role = 'presentation']//tr",
                            ),
                        )
                    purobj1.forEach {
                        val test =
                            it.findElementWithoutException(By.xpath(".//td[9]"))?.text?.trim { it <= ' ' }
                                ?: ""
                        if (true) {
                            val name1 =
                                it.findElementWithoutException(By.xpath(".//td[2]"))?.text?.trim { it <= ' ' }
                                    ?: ""
                            val name2 =
                                it.findElementWithoutException(By.xpath(".//td[3]"))?.text?.trim { it <= ' ' }
                                    ?: ""
                            val name = "$name1 $name2".trim { it <= ' ' }
                            val okei =
                                it.findElementWithoutException(By.xpath(".//td[5]"))?.text?.trim { it <= ' ' }
                                    ?: ""
                            val quantity =
                                it
                                    .findElementWithoutException(By.xpath(".//td[4]"))
                                    ?.text
                                    ?.trim { it <= ' ' }
                                    ?.replace(",", "")
                                    ?.deleteAllWhiteSpace()
                                    ?: ""
                            val insertPurObj =
                                con
                                    .prepareStatement(
                                        "INSERT INTO ${Prefix}purchase_object SET id_lot = ?, id_customer = ?, name = ?, okei = ?, quantity_value = ?, customer_quantity_value = ?",
                                    ).apply {
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
                        val delivTermT =
                            it.findElementWithoutException(By.xpath(".//td[6]"))?.text?.trim { it <= ' ' }
                                ?: ""
                        val delivTerm =
                            if (delivTermT != "") {
                                "Срок поставки: $delivTerm"
                            } else {
                                ""
                            }
                        if (delivTerm != "") {
                            con
                                .prepareStatement(
                                    "INSERT INTO ${Prefix}customer_requirement SET id_lot = ?, id_customer = ?, delivery_place = ?, delivery_term = ?",
                                ).apply {
                                    setInt(1, idLot)
                                    setInt(2, idCustomer)
                                    setString(3, delivPlace)
                                    setString(4, delivTerm)
                                    executeUpdate()
                                    close()
                                }
                        }
                    }

                    var purobj2 = mutableListOf<WebElement>()
                    val toggler =
                        lot.findElementWithoutException(By.xpath("//tr[@class = 'purchase-items-toggler-row' and @onclick]"))
                    if (toggler != null) {
                        toggler.click()
                        Thread.sleep(3000)
                        purobj2 =
                            lot.findElements(By.xpath("//tr[contains(@class, 'purchase-items-table')]//table/tbody/tr[position() > 1]"))
                    }

                    purobj2.forEach {
                        val name1 =
                            it.findElementWithoutException(By.xpath(".//td[2]"))?.text?.trim { it <= ' ' }
                                ?: ""
                        val name2 =
                            it.findElementWithoutException(By.xpath(".//td[6]"))?.text?.trim { it <= ' ' }
                                ?: ""
                        val name = "$name1 $name2".trim { it <= ' ' }
                        val okei =
                            it.findElementWithoutException(By.xpath(".//td[5]"))?.text?.trim { it <= ' ' }
                                ?: ""
                        val quantity =
                            it.findElementWithoutException(By.xpath(".//td[4]"))?.text?.trim { it <= ' ' }
                                ?: ""
                        con
                            .prepareStatement(
                                "INSERT INTO ${Prefix}purchase_object SET id_lot = ?, id_customer = ?, name = ?, okei = ?, quantity_value = ?, customer_quantity_value = ?",
                            ).apply {
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
                        con
                            .prepareStatement("INSERT INTO ${Prefix}purchase_object SET id_lot = ?, id_customer = ?, name = ?, sum = ?")
                            .apply {
                                setInt(1, idLot)
                                setInt(2, idCustomer)
                                setString(3, lotName)
                                setString(4, nmck)
                                executeUpdate()
                                close()
                            }
                    }
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
