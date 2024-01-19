package ch.epfl.gsn.monitoring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;

public class AnomalyTest {

  @Test
  public void testConstructor() {
    DataField field =  new DataField("device_id", DataTypes.INTEGER);
    String timeStr = "1m";
    DataField groupByField =  new DataField("device_id", DataTypes.INTEGER);
    Anomaly anomaly = new Anomaly("func", field, timeStr, groupByField);
    
    assertEquals("func", anomaly.getFunction());
    assertEquals(field, anomaly.getField());
    assertEquals(60000, anomaly.getTime()); 
    assertTrue(anomaly.isGroupBy());
    assertEquals(groupByField, anomaly.getGroupByField());

    Anomaly anomaly1 = new Anomaly("func", field, "1h", groupByField);
    assertEquals(3600000, anomaly1.getTime());
    Anomaly anomaly2 = new Anomaly("func", field, "1d", groupByField);
    assertEquals(86400000, anomaly2.getTime());
    Anomaly anomaly3 = new Anomaly("func", field, "1w", groupByField);
    assertEquals(-1, anomaly3.getTime());
  }


  @Test
  public void testConstructor2() {
    DataField field =  new DataField("device_id", DataTypes.INTEGER);
    String timeStr = "1m";
    DataField groupByField =  new DataField("device_id", DataTypes.INTEGER);
    Anomaly anomaly = new Anomaly("func", field, timeStr, groupByField,"value");
    
    assertEquals("func", anomaly.getFunction());
    assertEquals(field, anomaly.getField());
    assertEquals(60000, anomaly.getTime()); 
    assertTrue(anomaly.isGroupBy());
    assertEquals(groupByField, anomaly.getGroupByField());
    assertEquals("value", anomaly.getValue());
    assertEquals("1m", anomaly.getTimeStr());

    assertEquals("anomaly.func.device_id=1m,value,[device_id]", anomaly.toString());
  }

}
