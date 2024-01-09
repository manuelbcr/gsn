package ch.epfl.gsn.wrappers.backlog.plugins.dpp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Serializable;
import java.nio.ByteBuffer;

import org.junit.Test;

import ch.epfl.gsn.beans.DataField;

public class ComHealthMsgTest {
    
    @Test
    public void testReceivePayload() throws Exception {
        ComHealthMsg comHealthMsg = new ComHealthMsg();

        ByteBuffer payloadBuffer = ByteBuffer.allocate(36);
        payloadBuffer.putInt(1000); 
        payloadBuffer.putShort((short) 5); 
        payloadBuffer.putShort((short) 12000); 
        payloadBuffer.putShort((short) 2500); 
        payloadBuffer.putShort((short) 80); 
        payloadBuffer.put((byte) 50); 
        
        payloadBuffer.put((byte) 25); 
        payloadBuffer.put((byte) 10); 
        payloadBuffer.put((byte) -5); 
        payloadBuffer.putShort((short) 70); 
        payloadBuffer.putShort((short) 60); 
        payloadBuffer.putShort((short) 120);
        
        payloadBuffer.putShort((short) 500);
        payloadBuffer.put((byte) 15); 
        payloadBuffer.put((byte) 20); 
        payloadBuffer.put((byte) 2); 
        payloadBuffer.put((byte) 3); 
        payloadBuffer.flip();

        Serializable[] result = comHealthMsg.receivePayload(payloadBuffer);

        assertNotNull(result);
        assertEquals(17, result.length);
        assertEquals(1000L, result[0]);
        assertEquals(5, result[1]);
        assertEquals(12000, result[2]);
        assertEquals(2500, result[3]);
        assertEquals(80, result[4]);
        assertEquals((short) 50, result[5]);
        
        assertEquals((short) 25, result[6]);
        assertEquals((short) 10, result[7]);
        assertEquals((short) -5, result[8]);
        assertEquals(70, result[9]);
        assertEquals(60, result[10]);
        assertEquals(120, result[11]);
        
        assertEquals(500, result[12]);
        assertEquals((short) 15, result[13]);
        assertEquals((short) 20, result[14]);
        assertEquals((short) 2, result[15]);
        assertEquals((short) 3, result[16]);
    }

    @Test
    public void testGetOutputFormat() {
        ComHealthMsg comHealthMsg = new ComHealthMsg();
        DataField[] outputFormat = comHealthMsg.getOutputFormat();

        assertNotNull(outputFormat);
        assertEquals(17, outputFormat.length);

        assertEquals("uptime", outputFormat[0].getName());
        assertEquals("BIGINT", outputFormat[0].getType());

        assertEquals("rx_dropped", outputFormat[16].getName());
        assertEquals("SMALLINT", outputFormat[16].getType());
    }

    @Test
    public void testGetType() {
        ComHealthMsg comHealthMsg = new ComHealthMsg();
        assertEquals(MessageTypes.DPP_MSG_TYPE_COM_HEALTH, comHealthMsg.getType());
    }

}
