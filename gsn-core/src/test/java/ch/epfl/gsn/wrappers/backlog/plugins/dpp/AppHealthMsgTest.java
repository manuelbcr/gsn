package ch.epfl.gsn.wrappers.backlog.plugins.dpp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Serializable;
import java.nio.ByteBuffer;

import org.junit.Test;

import ch.epfl.gsn.beans.DataField;

public class AppHealthMsgTest {
    
    @Test
    public void testReceivePayload() throws Exception {
        AppHealthMsg appHealthMsg = new AppHealthMsg();

        ByteBuffer payloadBuffer = ByteBuffer.allocate(24);
        payloadBuffer.putInt(1000); 
        payloadBuffer.putShort((short) 5); 
        payloadBuffer.putShort((short) 12000); 
        payloadBuffer.putShort((short) 2500); 
        payloadBuffer.putShort((short) 80);
        payloadBuffer.put((byte) 50); 
        payloadBuffer.put((byte) 30); 
        payloadBuffer.putShort((short) 3300); 
        payloadBuffer.putShort((short) 150); 
        payloadBuffer.putShort((short) 2800); 
        payloadBuffer.putShort((short) 5000);
        payloadBuffer.flip();

        Serializable[] result = appHealthMsg.receivePayload(payloadBuffer);

        assertNotNull(result);
        assertEquals(11, result.length);
        assertEquals(1000L, result[0]);
        assertEquals(5, result[1]);
        assertEquals(12000, result[2]);
        assertEquals(2500, result[3]);
        assertEquals(80, result[4]);
        assertEquals((short) 50, result[5]);
        assertEquals((short) 30, result[6]);
        assertEquals(3300, result[7]);
        assertEquals(150, result[8]);
        assertEquals(2800, result[9]);
        assertEquals(5000, result[10]);
    }

    @Test
    public void testGetOutputFormat() {
        AppHealthMsg appHealthMsg = new AppHealthMsg();
        DataField[] outputFormat = appHealthMsg.getOutputFormat();

        assertNotNull(outputFormat);
        assertEquals(11, outputFormat.length);

        assertEquals("uptime", outputFormat[0].getName());
        assertEquals("BIGINT", outputFormat[0].getType());

        assertEquals("humidity", outputFormat[10].getName());
        assertEquals("INTEGER", outputFormat[10].getType());
    }

    @Test
    public void testGetType() {
        AppHealthMsg appHealthMsg = new AppHealthMsg();
        assertEquals(MessageTypes.DPP_MSG_TYPE_APP_HEALTH, appHealthMsg.getType());
    }
}
