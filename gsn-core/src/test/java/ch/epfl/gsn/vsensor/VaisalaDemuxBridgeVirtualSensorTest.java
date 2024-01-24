package ch.epfl.gsn.vsensor;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;

import org.junit.Ignore;
import org.junit.Test;

import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;

@Ignore
public class VaisalaDemuxBridgeVirtualSensorTest {

    @Test
    public void testvaisala() {
        VaisalaDemuxBridgeVirtualSensor vs = new VaisalaDemuxBridgeVirtualSensor();
        
        String[] fieldNames = {"GENERATION_TIME", "DEVICE_ID", "wu", "tu", "ru", "su"};
        Byte[] fieldTypes = {DataTypes.BIGINT, DataTypes.INTEGER, DataTypes.VARCHAR, DataTypes.VARCHAR, DataTypes.VARCHAR, DataTypes.VARCHAR};
    
        String wu = "1,R1:123,12.3,15.4,Wdm#"; 
        String tu = "2,R2:456,22.1,21.4,Ti#";
        String ru = "3,R3:789,81.2,Ux#";
        String su = "5,R5:101112,1031.2,1032.1,1#";
        
        Serializable[] fieldValues = {12321412L, 1, wu, tu, ru, su};
        
        StreamElement input = new StreamElement(fieldNames, fieldTypes, fieldValues, System.currentTimeMillis() + 200);
        
        vs.dataAvailable("input", input);
    }
}
