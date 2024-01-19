package ch.epfl.gsn.vsensor.permasense;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class Sht21TemperatureTest {
    @Test
    public void testConvert() {
        Sht21Temperature sht21Temperature = new Sht21Temperature();

        String result1 = sht21Temperature.convert(20, "20.0", 20);
        assertEquals("-46.635", result1);

        String result2 = sht21Temperature.convert(0xffff, "30.0", 400);
        assertNull(result2);

        String result3 = sht21Temperature.convert(null, "25.0", 300);
        assertNull(result3);
    }
}
