/**
 * http://atom.research.microsoft.com/sensewebv3/sensormanager/service.asmx
 * http://localhost:22001/services/Service?wsdl
 * Generated from : http://atom.research.microsoft.com/SenseWebV3/DataHub/Service.asmx
 * This file was auto-generated from WSDL2java (axis2) and Modified by GSN TEAM.
 */

package gsn.msr.sensormap.datahub;

import gsn.Mappings;
import gsn.beans.StreamElement;
import gsn.beans.VSensorConfig;
import gsn.storage.DataEnumerator;
import gsn.storage.StorageManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.tempuri.ArrayOfArrayOfSensorData;
import org.tempuri.ArrayOfDateTime;
import org.tempuri.ArrayOfDouble;
import org.tempuri.ArrayOfSensorData;
import org.tempuri.GetAggregateScalarDataSeriesInBatchResponse;
import org.tempuri.SensorData;

public class ServiceSkeleton {
  private static final transient Logger         logger          = Logger.getLogger( ServiceSkeleton.class );
  /**
   * SensorTypes : 
   * public  int Unknown = 0;
   * public  int Generic = 1;
   * public  int Temperature = 2;
   * public  int Video = 3;
   * public  int Traffic = 4;
   * public  int Parking = 5;
   * public  int Pressure = 6;
   * public  int Humidity = 7;
   * **************************
   * DataTypes :
   * public  int Unknown = 0;
   * public  int Scalar = 1;
   * public  int BMP = 2;
   * public  int JPG = 3;
   * public  int GIF = 4;
   * public  int Vector = 5;
   * public  int HTML = 6;
   */
  
  /**
   * Auto generated method signature
   * @param getAggregateScalarDataSeriesInBatch
   */                  
  public org.tempuri.GetAggregateScalarDataSeriesInBatchResponse GetAggregateScalarDataSeriesInBatch(org.tempuri.GetAggregateScalarDataSeriesInBatch input) {
    GetAggregateScalarDataSeriesInBatchResponse toReturn = new GetAggregateScalarDataSeriesInBatchResponse();
    String[] sensorNames = input.getSensorNames().getString();
    long aggInMSec = input.getAggregateIntervalInSeconds()*1000;
    ArrayOfArrayOfSensorData items = new ArrayOfArrayOfSensorData();
    for (String sensorName: sensorNames) {
      ArrayOfSensorData sensorData = new ArrayOfSensorData();
      StringTokenizer st= new StringTokenizer(sensorName,"@");
      String vsName = st.nextToken();
      logger.fatal("VSNAME : "+vsName);
      int vsFieldIndex = Integer.parseInt(st.nextToken());
      logger.fatal("VSFIELD INDEX : "+vsFieldIndex);
      VSensorConfig conf =Mappings.getVSensorConfig(vsName);
      if (vsFieldIndex>=conf.getOutputStructure().length)
        continue;//reject.
      StringBuilder query = new StringBuilder("select AVG(TIMED) as TIMED,PK, AVG(").append(conf.getOutputStructure()[vsFieldIndex].getName()).append(") as data from ").append(vsName).append(" where TIMED >= ").append(input.getStartTime().getTimeInMillis()).append(" AND TIMED <= ").append(input.getEndTime().getTimeInMillis()).append(" group by TIMED/").append(aggInMSec).append(" order by TIMED");
      System.out.println("QUERY : "+query);
      DataEnumerator output = null;
      try {
        output = StorageManager.getInstance().executeQuery(query, true);
        SensorData data = new SensorData(); 
        ArrayOfDateTime arrayOfDateTime = new ArrayOfDateTime();
        ArrayList<Double> sensor_readings = new ArrayList();
        while(output.hasMoreElements()) {
          StreamElement se = output.nextElement();
          Calendar timestamp = Calendar.getInstance();
          timestamp.setTimeInMillis(se.getTimeStamp());
          arrayOfDateTime.addDateTime(timestamp);
          sensor_readings.add(Double.parseDouble(se.getData()[0].toString()));
        }
        data.setSensorType(5);//Vector
        data.setDataType(1);// Generic
        data.setTimestamps(arrayOfDateTime);
        ArrayOfDouble arrayOfDouble = new ArrayOfDouble();
        arrayOfDouble.set_double(ArrayUtils.toPrimitive(sensor_readings.toArray(new Double[] {})));
        data.setData(arrayOfDouble);
        sensorData.addSensorData(data);
      }catch (SQLException e) {
        logger.error(e.getMessage(),e);
      }
      items.addArrayOfSensorData(sensorData);
    }
    toReturn.setGetAggregateScalarDataSeriesInBatchResult(items);
    return toReturn;
  } 
  
  public org.tempuri.GetLatestScalarDataInBatchResponse GetLatestScalarDataInBatch(org.tempuri.GetLatestScalarDataInBatch input) {
  //TODO : fill this with the necessary business logic
    throw new java.lang.UnsupportedOperationException("Please implement " +
        this.getClass().getName() + "#GetAggregateScalarDataSeriesInBatch");
    
  }
  
