package ch.epfl.gsn.vsensor.permasense;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class PressureTest {
    @Test
    public void testConvert() {
        Pressure pressure= new Pressure();

        String result1 = pressure.convert(2, "3.5", null);
        assertEquals("0.2", result1);

        String result2 = pressure.convert(64001, "2.0", null);
        assertNull(result2);

        String result3 = pressure.convert(null, "999999.999", null);
        assertNull(result3);

    }
}
