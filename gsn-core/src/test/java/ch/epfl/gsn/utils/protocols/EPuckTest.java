package ch.epfl.gsn.utils.protocols.EPuck;

import static org.junit.Assert.*;
import org.mockito.Mockito;
import java.util.Vector;

import org.junit.Ignore;
import org.junit.Test;


public class EPuckTest {
    public enum LED_STATE {OFF, ON, INVERSE};

    @Test
    public void testBuildRawQuery() {
        BodyLED bodyLED = new BodyLED("TestBodyLED");

        Vector<Object> mockParams = Mockito.mock(Vector.class);
        Mockito.when(mockParams.firstElement()).thenReturn(LED_STATE.ON.ordinal());

        byte[] rawQuery = bodyLED.buildRawQuery(mockParams);

        assertNotNull(rawQuery);
        assertArrayEquals(new byte[] { 'B', ',', '1' }, rawQuery);


        Reset reset = new Reset("TestReset");

        Vector<Object> mockParams1 = Mockito.mock(Vector.class);

        byte[] rawQuery1 = reset.buildRawQuery(mockParams1);

        assertNotNull(rawQuery1);
        assertArrayEquals(new byte[] { 'r' }, rawQuery1);

        
        SetSpeed setSpeed = new SetSpeed("TestSetSpeed");
        Vector<Object> validParams = new Vector<>();
        validParams.add("10"); 
        validParams.add("20"); 

        byte[] rawQueryValid = setSpeed.buildRawQuery(validParams);
        assertNotNull(rawQueryValid);
        assertArrayEquals(new byte[] { 'D', ',', '1', '0', ',', '2', '0' }, rawQueryValid);
    }

    @Test
    public void testBuildRawQueryWithInvalidParams() {

        BodyLED bodyLED = new BodyLED("TestBodyLED");

        Vector<Object> mockParams = Mockito.mock(Vector.class);
        Mockito.when(mockParams.firstElement()).thenReturn("InvalidParam");

        byte[] rawQuery = bodyLED.buildRawQuery(mockParams);

        assertNull(rawQuery);
    }

    @Test
    public void testSerComProtocol() {
        SerComProtocol serComProtocol = new SerComProtocol();
        assertEquals(SerComProtocol.EPUCK_PROTOCOL, serComProtocol.EPUCK_PROTOCOL);
    }
}
