package enterit.tenders

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import enterit.Prefix
import enterit.tools.downloadFromUrl
import enterit.tools.getConformity
import enterit.tools.getRegion
import java.lang.reflect.Type
import java.sql.Connection
import java.sql.Statement

abstract class TenderAbstract {
    var etpName = ""
    var etpUrl = ""
    fun getEtp(con: Connection): Int {
        var IdEtp = 0
        val stmto = con.prepareStatement("SELECT id_etp FROM ${Prefix}etp WHERE name = ? AND url = ? LIMIT 1")
        stmto.setString(1, etpName)
        stmto.setString(2, etpUrl)
        val rso = stmto.executeQuery()
        if (rso.next()) {
            IdEtp = rso.getInt(1)
            rso.close()
            stmto.close()
        } else {
            rso.close()
            stmto.close()
            val stmtins = con.prepareStatement("INSERT INTO ${Prefix}etp SET name = ?, url = ?, conf=0", Statement.RETURN_GENERATED_KEYS)
            stmtins.setString(1, etpName)
            stmtins.setString(2, etpUrl)
            stmtins.executeUpdate()
            val rsoi = stmtins.generatedKeys
            if (rsoi.next()) {
                IdEtp = rsoi.getInt(1)
            }
            rsoi.close()
            stmtins.close()
        }
        return IdEtp
    }

    fun getAttachments(idTender: Int, con: Connection, purNum: String) {
        val page = downloadFromUrl("https://zmo-new-webapi.rts-tender.ru/api/Trade/$purNum/GetTradeDocuments")
        if (page == "") {
            return
        }
        val gson = Gson()
        val listType: Type = object : TypeToken<List<RtsAtt?>?>() {}.type
        val docs: List<RtsAtt> = gson.fromJson(page, listType)
        docs.forEach {
            if (it.FileName != null && it.Url != null) {
                con.prepareStatement("INSERT INTO ${Prefix}attachment SET id_tender = ?, file_name = ?, url = ?").apply {
                    setInt(1, idTender)
                    setString(2, it.FileName)
                    setString(3, it.Url)
                    executeUpdate()
                    close()
                }
            }
        }

    }

    fun getPlacingWay(con: Connection, placingWay: String): Int {
        var idPlacingWay = 0
        val stmto = con.prepareStatement("SELECT id_placing_way FROM ${Prefix}placing_way WHERE name = ? LIMIT 1")
        stmto.setString(1, placingWay)
        val rso = stmto.executeQuery()
        if (rso.next()) {
            idPlacingWay = rso.getInt(1)
            rso.close()
            stmto.close()
        } else {
            rso.close()
            stmto.close()
            val conf = getConformity(placingWay)
            val stmtins = con.prepareStatement("INSERT INTO ${Prefix}placing_way SET name = ?, conformity = ?", Statement.RETURN_GENERATED_KEYS)
            stmtins.setString(1, placingWay)
            stmtins.setInt(2, conf)
            stmtins.executeUpdate()
            val rsoi = stmtins.generatedKeys
            if (rsoi.next()) {
                idPlacingWay = rsoi.getInt(1)
            }
            rsoi.close()
            stmtins.close()

        }
        return idPlacingWay
    }

    fun getIdRegion(con: Connection, reg: String): Int {
        var idReg = 0
        val re = getRegion(reg)
        if (re != "") {
            val stmto = con.prepareStatement("SELECT id FROM region WHERE name LIKE ?")
            stmto.setString(1, "%$re%")
            val rso = stmto.executeQuery()
            if (rso.next()) {
                idReg = rso.getInt(1)
                rso.close()
                stmto.close()
            } else {
                rso.close()
                stmto.close()
            }
        }
        return idReg

    }

    class RtsAtt {
        var FileName: String? = null
        var Url: String? = null
    }
}