  /**
   * Auto generated method signature
   * @param registerSensor
   */
  public org.tempuri.RegisterSensorResponse RegisterSensor(
      org.tempuri.RegisterSensor registerSensor) {
    //TODO : fill this with the necessary business logic
    throw new java.lang.UnsupportedOperationException("Please implement " +
        this.getClass().getName() + "#RegisterSensor");
  }
  
  /**
   * Auto generated method signature
   * @param registerVectorSensor
   */
  public org.tempuri.RegisterVectorSensorResponse RegisterVectorSensor(
      org.tempuri.RegisterVectorSensor registerVectorSensor) {
    //TODO : fill this with the necessary business logic
    throw new java.lang.UnsupportedOperationException("Please implement " +
        this.getClass().getName() + "#RegisterVectorSensor");
  }
  
  /**
   * Auto generated method signature
   * @param deleteSensor
   */
  public org.tempuri.DeleteSensorResponse DeleteSensor(
      org.tempuri.DeleteSensor deleteSensor) {
    //TODO : fill this with the necessary business logic
    throw new java.lang.UnsupportedOperationException("Please implement " +
        this.getClass().getName() + "#DeleteSensor");
  }
  
  /**
   * Auto generated method signature
   * @param deleteVectorSensor
   */
  public org.tempuri.DeleteVectorSensorResponse DeleteVectorSensor(
      org.tempuri.DeleteVectorSensor deleteVectorSensor) {
    //TODO : fill this with the necessary business logic
    throw new java.lang.UnsupportedOperationException("Please implement " +
        this.getClass().getName() + "#DeleteVectorSensor");
  }
  
  /**
   * Auto generated method signature
   * @param updateSensorLocation
   */
  public org.tempuri.UpdateSensorLocationResponse UpdateSensorLocation(
      org.tempuri.UpdateSensorLocation updateSensorLocation) {
    //TODO : fill this with the necessary business logic
    throw new java.lang.UnsupportedOperationException("Please implement " +
        this.getClass().getName() + "#UpdateSensorLocation");
  }
  
  /**
   * Auto generated method signature
   * @param getSensorByPublisherAndName
   */
  public org.tempuri.GetSensorByPublisherAndNameResponse GetSensorByPublisherAndName(
      org.tempuri.GetSensorByPublisherAndName getSensorByPublisherAndName) {
    //TODO : fill this with the necessary business logic
    throw new java.lang.UnsupportedOperationException("Please implement " +
        this.getClass().getName() + "#GetSensorByPublisherAndName");
  }
  
  /**
   * Auto generated method signature
   * @param getSensorsByPublisher
   */
  public org.tempuri.GetSensorsByPublisherResponse GetSensorsByPublisher(
      org.tempuri.GetSensorsByPublisher getSensorsByPublisher) {
    //TODO : fill this with the necessary business logic
    throw new java.lang.UnsupportedOperationException("Please implement " +
        this.getClass().getName() + "#GetSensorsByPublisher");
  }
  
  /**
   * Auto generated method signature
   * @param debugVectorSensorManager
   */
  public org.tempuri.DebugVectorSensorManagerResponse DebugVectorSensorManager(
      org.tempuri.DebugVectorSensorManager debugVectorSensorManager) {
    //TODO : fill this with the necessary business logic
    throw new java.lang.UnsupportedOperationException("Please implement " +
        this.getClass().getName() + "#DebugVectorSensorManager");
  }
  
  /**
   * Auto generated method signature
   * @param debugSensorManager
   */
  public org.tempuri.DebugSensorManagerResponse DebugSensorManager(
      org.tempuri.DebugSensorManager debugSensorManager) {
    //TODO : fill this with the necessary business logic
    throw new java.lang.UnsupportedOperationException("Please implement " +
        this.getClass().getName() + "#DebugSensorManager");
  }
  
  /**
   * Auto generated method signature
   * @param storeScalarData
   */
  public org.tempuri.StoreScalarDataResponse StoreScalarData(
      org.tempuri.StoreScalarData storeScalarData) {
    //TODO : fill this with the necessary business logic
    throw new java.lang.UnsupportedOperationException("Please implement " +
        this.getClass().getName() + "#StoreScalarData");
  }
  
  /**
   * Auto generated method signature
   * @param storeScalarDataBatch
   */
  public org.tempuri.StoreScalarDataBatchResponse StoreScalarDataBatch(
      org.tempuri.StoreScalarDataBatch storeScalarDataBatch) {
    //TODO : fill this with the necessary business logic
    throw new java.lang.UnsupportedOperationException("Please implement " +
        this.getClass().getName() + "#StoreScalarDataBatch");
  }
  
