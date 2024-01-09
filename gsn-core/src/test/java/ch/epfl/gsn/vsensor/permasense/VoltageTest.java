package ch.epfl.gsn.vsensor.permasense;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class VoltageTest {
    @Test
    public void testConvert() {
        Voltage voltage = new Voltage();

        
        String result1 = voltage.convert(20, "20.0", 20);
        assertEquals("400.000", result1);

        String result3 = voltage.convert(null, "25.0", 400);
        assertNull(result3);
    }
}