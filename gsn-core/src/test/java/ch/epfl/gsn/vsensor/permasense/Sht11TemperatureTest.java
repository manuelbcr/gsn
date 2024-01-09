package ch.epfl.gsn.vsensor.permasense;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class Sht11TemperatureTest {
    @Test
    public void testConvert() {
        Sht11Temperature sht11Temperature = new Sht11Temperature();

        String result1 = sht11Temperature.convert(20, "20.0", 20);
        assertEquals("-39.430", result1);

        String result2 = sht11Temperature.convert(0xffff, "30.0", 400);
        assertNull(result2);

        String result3 = sht11Temperature.convert(null, "25.0", 300);
        assertNull(result3);

    }
}
