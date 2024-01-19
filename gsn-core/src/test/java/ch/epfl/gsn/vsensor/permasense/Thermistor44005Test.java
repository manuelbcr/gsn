package ch.epfl.gsn.vsensor.permasense;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class Thermistor44005Test {
    @Test
    public void testConvert() {
        Thermistor44005 thermistor44005 = new Thermistor44005();

        
        String result1 = thermistor44005.convert(20, "20.0", 20);
        assertEquals("303.7734", result1);

        String result2 = thermistor44005.convert(0, "30.0", 400);
        assertNull(result2);

        String result3 = thermistor44005.convert(640001, "20.0", 20);
        assertNull(result3);

        String result4 = thermistor44005.convert(null, "25.0", 400);
        assertNull(result4);
    }

}
