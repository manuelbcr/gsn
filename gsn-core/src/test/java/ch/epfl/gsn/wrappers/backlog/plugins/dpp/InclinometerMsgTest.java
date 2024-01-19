package ch.epfl.gsn.wrappers.backlog.plugins.dpp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Serializable;
import java.nio.ByteBuffer;

import org.junit.Test;

import ch.epfl.gsn.beans.DataField;

public class InclinometerMsgTest {

    @Test
    public void testReceivePayload() throws Exception {
        InclinometerMsg inclinometerMsg = new InclinometerMsg();

        ByteBuffer payloadBuffer = ByteBuffer.allocate(14);
        payloadBuffer.putShort((short) 1000); 
        payloadBuffer.putShort((short) -500); 
        payloadBuffer.putShort((short) 1500); 
        payloadBuffer.putShort((short) 200); 
        payloadBuffer.putShort((short) -100); 
        payloadBuffer.putShort((short) 300); 
        payloadBuffer.putShort((short) 25);
        payloadBuffer.flip();

        Serializable[] result = inclinometerMsg.receivePayload(payloadBuffer);

        assertNotNull(result);
        assertEquals(7, result.length);
        assertEquals(1000, result[0]);
        assertEquals(-500, result[1]);
        assertEquals(1500, result[2]);
        assertEquals(200, result[3]);
        assertEquals(-100, result[4]);
        assertEquals(300, result[5]);
        assertEquals(25, result[6]);
    }

    @Test
    public void testGetOutputFormat() {
        InclinometerMsg inclinometerMsg = new InclinometerMsg();
        DataField[] outputFormat = inclinometerMsg.getOutputFormat();

        assertNotNull(outputFormat);
        assertEquals(7, outputFormat.length);

        assertEquals("acc_x", outputFormat[0].getName());
        assertEquals("INTEGER", outputFormat[0].getType());
        assertEquals("acc_y", outputFormat[1].getName());
        assertEquals("INTEGER", outputFormat[1].getType());
        assertEquals("acc_z", outputFormat[2].getName());
        assertEquals("INTEGER", outputFormat[2].getType());
        assertEquals("ang_x", outputFormat[3].getName());
        assertEquals("INTEGER", outputFormat[3].getType());
        assertEquals("ang_y", outputFormat[4].getName());
        assertEquals("INTEGER", outputFormat[4].getType());
        assertEquals("ang_z", outputFormat[5].getName());
        assertEquals("INTEGER", outputFormat[5].getType());
        assertEquals("temperature", outputFormat[6].getName());
        assertEquals("INTEGER", outputFormat[6].getType());
    }

    @Test
    public void testGetType() {
        InclinometerMsg inclinometerMsg = new InclinometerMsg();
        assertEquals(MessageTypes.DPP_MSG_TYPE_INCLINO, inclinometerMsg.getType());
    }
}
