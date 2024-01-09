package ch.epfl.gsn.vsensor.permasense;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class MspTemperatureTest {
    @Test
    public void testConvert() {
        MspTemperature mspTemp= new MspTemperature();
        String result1 = mspTemp.convert(32000, "3.5", null);
        assertEquals("3024.116", result1);

        String result2 = mspTemp.convert(65535, "2.0", null);
        assertNull(result2);

        String result3 = mspTemp.convert(null, "999999.999", null);
        assertNull(result3);

    }
}
