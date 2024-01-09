package ch.epfl.gsn.vsensor;

import static org.junit.Assert.assertTrue;

import java.io.Serializable;

import org.junit.Test;

import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;

public class WiseNodeVirtualSensorTest {

    @Test
    public void testInitialize(){
        WiseNodeVirtualSensor vs = new WiseNodeVirtualSensor();
        assertTrue(vs.initialize());
        byte[] buffer = new byte[20]; 
        buffer[0] = 'e';
        buffer[1] = 'd';
        buffer[3] = 's';
        buffer[4] = 18;
        buffer[5] = 1;
        buffer[6] = 1;
        buffer[7] = 1;
        buffer[8] = 1;
        buffer[9] = 1;
        buffer[10] = 1;
        buffer[11] = 1;
        buffer[12] = 1;
        buffer[13] = 1;
        buffer[14] = 1;
        buffer[15] = 1;
        buffer[16] = 1;
        buffer[17] = 1;
        buffer[18] = 1;
        buffer[19] = 25;  
        buffer[5] = 3;   

        String[] fieldnames = {"RAW_PACKET","field2","field3","field4"};
        StreamElement streamElement1 = new StreamElement(
            fieldnames,
            new Byte[]{DataTypes.BINARY,DataTypes.INTEGER,DataTypes.INTEGER,DataTypes.BIGINT},
            new Serializable[]{buffer,2,3,1657752584L},
            System.currentTimeMillis()+10
        );
        vs.dataAvailable("input", streamElement1);
        vs.dispose();
    }
}
