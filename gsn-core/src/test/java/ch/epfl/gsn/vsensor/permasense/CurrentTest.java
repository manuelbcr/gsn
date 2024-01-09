package ch.epfl.gsn.vsensor.permasense;

import static org.junit.Assert.*;

import org.junit.Test;

public class CurrentTest {
    @Test
    public void testConvert() {
        Current current = new Current();

        String result1 = current.convert(2, "4", null);
        assertEquals("8.000", result1);

        String result2 = current.convert(1, "", null);
        assertNull(result2);

        String result3 = current.convert(null, "2.5", null);
        assertNull(result3);

        String result4 = current.convert(0xffff, "2.0", null);
        assertNull(result4);

        String result5 = current.convert(3, "-2", null);
        assertEquals("-6.000", result5);

        String result6 = current.convert(2, "1000000.5", null);
        assertEquals("2000001.000", result6);
    }
}
