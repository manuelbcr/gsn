
package gsn.msr.sensormap.appman;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TestAuthenticatedResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "testAuthenticatedResult"
})
@XmlRootElement(name = "TestAuthenticatedResponse")
public class TestAuthenticatedResponse {

    @XmlElement(name = "TestAuthenticatedResult")
    protected String testAuthenticatedResult;

    /**
     * Gets the value of the testAuthenticatedResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTestAuthenticatedResult() {
        return testAuthenticatedResult;
    }

    /**
     * Sets the value of the testAuthenticatedResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTestAuthenticatedResult(String value) {
        this.testAuthenticatedResult = value;
    }

}
