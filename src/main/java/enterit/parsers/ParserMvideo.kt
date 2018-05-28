package enterit.parsers

import enterit.UserMvideo
import enterit.mvideosoup.DTGetProcListResponse
import enterit.tenders.TenderMvideo
import enterit.tools.logger
import javax.xml.ws.BindingProvider


class ParserMvideo : Iparser {
    override fun parser() {
        val t = siGetProcList2SRMSO("")
        val tns = t.tenders
        val comp = t.header.company
        tns.item?.forEach {
            try {
                val ten = TenderMvideo(it, comp)
                ten.parsing()
            } catch (e: Exception) {
                logger("error in TenderMvideo.parsing()", e.stackTrace, e)
            }
        }


    }

    private fun siGetProcList2SRMSO(mtGetProcListRequest: String): DTGetProcListResponse {
        val service = enterit.mvideosoup.SIGetProcList2SRMSOService()
        val port = service.getHTTPSPort()
        val reqContext = (port as BindingProvider).requestContext
        reqContext[BindingProvider.USERNAME_PROPERTY] = UserMvideo
        reqContext[BindingProvider.PASSWORD_PROPERTY] = "fs234GF&*a3"
        return port.siGetProcList2SRMSO(mtGetProcListRequest)
    }
}