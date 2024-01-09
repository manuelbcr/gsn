package ch.epfl.gsn.wrappers.backlog.plugins.dpp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Serializable;
import java.nio.ByteBuffer;

import org.junit.Test;

import ch.epfl.gsn.beans.DataField;

public class ImuMsgTest {

    @Test
    public void testReceivePayload() throws Exception {
        ImuMsg imuMsg = new ImuMsg();

        ByteBuffer payloadBuffer = ByteBuffer.allocate(12);
        payloadBuffer.putShort((short) 1000); 
        payloadBuffer.putShort((short) -500); 
        payloadBuffer.putShort((short) 1500); 
        payloadBuffer.putShort((short) 200); 
        payloadBuffer.putShort((short) -100); 
        payloadBuffer.putShort((short) 300); 
        payloadBuffer.flip();

        Serializable[] result = imuMsg.receivePayload(payloadBuffer);

        assertNotNull(result);
        assertEquals(6, result.length);
        assertEquals(1000, result[0]);
        assertEquals(-500, result[1]);
        assertEquals(1500, result[2]);
        assertEquals(200, result[3]);
        assertEquals(-100, result[4]);
        assertEquals(300, result[5]);
    }

    @Test
    public void testGetOutputFormat() {
        ImuMsg imuMsg = new ImuMsg();
        DataField[] outputFormat = imuMsg.getOutputFormat();

        assertNotNull(outputFormat);
        assertEquals(6, outputFormat.length);

        assertEquals("acc_x", outputFormat[0].getName());
        assertEquals("INTEGER", outputFormat[0].getType());
        assertEquals("acc_y", outputFormat[1].getName());
        assertEquals("INTEGER", outputFormat[1].getType());
        assertEquals("acc_z", outputFormat[2].getName());
        assertEquals("INTEGER", outputFormat[2].getType());
        assertEquals("mag_x", outputFormat[3].getName());
        assertEquals("INTEGER", outputFormat[3].getType());
        assertEquals("mag_y", outputFormat[4].getName());
        assertEquals("INTEGER", outputFormat[4].getType());
        assertEquals("mag_z", outputFormat[5].getName());
        assertEquals("INTEGER", outputFormat[5].getType());
    }

    @Test
    public void testGetType() {
        ImuMsg imuMsg = new ImuMsg();
        assertEquals(MessageTypes.DPP_MSG_TYPE_IMU, imuMsg.getType());
    }
}


