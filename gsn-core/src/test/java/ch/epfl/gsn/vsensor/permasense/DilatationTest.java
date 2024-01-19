package ch.epfl.gsn.vsensor.permasense;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class DilatationTest {
    @Test
    public void testConvert() {
        Dilatation dilatation = new Dilatation();

        String result1 = dilatation.convert(32000, "3.5", null);
        assertEquals("1.7500", result1);

        String result2 = dilatation.convert(70000, "2.0", null);
        assertNull(result2);

        String result3 = dilatation.convert(null, "999999.999", null);
        assertNull(result3);
    }
}
