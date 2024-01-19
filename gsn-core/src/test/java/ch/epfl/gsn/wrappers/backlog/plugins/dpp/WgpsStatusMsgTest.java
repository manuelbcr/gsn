package ch.epfl.gsn.wrappers.backlog.plugins.dpp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Serializable;
import java.nio.ByteBuffer;

import org.junit.Test;

import ch.epfl.gsn.beans.DataField;

public class WgpsStatusMsgTest {

    @Test
    public void testReceivePayload() throws Exception {
        WgpsStatusMsg wgpsStatusMsg = new WgpsStatusMsg();

        ByteBuffer payloadBuffer = ByteBuffer.allocate(24);
        payloadBuffer.putShort((short) 123); 
        payloadBuffer.putShort((short) 456); 
        payloadBuffer.putShort((short) 789); 
        payloadBuffer.putShort((short) 101); 
        payloadBuffer.putShort((short) 112); 
        payloadBuffer.putShort((short) 131); 
        payloadBuffer.putInt(56789); 
        payloadBuffer.putShort((short) 1); 

        payloadBuffer.flip();

        Serializable[] result = wgpsStatusMsg.receivePayload(payloadBuffer);

        assertNotNull(result);
        assertEquals(8, result.length);
        assertEquals(123, result[0]);
        assertEquals(456, result[1]);
        assertEquals(789, result[2]);
        assertEquals(101, result[3]);
        assertEquals(112, result[4]);
        assertEquals(131, result[5]);
        assertEquals(56789L, result[6]);
        assertEquals(1, result[7]);
    }

    @Test
    public void testGetOutputFormat() {
        WgpsStatusMsg wgpsStatusMsg = new WgpsStatusMsg();
        DataField[] outputFormat = wgpsStatusMsg.getOutputFormat();

        assertNotNull(outputFormat);
        assertEquals(8, outputFormat.length);

        assertEquals("inc_x", outputFormat[0].getName());
        assertEquals("INTEGER", outputFormat[0].getType());
        assertEquals("inc_y", outputFormat[1].getName());
        assertEquals("INTEGER", outputFormat[1].getType());
        assertEquals("status", outputFormat[7].getName());
        assertEquals("INTEGER", outputFormat[7].getType());
    }

    @Test
    public void testGetType() {
        WgpsStatusMsg wgpsStatusMsg = new WgpsStatusMsg();
        assertEquals(MessageTypes.DPP_MSG_TYPE_WGPS_STATUS, wgpsStatusMsg.getType());
    }
}
