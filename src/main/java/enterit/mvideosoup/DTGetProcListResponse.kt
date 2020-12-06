package enterit.mvideosoup


import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlType


/**
 *
 * Java class for DT_GetProcList_Response complex type.
 *
 *
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="DT_GetProcList_Response">
 * &lt;complexContent>
 * &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 * &lt;sequence>
 * &lt;element name="Header">
 * &lt;complexType>
 * &lt;complexContent>
 * &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 * &lt;sequence>
 * &lt;element name="Company" minOccurs="0">
 * &lt;complexType>
 * &lt;complexContent>
 * &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 * &lt;sequence>
 * &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 * &lt;element name="INN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 * &lt;element name="KPP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 * &lt;element name="Address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 * &lt;element name="Phone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 * &lt;element name="Email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 * &lt;/sequence>
 * &lt;/restriction>
 * &lt;/complexContent>
 * &lt;/complexType>
 * &lt;/element>
 * &lt;element name="Source" minOccurs="0">
 * &lt;complexType>
 * &lt;complexContent>
 * &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 * &lt;sequence>
 * &lt;element name="Version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 * &lt;/sequence>
 * &lt;/restriction>
 * &lt;/complexContent>
 * &lt;/complexType>
 * &lt;/element>
 * &lt;/sequence>
 * &lt;/restriction>
 * &lt;/complexContent>
 * &lt;/complexType>
 * &lt;/element>
 * &lt;element name="Tenders">
 * &lt;complexType>
 * &lt;complexContent>
 * &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 * &lt;sequence>
 * &lt;element name="Item" maxOccurs="unbounded" minOccurs="0">
 * &lt;complexType>
 * &lt;complexContent>
 * &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 * &lt;sequence>
 * &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 * &lt;element name="ProcessTypeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 * &lt;element name="ProcessTypeText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 * &lt;element name="ChangeDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 * &lt;element name="PurchaseStartDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 * &lt;element name="PurchaseEndDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 * &lt;element name="ContractDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 * &lt;element name="Price" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 * &lt;element name="Comment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 * &lt;element name="Link" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 * &lt;element name="Text" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 * &lt;/sequence>
 * &lt;/restriction>
 * &lt;/complexContent>
 * &lt;/complexType>
 * &lt;/element>
 * &lt;/sequence>
 * &lt;/restriction>
 * &lt;/complexContent>
 * &lt;/complexType>
 * &lt;/element>
 * &lt;/sequence>
 * &lt;/restriction>
 * &lt;/complexContent>
 * &lt;/complexType>
</pre> *
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DT_GetProcList_Response", propOrder = arrayOf("header", "tenders"))
class DTGetProcListResponse {

    /**
     * Gets the value of the header property.
     *
     * @return
     * possible object is
     * [DTGetProcListResponse.Header]
     */
    /**
     * Sets the value of the header property.
     *
     * @param value
     * allowed object is
     * [DTGetProcListResponse.Header]
     */
    @XmlElement(name = "Header", required = true)
    var header: DTGetProcListResponse.Header = DTGetProcListResponse.Header()
    /**
     * Gets the value of the tenders property.
     *
     * @return
     * possible object is
     * [DTGetProcListResponse.Tenders]
     */
    /**
     * Sets the value of the tenders property.
     *
     * @param value
     * allowed object is
     * [DTGetProcListResponse.Tenders]
     */
    @XmlElement(name = "Tenders", required = true)
    var tenders: DTGetProcListResponse.Tenders = DTGetProcListResponse.Tenders()


    /**
     *
     * Java class for anonymous complex type.
     *
     *
     * The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     * &lt;complexContent>
     * &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     * &lt;sequence>
     * &lt;element name="Company" minOccurs="0">
     * &lt;complexType>
     * &lt;complexContent>
     * &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     * &lt;sequence>
     * &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     * &lt;element name="INN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     * &lt;element name="KPP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     * &lt;element name="Address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     * &lt;element name="Phone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     * &lt;element name="Email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     * &lt;/sequence>
     * &lt;/restriction>
     * &lt;/complexContent>
     * &lt;/complexType>
     * &lt;/element>
     * &lt;element name="Source" minOccurs="0">
     * &lt;complexType>
     * &lt;complexContent>
     * &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     * &lt;sequence>
     * &lt;element name="Version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     * &lt;/sequence>
     * &lt;/restriction>
     * &lt;/complexContent>
     * &lt;/complexType>
     * &lt;/element>
     * &lt;/sequence>
     * &lt;/restriction>
     * &lt;/complexContent>
     * &lt;/complexType>
    </pre> *
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = arrayOf("company", "source"))
    class Header {

        /**
         * Gets the value of the company property.
         *
         * @return
         * possible object is
         * [DTGetProcListResponse.Header.Company]
         */
        /**
         * Sets the value of the company property.
         *
         * @param value
         * allowed object is
         * [DTGetProcListResponse.Header.Company]
         */
        @XmlElement(name = "Company")
        var company: DTGetProcListResponse.Header.Company = DTGetProcListResponse.Header.Company()
        /**
         * Gets the value of the source property.
         *
         * @return
         * possible object is
         * [DTGetProcListResponse.Header.Source]
         */
        /**
         * Sets the value of the source property.
         *
         * @param value
         * allowed object is
         * [DTGetProcListResponse.Header.Source]
         */
        @XmlElement(name = "Source")
        var source: DTGetProcListResponse.Header.Source = DTGetProcListResponse.Header.Source()


        /**
         *
         * Java class for anonymous complex type.
         *
         *
         * The following schema fragment specifies the expected content contained within this class.
         *
         * <pre>
         * &lt;complexType>
         * &lt;complexContent>
         * &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         * &lt;sequence>
         * &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         * &lt;element name="INN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         * &lt;element name="KPP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         * &lt;element name="Address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         * &lt;element name="Phone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         * &lt;element name="Email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         * &lt;/sequence>
         * &lt;/restriction>
         * &lt;/complexContent>
         * &lt;/complexType>
        </pre> *
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = arrayOf("name", "inn", "kpp", "address", "phone", "email"))
        class Company {

            /**
             * Gets the value of the name property.
             *
             * @return
             * possible object is
             * [String]
             */
            /**
             * Sets the value of the name property.
             *
             * @param value
             * allowed object is
             * [String]
             */
            @XmlElement(name = "Name")
            var name: String = ""
            /**
             * Gets the value of the inn property.
             *
             * @return
             * possible object is
             * [String]
             */
            /**
             * Sets the value of the inn property.
             *
             * @param value
             * allowed object is
             * [String]
             */
            @XmlElement(name = "INN")
            var inn: String = ""
            /**
             * Gets the value of the kpp property.
             *
             * @return
             * possible object is
             * [String]
             */
            /**
             * Sets the value of the kpp property.
             *
             * @param value
             * allowed object is
             * [String]
             */
            @XmlElement(name = "KPP")
            var kpp: String = ""
            /**
             * Gets the value of the address property.
             *
             * @return
             * possible object is
             * [String]
             */
            /**
             * Sets the value of the address property.
             *
             * @param value
             * allowed object is
             * [String]
             */
            @XmlElement(name = "Address")
            var address: String = ""
            /**
             * Gets the value of the phone property.
             *
             * @return
             * possible object is
             * [String]
             */
            /**
             * Sets the value of the phone property.
             *
             * @param value
             * allowed object is
             * [String]
             */
            @XmlElement(name = "Phone")
            var phone: String = ""
            /**
             * Gets the value of the email property.
             *
             * @return
             * possible object is
             * [String]
             */
            /**
             * Sets the value of the email property.
             *
             * @param value
             * allowed object is
             * [String]
             */
            @XmlElement(name = "Email")
            var email: String = ""

        }


        /**
         *
         * Java class for anonymous complex type.
         *
         *
         * The following schema fragment specifies the expected content contained within this class.
         *
         * <pre>
         * &lt;complexType>
         * &lt;complexContent>
         * &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         * &lt;sequence>
         * &lt;element name="Version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         * &lt;/sequence>
         * &lt;/restriction>
         * &lt;/complexContent>
         * &lt;/complexType>
        </pre> *
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = arrayOf("version"))
        class Source {

            /**
             * Gets the value of the version property.
             *
             * @return
             * possible object is
             * [String]
             */
            /**
             * Sets the value of the version property.
             *
             * @param value
             * allowed object is
             * [String]
             */
            @XmlElement(name = "Version")
            var version: String = ""

        }

    }


    /**
     *
     * Java class for anonymous complex type.
     *
     *
     * The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     * &lt;complexContent>
     * &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     * &lt;sequence>
     * &lt;element name="Item" maxOccurs="unbounded" minOccurs="0">
     * &lt;complexType>
     * &lt;complexContent>
     * &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     * &lt;sequence>
     * &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}string"/>
     * &lt;element name="ProcessTypeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
     * &lt;element name="ProcessTypeText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     * &lt;element name="ChangeDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     * &lt;element name="PurchaseStartDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     * &lt;element name="PurchaseEndDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     * &lt;element name="ContractDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     * &lt;element name="Price" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     * &lt;element name="Comment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     * &lt;element name="Link" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     * &lt;element name="Text" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     * &lt;/sequence>
     * &lt;/restriction>
     * &lt;/complexContent>
     * &lt;/complexType>
     * &lt;/element>
     * &lt;/sequence>
     * &lt;/restriction>
     * &lt;/complexContent>
     * &lt;/complexType>
    </pre> *
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = arrayOf("item"))
    class Tenders {

        @XmlElement(name = "Item")
        var item: List<DTGetProcListResponse.Tenders.Item>? = null


        /**
         * Gets the value of the item property.
         *
         *
         *
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the item property.
         *
         *
         *
         * For example, to add a new item, do as follows:
         * <pre>
         * getItem().add(newItem);
        </pre> *
         *
         *
         *
         *
         * Objects of the following type(s) are allowed in the list
         * [DTGetProcListResponse.Tenders.Item]
         *
         *
         */
        /*fun getItem(): List<DTGetProcListResponse.Tenders.Item> {
            if (item == null) {
                item = ArrayList()
            }
            return this.item
        }*/


        /**
         *
         * Java class for anonymous complex type.
         *
         *
         * The following schema fragment specifies the expected content contained within this class.
         *
         * <pre>
         * &lt;complexType>
         * &lt;complexContent>
         * &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         * &lt;sequence>
         * &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}string"/>
         * &lt;element name="ProcessTypeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
         * &lt;element name="ProcessTypeText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         * &lt;element name="ChangeDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         * &lt;element name="PurchaseStartDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         * &lt;element name="PurchaseEndDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         * &lt;element name="ContractDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         * &lt;element name="Price" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         * &lt;element name="Comment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         * &lt;element name="Link" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         * &lt;element name="Text" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         * &lt;/sequence>
         * &lt;/restriction>
         * &lt;/complexContent>
         * &lt;/complexType>
        </pre> *
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(
            name = "",
            propOrder = arrayOf(
                "id",
                "processTypeCode",
                "processTypeText",
                "changeDate",
                "purchaseStartDate",
                "purchaseEndDate",
                "contractDate",
                "price",
                "comment",
                "link",
                "text"
            )
        )
        class Item {

            /**
             * Gets the value of the id property.
             *
             * @return
             * possible object is
             * [String]
             */
            /**
             * Sets the value of the id property.
             *
             * @param value
             * allowed object is
             * [String]
             */
            @XmlElement(name = "Id", required = true)
            var id: String = ""
            /**
             * Gets the value of the processTypeCode property.
             *
             * @return
             * possible object is
             * [String]
             */
            /**
             * Sets the value of the processTypeCode property.
             *
             * @param value
             * allowed object is
             * [String]
             */
            @XmlElement(name = "ProcessTypeCode", required = true)
            var processTypeCode: String = ""
            /**
             * Gets the value of the processTypeText property.
             *
             * @return
             * possible object is
             * [String]
             */
            /**
             * Sets the value of the processTypeText property.
             *
             * @param value
             * allowed object is
             * [String]
             */
            @XmlElement(name = "ProcessTypeText")
            var processTypeText: String = ""
            /**
             * Gets the value of the changeDate property.
             *
             * @return
             * possible object is
             * [String]
             */
            /**
             * Sets the value of the changeDate property.
             *
             * @param value
             * allowed object is
             * [String]
             */
            @XmlElement(name = "ChangeDate")
            var changeDate: String = ""
            /**
             * Gets the value of the purchaseStartDate property.
             *
             * @return
             * possible object is
             * [String]
             */
            /**
             * Sets the value of the purchaseStartDate property.
             *
             * @param value
             * allowed object is
             * [String]
             */
            @XmlElement(name = "PurchaseStartDate")
            var purchaseStartDate: String = ""
            /**
             * Gets the value of the purchaseEndDate property.
             *
             * @return
             * possible object is
             * [String]
             */
            /**
             * Sets the value of the purchaseEndDate property.
             *
             * @param value
             * allowed object is
             * [String]
             */
            @XmlElement(name = "PurchaseEndDate")
            var purchaseEndDate: String = ""
            /**
             * Gets the value of the contractDate property.
             *
             * @return
             * possible object is
             * [String]
             */
            /**
             * Sets the value of the contractDate property.
             *
             * @param value
             * allowed object is
             * [String]
             */
            @XmlElement(name = "ContractDate")
            var contractDate: String = ""
            /**
             * Gets the value of the price property.
             *
             * @return
             * possible object is
             * [String]
             */
            /**
             * Sets the value of the price property.
             *
             * @param value
             * allowed object is
             * [String]
             */
            @XmlElement(name = "Price")
            var price: String = ""
            /**
             * Gets the value of the comment property.
             *
             * @return
             * possible object is
             * [String]
             */
            /**
             * Sets the value of the comment property.
             *
             * @param value
             * allowed object is
             * [String]
             */
            @XmlElement(name = "Comment")
            var comment: String = ""
            /**
             * Gets the value of the link property.
             *
             * @return
             * possible object is
             * [String]
             */
            /**
             * Sets the value of the link property.
             *
             * @param value
             * allowed object is
             * [String]
             */
            @XmlElement(name = "Link")
            var link: String = ""
            /**
             * Gets the value of the text property.
             *
             * @return
             * possible object is
             * [String]
             */
            /**
             * Sets the value of the text property.
             *
             * @param value
             * allowed object is
             * [String]
             */
            @XmlElement(name = "Text")
            var text: String = ""

        }

    }

}