  /**
   * Auto generated method signature
   * @param getLatestScalarData
   */
  public org.tempuri.GetLatestScalarDataResponse GetLatestScalarData(
      org.tempuri.GetLatestScalarData getLatestScalarData) {
    //TODO : fill this with the necessary business logic
    throw new java.lang.UnsupportedOperationException("Please implement " +
        this.getClass().getName() + "#GetLatestScalarData");
  }
  
  
  /**
   * Auto generated method signature
   * @param getScalarDataSeries
   */
  public org.tempuri.GetScalarDataSeriesResponse GetScalarDataSeries(
      org.tempuri.GetScalarDataSeries getScalarDataSeries) {
    //TODO : fill this with the necessary business logic
    throw new java.lang.UnsupportedOperationException("Please implement " +
        this.getClass().getName() + "#GetScalarDataSeries");
  }
  
  /**
   * Auto generated method signature
   * @param getAggregateScalarData
   */
  public org.tempuri.GetAggregateScalarDataResponse GetAggregateScalarData(
      org.tempuri.GetAggregateScalarData getAggregateScalarData) {
    //TODO : fill this with the necessary business logic
    throw new java.lang.UnsupportedOperationException("Please implement " +
        this.getClass().getName() + "#GetAggregateScalarData");
  }
  
  /**
   * Auto generated method signature
   * @param getAggregateScalarDataInBatch
   */
  public org.tempuri.GetAggregateScalarDataInBatchResponse GetAggregateScalarDataInBatch(
      org.tempuri.GetAggregateScalarDataInBatch getAggregateScalarDataInBatch) {
    //TODO : fill this with the necessary business logic
    throw new java.lang.UnsupportedOperationException("Please implement " +
        this.getClass().getName() + "#GetAggregateScalarDataInBatch");
  }
  
  /**
   * Auto generated method signature
   * @param getAggregateScalarDataSeries
   */
  public org.tempuri.GetAggregateScalarDataSeriesResponse GetAggregateScalarDataSeries(
      org.tempuri.GetAggregateScalarDataSeries getAggregateScalarDataSeries) {
    //TODO : fill this with the necessary business logic
    throw new java.lang.UnsupportedOperationException("Please implement " +
        this.getClass().getName() + "#GetAggregateScalarDataSeries");
  }

  
  /**
   * Auto generated method signature
   * @param storeVectorData
   */
  public org.tempuri.StoreVectorDataResponse StoreVectorData(
      org.tempuri.StoreVectorData storeVectorData) {
    //TODO : fill this with the necessary business logic
    throw new java.lang.UnsupportedOperationException("Please implement " +
        this.getClass().getName() + "#StoreVectorData");
  }
  
  /**
   * Auto generated method signature
   * @param storeVectorDataByComponentIndex
   */
  public org.tempuri.StoreVectorDataByComponentIndexResponse StoreVectorDataByComponentIndex(
      org.tempuri.StoreVectorDataByComponentIndex storeVectorDataByComponentIndex) {
    //TODO : fill this with the necessary business logic
    throw new java.lang.UnsupportedOperationException("Please implement " +
        this.getClass().getName() + "#StoreVectorDataByComponentIndex");
  }
  
  /**
   * Auto generated method signature
   * @param getLatestVectorData
   */
  public org.tempuri.GetLatestVectorDataResponse GetLatestVectorData(
      org.tempuri.GetLatestVectorData getLatestVectorData) {
    //TODO : fill this with the necessary business logic
    throw new java.lang.UnsupportedOperationException("Please implement " +
        this.getClass().getName() + "#GetLatestVectorData");
  }
  
  /**
   * Auto generated method signature
   * @param getLatestVectorDataByComponentIndex
   */
  public org.tempuri.GetLatestVectorDataByComponentIndexResponse GetLatestVectorDataByComponentIndex(
      org.tempuri.GetLatestVectorDataByComponentIndex getLatestVectorDataByComponentIndex) {
    //TODO : fill this with the necessary business logic
    throw new java.lang.UnsupportedOperationException("Please implement " +
        this.getClass().getName() + "#GetLatestVectorDataByComponentIndex");
  }
  
  /**
   * Auto generated method signature
   * @param storeBinaryData
   */
  public org.tempuri.StoreBinaryDataResponse StoreBinaryData(
      org.tempuri.StoreBinaryData storeBinaryData) {
    //TODO : fill this with the necessary business logic
    throw new java.lang.UnsupportedOperationException("Please implement " +
        this.getClass().getName() + "#StoreBinaryData");
  }
  
  /**
   * Auto generated method signature
   * @param getLatestBinarySensorData
   */
  public org.tempuri.GetLatestBinarySensorDataResponse GetLatestBinarySensorData(
      org.tempuri.GetLatestBinarySensorData getLatestBinarySensorData) {
    //TODO : fill this with the necessary business logic
    throw new java.lang.UnsupportedOperationException("Please implement " +
        this.getClass().getName() + "#GetLatestBinarySensorData");
  }
}
