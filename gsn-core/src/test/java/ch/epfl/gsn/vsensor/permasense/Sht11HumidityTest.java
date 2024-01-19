package ch.epfl.gsn.vsensor.permasense;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class Sht11HumidityTest {
    @Test
    public void testConvert() {
        Sht11Humidity sht11Humidity = new Sht11Humidity();

        String result1 = sht11Humidity.convert(20, "20.0", 20);
        assertEquals("-3.939", result1);

        String result2 = sht11Humidity.convert(0xffff, "30.0", 400);
        assertNull(result2);

        String result3 = sht11Humidity.convert(200, "28.0", 0xffff);
        assertNull(result3);

        String result4 = sht11Humidity.convert(null, "25.0", 300);
        assertNull(result4);

        String result5 = sht11Humidity.convert(100, "25.0", null);
        assertNull(result5);

    }

}
