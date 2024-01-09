package ch.epfl.gsn.wrappers.backlog.plugins.dpp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Serializable;
import java.nio.ByteBuffer;

import org.junit.Test;

import ch.epfl.gsn.beans.DataField;

public class GnssSvMsgTest {
        @Test
    public void testReceivePayload() throws Exception {
        GnssSvMsg gnssSvMsg = new GnssSvMsg();

        ByteBuffer payloadBuffer = ByteBuffer.allocate(50);
        payloadBuffer.putDouble(123456.789); 
        payloadBuffer.putShort((short) 2048);
        payloadBuffer.put((byte) 37); 
        payloadBuffer.put((byte) 10); 
        payloadBuffer.put((byte) 3); 
        payloadBuffer.putDouble(98765.432); 
        payloadBuffer.putDouble(54321.012); 
        payloadBuffer.putFloat(12.34f); 
        payloadBuffer.put((byte) 1); 
        payloadBuffer.put((byte) 10); 
        payloadBuffer.put((byte) 25); 
        payloadBuffer.putShort((short) 512); 
        payloadBuffer.put((byte) 5); 
        payloadBuffer.put((byte) 8); 
        payloadBuffer.put((byte) 2); 
        payloadBuffer.put((byte) 15); 
        payloadBuffer.flip();

        Serializable[] result = gnssSvMsg.receivePayload(payloadBuffer);

        assertNotNull(result);
        assertEquals(16, result.length);
        assertEquals(123456.789, result[0]);
        assertEquals(2048, result[1]);
        assertEquals((short) 37, result[2]);
        assertEquals((short)10, result[3]);
        assertEquals((short)3, result[4]);
        assertEquals(98765.432, result[5]);
        assertEquals(54321.012, result[6]);
        assertEquals(12.34f, result[7]);
        assertEquals((short)1, result[8]);
        assertEquals((short)10, result[9]);
        assertEquals((short)25, result[10]);
        assertEquals(512, result[11]);
        assertEquals((short)5, result[12]);
        assertEquals((short)8, result[13]);
        assertEquals((short)2, result[14]);
        assertEquals((short)15, result[15]);
    }

    @Test
    public void testGetOutputFormat() {
        GnssSvMsg gnssSvMsg = new GnssSvMsg();
        DataField[] outputFormat = gnssSvMsg.getOutputFormat();

        assertNotNull(outputFormat);
        assertEquals(16, outputFormat.length);

        assertEquals("rcvtow", outputFormat[0].getName());
        assertEquals("DOUBLE", outputFormat[0].getType());
        assertEquals("week", outputFormat[1].getName());
        assertEquals("INTEGER", outputFormat[1].getType());
        assertEquals("leaps", outputFormat[2].getName());
        assertEquals("SMALLINT", outputFormat[2].getType());
        assertEquals("nummeas", outputFormat[3].getName());
        assertEquals("SMALLINT", outputFormat[3].getType());
        assertEquals("recstat", outputFormat[4].getName());
        assertEquals("SMALLINT", outputFormat[4].getType());
        assertEquals("prmes", outputFormat[5].getName());
        assertEquals("DOUBLE", outputFormat[5].getType());
        assertEquals("cpmes", outputFormat[6].getName());
        assertEquals("DOUBLE", outputFormat[6].getType());
        assertEquals("domes", outputFormat[7].getName());
        assertEquals("DOUBLE", outputFormat[7].getType());
        assertEquals("gnssid", outputFormat[8].getName());
        assertEquals("SMALLINT", outputFormat[8].getType());
        assertEquals("svid", outputFormat[9].getName());
        assertEquals("SMALLINT", outputFormat[9].getType());
        assertEquals("cno", outputFormat[10].getName());
        assertEquals("SMALLINT", outputFormat[10].getType());
        assertEquals("locktime", outputFormat[11].getName());
        assertEquals("INTEGER", outputFormat[11].getType());
        assertEquals("prstdev", outputFormat[12].getName());
        assertEquals("SMALLINT", outputFormat[12].getType());
        assertEquals("cpstdev", outputFormat[13].getName());
        assertEquals("SMALLINT", outputFormat[13].getType());
        assertEquals("dostdev", outputFormat[14].getName());
        assertEquals("SMALLINT", outputFormat[14].getType());
        assertEquals("trkstat", outputFormat[15].getName());
        assertEquals("SMALLINT", outputFormat[15].getType());
    }

    @Test
    public void testGetType() {
        GnssSvMsg gnssSvMsg = new GnssSvMsg();
        assertEquals(MessageTypes.DPP_MSG_TYPE_GNSS_SV, gnssSvMsg.getType());
    }
}
