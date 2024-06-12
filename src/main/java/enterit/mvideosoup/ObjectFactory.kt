package enterit.mvideosoup

import javax.xml.bind.JAXBElement
import javax.xml.bind.annotation.XmlElementDecl
import javax.xml.bind.annotation.XmlRegistry
import javax.xml.namespace.QName

/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the mvideosoap package.
 *
 * An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */

/**
 * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: mvideosoap
 *
 */
@XmlRegistry
class ObjectFactory {
    /**
     * Create an instance of [DTGetProcListResponse]
     *
     */
    fun createDTGetProcListResponse(): DTGetProcListResponse = DTGetProcListResponse()

    /**
     * Create an instance of [DTGetProcListResponse.Tenders]
     *
     */
    fun createDTGetProcListResponseTenders(): DTGetProcListResponse.Tenders = DTGetProcListResponse.Tenders()

    /**
     * Create an instance of [DTGetProcListResponse.Header]
     *
     */
    fun createDTGetProcListResponseHeader(): DTGetProcListResponse.Header = DTGetProcListResponse.Header()

    /**
     * Create an instance of [DTGetProcListResponse.Tenders.Item]
     *
     */
    fun createDTGetProcListResponseTendersItem(): DTGetProcListResponse.Tenders.Item = DTGetProcListResponse.Tenders.Item()

    /**
     * Create an instance of [DTGetProcListResponse.Header.Company]
     *
     */
    fun createDTGetProcListResponseHeaderCompany(): DTGetProcListResponse.Header.Company = DTGetProcListResponse.Header.Company()

    /**
     * Create an instance of [DTGetProcListResponse.Header.Source]
     *
     */
    fun createDTGetProcListResponseHeaderSource(): DTGetProcListResponse.Header.Source = DTGetProcListResponse.Header.Source()

    /**
     * Create an instance of [JAXBElement]`<`[String]`>`}
     *
     */
    @XmlElementDecl(namespace = "http://mvideo.ru/SRM/GetProcList", name = "MT_GetProcList_Request")
    fun createMTGetProcListRequest(value: String): JAXBElement<String> =
        JAXBElement(_MTGetProcListRequest_QNAME, String::class.java, null, value)

    /**
     * Create an instance of [JAXBElement]`<`[DTGetProcListResponse]`>`}
     *
     */
    @XmlElementDecl(namespace = "http://mvideo.ru/SRM/GetProcList", name = "MT_GetProcList_Response")
    fun createMTGetProcListResponse(value: DTGetProcListResponse): JAXBElement<DTGetProcListResponse> =
        JAXBElement<DTGetProcListResponse>(
            _MTGetProcListResponse_QNAME,
            DTGetProcListResponse::class.java,
            null,
            value,
        )

    companion object {
        private val _MTGetProcListRequest_QNAME = QName("http://mvideo.ru/SRM/GetProcList", "MT_GetProcList_Request")
        private val _MTGetProcListResponse_QNAME = QName("http://mvideo.ru/SRM/GetProcList", "MT_GetProcList_Response")
    }
}
