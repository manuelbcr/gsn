package ch.epfl.gsn.wrappers.backlog.plugins.dpp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Serializable;
import java.nio.ByteBuffer;

import org.junit.Test;

import ch.epfl.gsn.beans.DataField;

public class HealthMinMsgTest {
    
    @Test
    public void testReceivePayload() throws Exception {
        HealthMinMsg healthMinMsg = new HealthMinMsg();

        ByteBuffer payloadBuffer = ByteBuffer.allocate(50);
        payloadBuffer.putShort((short) 720); 
        payloadBuffer.putShort((short) 1500); 
        payloadBuffer.putShort((short) 1000); 
        payloadBuffer.put((byte) 33); 
        payloadBuffer.put((byte) 80); 
        payloadBuffer.put((byte) 25); 
        payloadBuffer.put((byte) -5); 
        payloadBuffer.put((byte) 50); 
        payloadBuffer.put((byte) 75); 
        payloadBuffer.put((byte) -80); 
        payloadBuffer.put((byte) 12); 
        payloadBuffer.putShort((short) 1200); 
        payloadBuffer.putShort((short) 800); 
        payloadBuffer.putShort((short) 4096); 
        payloadBuffer.putShort((short) 1024); 
        payloadBuffer.flip();

        Serializable[] result = healthMinMsg.receivePayload(payloadBuffer);

        assertNotNull(result);
        assertEquals(15, result.length);
        assertEquals(720, result[0]);
        assertEquals(1500, result[1]);
        assertEquals(1000, result[2]);
        assertEquals((short)33, result[3]);
        assertEquals((short)80, result[4]);
        assertEquals((short)25, result[5]);
        assertEquals((short)-5, result[6]);
        assertEquals((short)50, result[7]);
        assertEquals((short)75, result[8]);
        assertEquals((short)-80, result[9]);
        assertEquals((short)12, result[10]);
        assertEquals(1200, result[11]);
        assertEquals(800, result[12]);
        assertEquals(4096, result[13]);
        assertEquals(1024, result[14]);
    }

    @Test
    public void testGetOutputFormat() {
        HealthMinMsg healthMinMsg = new HealthMinMsg();
        DataField[] outputFormat = healthMinMsg.getOutputFormat();

        assertNotNull(outputFormat);
        assertEquals(15, outputFormat.length);

        assertEquals("uptime", outputFormat[0].getName());
        assertEquals("INTEGER", outputFormat[0].getType());
        assertEquals("config", outputFormat[14].getName());
        assertEquals("INTEGER", outputFormat[14].getType());
    }

    @Test
    public void testGetType() {
        HealthMinMsg healthMinMsg = new HealthMinMsg();
        assertEquals(MessageTypes.DPP_MSG_TYPE_HEALTH_MIN, healthMinMsg.getType());
    }
}
