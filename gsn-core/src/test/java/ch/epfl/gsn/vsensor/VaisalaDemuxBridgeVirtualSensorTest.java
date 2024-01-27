package ch.epfl.gsn.vsensor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.Serializable;

import org.junit.Ignore;
import org.junit.Test;

import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;


public class VaisalaDemuxBridgeVirtualSensorTest {

    @Test
    public void testvaisala() {
        VaisalaDemuxBridgeVirtualSensor vs = new VaisalaDemuxBridgeVirtualSensor();
        
        String[] fieldNames = {"GENERATION_TIME", "DEVICE_ID", "wu", "tu", "ru", "su"};
        Byte[] fieldTypes = {DataTypes.BIGINT, DataTypes.INTEGER, DataTypes.VARCHAR, DataTypes.VARCHAR, DataTypes.VARCHAR, DataTypes.VARCHAR};
    
        String wu = "1R1R:timestamp=123,value1,value2,value3#"; 
        String tu = "1R2R:timestamp=456,value4,value5#";
        String ru = "1R3R:timestamp=789,value6,value7,value8#";
        String su = "1R5R:timestamp=987,value9,value10,VhX#";
        
        Serializable[] fieldValues = {12321412L, 1, wu, tu, ru, su};
        
        StreamElement input = new StreamElement(fieldNames, fieldTypes, fieldValues, System.currentTimeMillis() + 200);
        try{
            vs.dataAvailable("input", input);
            fail("Exception should be thrown");
        }catch(Exception e){

        }
        String wu1 = "1R2R:timestamp=123,value1,value2,value3#"; 
        String tu1 = "1R2R:timestamp=456,value4,value5#";
        String ru1 = "1R3R:timestamp=789,value6,value7,value8#";
        String su1 = "1R5R:timestamp=987,value9,value10,VhX#";
        Serializable[] fieldValues1 = {12321412L, 1, wu1, tu, ru, su};
        input = new StreamElement(fieldNames, fieldTypes, fieldValues1, System.currentTimeMillis() + 200);
        Serializable[] fieldValues2 = {12321412L, 1, wu, tu1, ru, su};
        StreamElement input1 = new StreamElement(fieldNames, fieldTypes, fieldValues2, System.currentTimeMillis() + 200);
        Serializable[] fieldValues3 = {12321412L, 1, wu, tu, ru1, su};
        StreamElement input2 = new StreamElement(fieldNames, fieldTypes, fieldValues3, System.currentTimeMillis() + 200);
        Serializable[] fieldValues4 = {12321412L, 1, wu, tu, ru, su1};
        StreamElement input3 = new StreamElement(fieldNames, fieldTypes, fieldValues4, System.currentTimeMillis() + 200);
        try{
            vs.dataAvailable("input", input);
            vs.dataAvailable("input", input1);
            vs.dataAvailable("input", input2);
            vs.dataAvailable("input", input3);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
