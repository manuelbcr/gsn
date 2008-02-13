
package gsn.msr.sensormap.sensorman;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAXWS SI.
 * JAX-WS RI 2.0_02-b08-fcs
 * Generated source version: 2.0
 * 
 */
@WebService(name = "ServiceSoap", targetNamespace = "http://tempuri.org/")
public interface ServiceSoap {


    /**
     * 
     * @return
     *     returns gsn.msr.sensormap.sensorman.ArrayOfString
     */
    @WebMethod(operationName = "DebugVectorSensor", action = "http://tempuri.org/DebugVectorSensor")
    @WebResult(name = "DebugVectorSensorResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "DebugVectorSensor", targetNamespace = "http://tempuri.org/", className = "gsn.msr.sensormap.sensorman.DebugVectorSensor")
    @ResponseWrapper(localName = "DebugVectorSensorResponse", targetNamespace = "http://tempuri.org/", className = "gsn.msr.sensormap.sensorman.DebugVectorSensorResponse")
    public ArrayOfString debugVectorSensor();

    /**
     * 
     * @return
     *     returns gsn.msr.sensormap.sensorman.ArrayOfString
     */
    @WebMethod(operationName = "DebugSensor", action = "http://tempuri.org/DebugSensor")
    @WebResult(name = "DebugSensorResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "DebugSensor", targetNamespace = "http://tempuri.org/", className = "gsn.msr.sensormap.sensorman.DebugSensor")
    @ResponseWrapper(localName = "DebugSensorResponse", targetNamespace = "http://tempuri.org/", className = "gsn.msr.sensormap.sensorman.DebugSensorResponse")
    public ArrayOfString debugSensor();

    /**
     * Dynamically Creates a Vector Sensor Type
     * 
     * @param publisherName
     * @param passCode
     * @param vectorType
     * @param componentTypes
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "CreateVectorSesnsorType", action = "http://tempuri.org/CreateVectorSesnsorType")
    @WebResult(name = "CreateVectorSesnsorTypeResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "CreateVectorSesnsorType", targetNamespace = "http://tempuri.org/", className = "gsn.msr.sensormap.sensorman.CreateVectorSesnsorType")
    @ResponseWrapper(localName = "CreateVectorSesnsorTypeResponse", targetNamespace = "http://tempuri.org/", className = "gsn.msr.sensormap.sensorman.CreateVectorSesnsorTypeResponse")
    public String createVectorSesnsorType(
        @WebParam(name = "publisherName", targetNamespace = "http://tempuri.org/")
        String publisherName,
        @WebParam(name = "passCode", targetNamespace = "http://tempuri.org/")
        String passCode,
        @WebParam(name = "vectorType", targetNamespace = "http://tempuri.org/")
        String vectorType,
        @WebParam(name = "componentTypes", targetNamespace = "http://tempuri.org/")
        ArrayOfString componentTypes);

    /**
     * Dynamically Creates a Sensor Type
     * 
     * @param publisherName
     * @param sensorType
     * @param passCode
     * @param uri
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "CreateSesnsorType", action = "http://tempuri.org/CreateSesnsorType")
    @WebResult(name = "CreateSesnsorTypeResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "CreateSesnsorType", targetNamespace = "http://tempuri.org/", className = "gsn.msr.sensormap.sensorman.CreateSesnsorType")
    @ResponseWrapper(localName = "CreateSesnsorTypeResponse", targetNamespace = "http://tempuri.org/", className = "gsn.msr.sensormap.sensorman.CreateSesnsorTypeResponse")
    public String createSesnsorType(
        @WebParam(name = "publisherName", targetNamespace = "http://tempuri.org/")
        String publisherName,
        @WebParam(name = "passCode", targetNamespace = "http://tempuri.org/")
        String passCode,
        @WebParam(name = "sensorType", targetNamespace = "http://tempuri.org/")
        String sensorType,
        @WebParam(name = "uri", targetNamespace = "http://tempuri.org/")
        String uri);

    /**
     * Registers a new sensor
     * 
     * @param sensor
     * @param publisherName
     * @param passCode
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "InsertSensor", action = "http://tempuri.org/InsertSensor")
    @WebResult(name = "InsertSensorResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "InsertSensor", targetNamespace = "http://tempuri.org/", className = "gsn.msr.sensormap.sensorman.InsertSensor")
    @ResponseWrapper(localName = "InsertSensorResponse", targetNamespace = "http://tempuri.org/", className = "gsn.msr.sensormap.sensorman.InsertSensorResponse")
    public String insertSensor(
        @WebParam(name = "publisherName", targetNamespace = "http://tempuri.org/")
        String publisherName,
        @WebParam(name = "passCode", targetNamespace = "http://tempuri.org/")
        String passCode,
        @WebParam(name = "Sensor", targetNamespace = "http://tempuri.org/")
        SensorInfo sensor);

    /**
     * Registers a new sensor
     * 
     * @param sensor
     * @param publisherName
     * @param passCode
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "InsertVectorSensor", action = "http://tempuri.org/InsertVectorSensor")
    @WebResult(name = "InsertVectorSensorResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "InsertVectorSensor", targetNamespace = "http://tempuri.org/", className = "gsn.msr.sensormap.sensorman.InsertVectorSensor")
    @ResponseWrapper(localName = "InsertVectorSensorResponse", targetNamespace = "http://tempuri.org/", className = "gsn.msr.sensormap.sensorman.InsertVectorSensorResponse")
    public String insertVectorSensor(
        @WebParam(name = "publisherName", targetNamespace = "http://tempuri.org/")
        String publisherName,
        @WebParam(name = "passCode", targetNamespace = "http://tempuri.org/")
        String passCode,
        @WebParam(name = "Sensor", targetNamespace = "http://tempuri.org/")
        SensorInfo sensor);

    /**
     * Deletes an existing sensor
     * 
     * @param publisherName
     * @param passCode
     * @param sensorName
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "DeleteSensor", action = "http://tempuri.org/DeleteSensor")
    @WebResult(name = "DeleteSensorResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "DeleteSensor", targetNamespace = "http://tempuri.org/", className = "gsn.msr.sensormap.sensorman.DeleteSensor")
    @ResponseWrapper(localName = "DeleteSensorResponse", targetNamespace = "http://tempuri.org/", className = "gsn.msr.sensormap.sensorman.DeleteSensorResponse")
    public String deleteSensor(
        @WebParam(name = "publisherName", targetNamespace = "http://tempuri.org/")
        String publisherName,
        @WebParam(name = "passCode", targetNamespace = "http://tempuri.org/")
        String passCode,
        @WebParam(name = "sensorName", targetNamespace = "http://tempuri.org/")
        String sensorName);

    /**
     * Deletes an existing sensor
     * 
     * @param publisherName
     * @param sensorType
     * @param passCode
     * @param sensorName
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "DeleteVectorSensor", action = "http://tempuri.org/DeleteVectorSensor")
    @WebResult(name = "DeleteVectorSensorResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "DeleteVectorSensor", targetNamespace = "http://tempuri.org/", className = "gsn.msr.sensormap.sensorman.DeleteVectorSensor")
    @ResponseWrapper(localName = "DeleteVectorSensorResponse", targetNamespace = "http://tempuri.org/", className = "gsn.msr.sensormap.sensorman.DeleteVectorSensorResponse")
    public String deleteVectorSensor(
        @WebParam(name = "publisherName", targetNamespace = "http://tempuri.org/")
        String publisherName,
        @WebParam(name = "passCode", targetNamespace = "http://tempuri.org/")
        String passCode,
        @WebParam(name = "sensorName", targetNamespace = "http://tempuri.org/")
        String sensorName,
        @WebParam(name = "sensorType", targetNamespace = "http://tempuri.org/")
        String sensorType);

    /**
     * Retreives metadata of an existing sensor
     * 
     * @param publisherName
     * @param passCode
     * @param sensorName
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "GetSensorDescriptionByName", action = "http://tempuri.org/GetSensorDescriptionByName")
    @WebResult(name = "GetSensorDescriptionByNameResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetSensorDescriptionByName", targetNamespace = "http://tempuri.org/", className = "gsn.msr.sensormap.sensorman.GetSensorDescriptionByName")
    @ResponseWrapper(localName = "GetSensorDescriptionByNameResponse", targetNamespace = "http://tempuri.org/", className = "gsn.msr.sensormap.sensorman.GetSensorDescriptionByNameResponse")
    public String getSensorDescriptionByName(
        @WebParam(name = "publisherName", targetNamespace = "http://tempuri.org/")
        String publisherName,
        @WebParam(name = "passCode", targetNamespace = "http://tempuri.org/")
        String passCode,
        @WebParam(name = "sensorName", targetNamespace = "http://tempuri.org/")
        String sensorName);

    /**
     * Returns metadata of all the sensors published by a given publisher
     * 
     * @param publisherName
     * @param passCode
     * @return
     *     returns gsn.msr.sensormap.sensorman.ArrayOfSensorInfo
     */
    @WebMethod(operationName = "GetSensorsByPublisher", action = "http://tempuri.org/GetSensorsByPublisher")
    @WebResult(name = "GetSensorsByPublisherResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetSensorsByPublisher", targetNamespace = "http://tempuri.org/", className = "gsn.msr.sensormap.sensorman.GetSensorsByPublisher")
    @ResponseWrapper(localName = "GetSensorsByPublisherResponse", targetNamespace = "http://tempuri.org/", className = "gsn.msr.sensormap.sensorman.GetSensorsByPublisherResponse")
    public ArrayOfSensorInfo getSensorsByPublisher(
        @WebParam(name = "publisherName", targetNamespace = "http://tempuri.org/")
        String publisherName,
        @WebParam(name = "passCode", targetNamespace = "http://tempuri.org/")
        String passCode);

    /**
     * Modifies location of a sensor
     * 
     * @param publisherName
     * @param altitude
     * @param passCode
     * @param longitude
     * @param latitude
     * @param sensorName
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "UpdateSensorLocation", action = "http://tempuri.org/UpdateSensorLocation")
    @WebResult(name = "UpdateSensorLocationResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "UpdateSensorLocation", targetNamespace = "http://tempuri.org/", className = "gsn.msr.sensormap.sensorman.UpdateSensorLocation")
    @ResponseWrapper(localName = "UpdateSensorLocationResponse", targetNamespace = "http://tempuri.org/", className = "gsn.msr.sensormap.sensorman.UpdateSensorLocationResponse")
    public String updateSensorLocation(
        @WebParam(name = "publisherName", targetNamespace = "http://tempuri.org/")
        String publisherName,
        @WebParam(name = "passCode", targetNamespace = "http://tempuri.org/")
        String passCode,
        @WebParam(name = "sensorName", targetNamespace = "http://tempuri.org/")
        String sensorName,
        @WebParam(name = "latitude", targetNamespace = "http://tempuri.org/")
        double latitude,
        @WebParam(name = "longitude", targetNamespace = "http://tempuri.org/")
        double longitude,
        @WebParam(name = "altitude", targetNamespace = "http://tempuri.org/")
        double altitude);

    /**
     * Returns all the sensors within a polygon. Use the null to ignore some parameter.
     * 
     * @param polygon
     * @param viewport
     * @param sensorTypes
     * @param searchStr
     * @return
     *     returns gsn.msr.sensormap.sensorman.ArrayOfSensorInfo
     */
    @WebMethod(operationName = "SensorsInsidePolygon", action = "http://tempuri.org/SensorsInsidePolygon")
    @WebResult(name = "SensorsInsidePolygonResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "SensorsInsidePolygon", targetNamespace = "http://tempuri.org/", className = "gsn.msr.sensormap.sensorman.SensorsInsidePolygon")
    @ResponseWrapper(localName = "SensorsInsidePolygonResponse", targetNamespace = "http://tempuri.org/", className = "gsn.msr.sensormap.sensorman.SensorsInsidePolygonResponse")
    public ArrayOfSensorInfo sensorsInsidePolygon(
        @WebParam(name = "polygon", targetNamespace = "http://tempuri.org/")
        ArrayOfPointF polygon,
        @WebParam(name = "viewport", targetNamespace = "http://tempuri.org/")
        ArrayOfPointF viewport,
        @WebParam(name = "searchStr", targetNamespace = "http://tempuri.org/")
        String searchStr,
        @WebParam(name = "sensorTypes", targetNamespace = "http://tempuri.org/")
        ArrayOfSensorTypeEnum sensorTypes);

}
