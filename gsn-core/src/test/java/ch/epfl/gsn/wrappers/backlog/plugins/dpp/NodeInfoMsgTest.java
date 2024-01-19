package ch.epfl.gsn.wrappers.backlog.plugins.dpp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import ch.epfl.gsn.beans.DataField;

public class NodeInfoMsgTest {
    
    @Test
    public void testReceivePayload() throws Exception {
        NodeInfoMsg nodeInfoMsg = new NodeInfoMsg();

        ByteBuffer payloadBuffer = ByteBuffer.allocate(56);
        payloadBuffer.put((byte) 1); 
        payloadBuffer.put((byte) 2); 
        payloadBuffer.putShort((short) 500); 

        String mcuDescValue = "CC430F5147";
        byte[] mcuDescBytes = new byte[12];
        byte[] mcuDescBytesValue = mcuDescValue.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(mcuDescBytesValue, 0, mcuDescBytes, 0, Math.min(mcuDescBytesValue.length, mcuDescBytes.length));
        payloadBuffer.put(mcuDescBytes);

    
        String compilerDescValue = "GCC";
        byte[] compilerDescBytes = new byte[4];
        byte[] compilerDescBytesValue = compilerDescValue.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(compilerDescBytesValue, 0, compilerDescBytes, 0, Math.min(compilerDescBytesValue.length, compilerDescBytes.length));
        payloadBuffer.put(compilerDescBytes);

        payloadBuffer.putInt(12345); 
        payloadBuffer.putInt(67890); 

  
        String fwNameValue = "MyFw";
        byte[] fwNameBytes = new byte[8];
        byte[] fwNameBytesValue = fwNameValue.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(fwNameBytesValue, 0, fwNameBytes, 0, Math.min(fwNameBytesValue.length, fwNameBytes.length));
        payloadBuffer.put(fwNameBytes);

        payloadBuffer.putShort((short) 100); 
        payloadBuffer.putInt(98765); 
        payloadBuffer.putInt(87654); 

        payloadBuffer.flip();

        Serializable[] result = nodeInfoMsg.receivePayload(payloadBuffer);

        assertNotNull(result);
        assertEquals(11, result.length);
        assertEquals((short) 1, result[0]);
        assertEquals((short) 2, result[1]);
        assertEquals(500, result[2]);
        assertEquals(mcuDescValue, result[3]);
        assertEquals(compilerDescValue, result[4]);
        assertEquals(12345L, result[5]);
        assertEquals(67890L, result[6]);
        assertEquals(fwNameValue, result[7]);
        assertEquals(100, result[8]);
        assertEquals(98765L, result[9]);
        assertEquals(87654L, result[10]);
    }

    @Test
    public void testGetOutputFormat() {
        NodeInfoMsg nodeInfoMsg = new NodeInfoMsg();
        DataField[] outputFormat = nodeInfoMsg.getOutputFormat();

        assertNotNull(outputFormat);
        assertEquals(11, outputFormat.length);

        assertEquals("component_id", outputFormat[0].getName());
        assertEquals("SMALLINT", outputFormat[0].getType());
        assertEquals("config", outputFormat[10].getName());
        assertEquals("BIGINT", outputFormat[10].getType());
    }

    @Test
    public void testGetType() {
        NodeInfoMsg nodeInfoMsg = new NodeInfoMsg();
        assertEquals(MessageTypes.DPP_MSG_TYPE_NODE_INFO, nodeInfoMsg.getType());
    }
    
}
