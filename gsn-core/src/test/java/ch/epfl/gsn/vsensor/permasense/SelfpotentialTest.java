package ch.epfl.gsn.vsensor.permasense;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class SelfpotentialTest {
    @Test
    public void testConvert() {
        Selfpotential selfpotential = new Selfpotential();
        String result1 = selfpotential.convert(50000, "3.5", null);
        assertEquals("250.000", result1);

        String result2 = selfpotential.convert(64001, "2.0", null);
        assertNull(result2);
        
        String result3 = selfpotential.convert(null, "999999.999", null);
        assertNull(result3);
    }
}
