package ch.epfl.gsn.vsensor.permasense;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class Sht21HumidityTest {
    @Test
    public void testConvert() {
        Sht21Humidity sht21Humidity = new Sht21Humidity();

        String result1 = sht21Humidity.convert(20, "20.0", 20);
        assertEquals("-5.390", result1);

        String result2 = sht21Humidity.convert(0xffff, "30.0", 400);
        assertNull(result2);

        String result3 = sht21Humidity.convert(null, "25.0", 300);
        assertNull(result3);

    }
}