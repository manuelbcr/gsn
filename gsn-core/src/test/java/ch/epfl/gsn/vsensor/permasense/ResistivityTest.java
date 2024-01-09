package ch.epfl.gsn.vsensor.permasense;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ResistivityTest {
    @Test
    public void testConvert() {
        Resistivity resistivity = new Resistivity();
        String result1 = resistivity.convert(2, "3.5", null);
        assertEquals("31999.000", result1);

        String result2 = resistivity.convert(0, "2.0", null);
        assertNull(result2);
        
        String result3 = resistivity.convert(null, "999999.999", null);
        assertNull(result3);
    }
}
