package enterit.mvideosoup

import javax.jws.WebMethod
import javax.jws.WebParam
import javax.jws.WebResult
import javax.jws.WebService
import javax.jws.soap.SOAPBinding
import javax.xml.bind.annotation.XmlSeeAlso


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.6-1b01
 * Generated source version: 2.2
 *
 */
@WebService(name = "SI_GetProcList_2_SRM_SO", targetNamespace = "http://mvideo.ru/SRM/GetProcList")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso(ObjectFactory::class)
interface SIGetProcList2SRMSO {


    /**
     *
     * @param mtGetProcListRequest
     * @return
     * returns mvideosoap.DTGetProcListResponse
     */
    @WebMethod(operationName = "SI_GetProcList_2_SRM_SO", action = "http://sap.com/xi/WebService/soap1.1")
    @WebResult(
        name = "MT_GetProcList_Response",
        targetNamespace = "http://mvideo.ru/SRM/GetProcList",
        partName = "MT_GetProcList_Response"
    )
    fun siGetProcList2SRMSO(
        @WebParam(
            name = "MT_GetProcList_Request",
            targetNamespace = "http://mvideo.ru/SRM/GetProcList",
            partName = "MT_GetProcList_Request"
        )
        mtGetProcListRequest: String
    ): DTGetProcListResponse

}
