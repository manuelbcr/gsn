package ch.epfl.gsn.wrappers.backlog.plugins.dpp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Serializable;
import java.nio.ByteBuffer;

import org.junit.Test;

import ch.epfl.gsn.beans.DataField;

public class LwbHealthMsgTest {

    @Test
    public void testReceivePayload() throws Exception {
        LwbHealthMsg lwbHealthMsg = new LwbHealthMsg();

        ByteBuffer payloadBuffer = ByteBuffer.allocate(24);
        payloadBuffer.put((byte) 10); 
        payloadBuffer.put((byte) 5); 
        payloadBuffer.putShort((short) 3000); 
        payloadBuffer.putShort((short) 500); 
        payloadBuffer.putShort((short) 1000); 
        payloadBuffer.put((byte) 8); 
        payloadBuffer.put((byte) 6); 
        payloadBuffer.put((byte) 4); 
        payloadBuffer.put((byte) 7); 
        payloadBuffer.put((byte) 2); 
        payloadBuffer.put((byte) 3); 
        payloadBuffer.put((byte) 15); 
        payloadBuffer.flip();

        Serializable[] result = lwbHealthMsg.receivePayload(payloadBuffer);

        assertNotNull(result);
        assertEquals(12, result.length);
        assertEquals((short)10, result[0]);
        assertEquals((short)5, result[1]);
        assertEquals(3000, result[2]);
        assertEquals(500, result[3]);
        assertEquals(1000, result[4]);
        assertEquals((short)8, result[5]);
        assertEquals((short)6, result[6]);
        assertEquals((short)4, result[7]);
        assertEquals((short)7, result[8]);
        assertEquals((short)2, result[9]);
        assertEquals((short)3, result[10]);
        assertEquals((short)15, result[11]);
    }

    @Test
    public void testGetOutputFormat() {
        LwbHealthMsg lwbHealthMsg = new LwbHealthMsg();
        DataField[] outputFormat = lwbHealthMsg.getOutputFormat();

        assertNotNull(outputFormat);
        assertEquals(12, outputFormat.length);

        assertEquals("bootstrap_cnt", outputFormat[0].getName());
        assertEquals("SMALLINT", outputFormat[0].getType());
        assertEquals("bus_load", outputFormat[11].getName());
        assertEquals("SMALLINT", outputFormat[11].getType());
    }

    @Test
    public void testGetType() {
        LwbHealthMsg lwbHealthMsg = new LwbHealthMsg();
        assertEquals(MessageTypes.DPP_MSG_TYPE_LWB_HEALTH, lwbHealthMsg.getType());
    }
}

