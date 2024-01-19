package ch.epfl.gsn.wrappers.backlog.plugins.dpp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Serializable;
import java.nio.ByteBuffer;

import org.junit.Test;

import ch.epfl.gsn.beans.DataField;

public class EventMsgTest {

    @Test
    public void testReceivePayload() throws Exception {
        EventMsg eventMsg = new EventMsg();

        ByteBuffer payloadBuffer = ByteBuffer.allocate(10);
        payloadBuffer.put((byte) 2); 
        payloadBuffer.put((byte) 2); 
        payloadBuffer.putInt(500); 
        payloadBuffer.flip();

        Serializable[] result = eventMsg.receivePayload(payloadBuffer);

        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals((short) 2, result[0]);
        assertEquals((short) 2, result[1]);
        assertEquals(500L, result[2]);
    }

    @Test
    public void testGetOutputFormat() {
        EventMsg eventMsg = new EventMsg();
        DataField[] outputFormat = eventMsg.getOutputFormat();

        assertNotNull(outputFormat);
        assertEquals(3, outputFormat.length);

        assertEquals("component_id", outputFormat[0].getName());
        assertEquals("SMALLINT", outputFormat[0].getType());
        assertEquals("type", outputFormat[1].getName());
        assertEquals("SMALLINT", outputFormat[1].getType());
        assertEquals("value", outputFormat[2].getName());
        assertEquals("BIGINT", outputFormat[2].getType());
    }

    @Test
    public void testGetType() {
        EventMsg eventMsg = new EventMsg();
        assertEquals(MessageTypes.DPP_MSG_TYPE_EVENT, eventMsg.getType());
    }


}

