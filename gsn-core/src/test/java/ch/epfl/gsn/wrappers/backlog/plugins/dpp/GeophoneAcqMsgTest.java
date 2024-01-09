package ch.epfl.gsn.wrappers.backlog.plugins.dpp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Serializable;
import java.nio.ByteBuffer;

import org.junit.Test;

import ch.epfl.gsn.beans.DataField;

public class GeophoneAcqMsgTest {
    @Test
    public void testReceivePayload() throws Exception {
        GeophoneAcqMsg geophoneAcqMsg = new GeophoneAcqMsg();

        //2 short 4 int 8 long
        ByteBuffer payloadBuffer = ByteBuffer.allocate(148);
        payloadBuffer.putLong(1630487896000L); 
        payloadBuffer.putLong(1630487897000L); 
        payloadBuffer.putInt(500); 
        payloadBuffer.putInt(200); 
        payloadBuffer.putInt(50); 
        payloadBuffer.putInt(150); 
        payloadBuffer.putInt(75); 
        payloadBuffer.putInt(10); 
        payloadBuffer.putInt(5); 
        payloadBuffer.putInt(30); 
        payloadBuffer.putInt(20); 
        payloadBuffer.putShort((short) 100); 
        payloadBuffer.putShort((short) 200); 
        payloadBuffer.putShort((short) 200); 
        payloadBuffer.put((byte) 1); 
        payloadBuffer.put((byte) 2); 
        payloadBuffer.putLong(0); 
        payloadBuffer.put((byte) 0); 
        payloadBuffer.flip();
    
        Serializable[] result = geophoneAcqMsg.receivePayload(payloadBuffer);
    
        assertNotNull(result);
        assertEquals(18, result.length);
        assertEquals(1630487896000L, result[0]);
        assertEquals(1630487897000L, result[1]);
        assertEquals(500L, result[2]);
        assertEquals(200L, result[3]);
        assertEquals(50L, result[4]);
        assertEquals(150L, result[5]);
        assertEquals(75L, result[6]);
        assertEquals(10L, result[7]);
        assertEquals(5L, result[8]);
        assertEquals(30L, result[9]);
        assertEquals(20L, result[10]);
        assertEquals(100, result[11]);
        assertEquals(200, result[12]);
        assertEquals(200, result[13]);
        assertEquals((short) 1, result[14]);
        assertEquals((short) 2, result[15]);
        assertEquals(0L, result[16]);
        assertEquals((short)0, result[17]);
    }

    @Test
    public void testGetOutputFormat() {
        GeophoneAcqMsg geophoneAcqMsg = new GeophoneAcqMsg();
        DataField[] outputFormat = geophoneAcqMsg.getOutputFormat();

        assertNotNull(outputFormat);
        assertEquals(18, outputFormat.length);

        assertEquals("start_time", outputFormat[0].getName());
        assertEquals("BIGINT", outputFormat[0].getType());
        assertEquals("BIGINT", outputFormat[1].getType());
        assertEquals("BIGINT", outputFormat[2].getType());
        assertEquals("BIGINT", outputFormat[3].getType());
        assertEquals("BIGINT", outputFormat[4].getType());
        assertEquals("BIGINT", outputFormat[5].getType());
        assertEquals("BIGINT", outputFormat[6].getType());
        assertEquals("BIGINT", outputFormat[7].getType());
        assertEquals("BIGINT", outputFormat[8].getType());
        assertEquals("BIGINT", outputFormat[9].getType());
        assertEquals("BIGINT", outputFormat[10].getType());
        assertEquals("INTEGER", outputFormat[11].getType());
        assertEquals("INTEGER", outputFormat[12].getType());
        assertEquals("INTEGER", outputFormat[13].getType());
        assertEquals("SMALLINT", outputFormat[14].getType());
        assertEquals("SMALLINT", outputFormat[15].getType());
        assertEquals("BIGINT", outputFormat[16].getType());
        assertEquals("adc_sps", outputFormat[17].getName());
        assertEquals("SMALLINT", outputFormat[17].getType());
    }

    @Test
    public void testGetType() {
        GeophoneAcqMsg geophoneAcqMsg = new GeophoneAcqMsg();
        assertEquals(MessageTypes.DPP_MSG_TYPE_GEOPHONE_ACQ, geophoneAcqMsg.getType());
    }
}

