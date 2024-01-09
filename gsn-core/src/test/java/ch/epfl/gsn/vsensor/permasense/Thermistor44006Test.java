package ch.epfl.gsn.vsensor.permasense;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class Thermistor44006Test {

    @Test
    public void testConvert() {
        Thermistor44006 thermistor44006 = new Thermistor44006();

        
        String result1 = thermistor44006.convert(20, "20.0", 20);
        assertEquals("354.6577", result1);

        String result2 = thermistor44006.convert(0, "30.0", 400);
        assertNull(result2);

        String result3 = thermistor44006.convert(640001, "20.0", 20);
        assertNull(result3);

        String result4 = thermistor44006.convert(null, "25.0", 400);
        assertNull(result4);
    }
}
