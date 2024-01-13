package ch.epfl.gsn.networking.mqtt;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Serializable;

import org.junit.Ignore;
import org.junit.Test;

import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;

public class MQTTDeliveryTest {

    @Test
    public void testWriteStreamElement(){
        MQTTDelivery mqttDelivery = new MQTTDelivery("tcp://broker.mqttdashboard.com:1883", "", "topic", "vsname");
        StreamElement streamElement1 = new StreamElement(
            new String[]{"raw_packet"},
            new Byte[]{DataTypes.BINARY},
            new Serializable[]{new byte[] {1, 2, 3}},
            System.currentTimeMillis()+200);
        assertTrue(mqttDelivery.writeStreamElement(streamElement1));
        assertTrue(mqttDelivery.writeKeepAliveStreamElement());
        assertFalse(mqttDelivery.isClosed());
        mqttDelivery.close();
        assertTrue(mqttDelivery.isClosed());
    }

    @Test
    public void testWriteStreamElementMqttException() {
        MQTTDelivery mqttDelivery = new MQTTDelivery("url", "id", "topic", "name");
        StreamElement se = new StreamElement();
        try{
            mqttDelivery.writeStreamElement(se);
            fail("exception not expected");
        } catch(Exception e){
            
        }
        
    }


    @Test
    public void testWriteStructure() throws IOException {
        MQTTDelivery mqttDelivery = new MQTTDelivery("tcp://broker.mqttdashboard.com:1883", "", "topic", "name");
        DataField[] fields = {new DataField("field1", "STRING", "vs")};
        mqttDelivery.writeStructure(fields);
        
    }

}
