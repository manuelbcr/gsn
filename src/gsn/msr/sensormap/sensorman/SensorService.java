
package gsn.msr.sensormap.sensorman;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;


/**
 * This class was generated by the JAXWS SI.
 * JAX-WS RI 2.0_02-b08-fcs
 * Generated source version: 2.0
 * 
 */
@WebServiceClient(name = "Service", targetNamespace = "http://tempuri.org/", wsdlLocation = "http://atom.research.microsoft.com/SenseWebV3/SensorManager/Service.asmx?WSDL")
public class SensorService
    extends javax.xml.ws.Service
{

    private final static URL SERVICE_WSDL_LOCATION;

    static {
        URL url = null;
        try {
            url = new URL("http://atom.research.microsoft.com/SenseWebV3/SensorManager/Service.asmx?WSDL");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        SERVICE_WSDL_LOCATION = url;
    }

    public SensorService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public SensorService() {
        super(SERVICE_WSDL_LOCATION, new QName("http://tempuri.org/", "Service"));
    }

    /**
     * 
     * @return
     *     returns ServiceSoap
     */
    @WebEndpoint(name = "ServiceSoap")
    public ServiceSoap getServiceSoap() {
        return (ServiceSoap)super.getPort(new QName("http://tempuri.org/", "ServiceSoap"), ServiceSoap.class);
    }

    /**
     * 
     * @return
     *     returns ServiceSoap
     */
    @WebEndpoint(name = "ServiceSoap12")
    public ServiceSoap getServiceSoap12() {
        return (ServiceSoap)super.getPort(new QName("http://tempuri.org/", "ServiceSoap12"), ServiceSoap.class);
    }

}
