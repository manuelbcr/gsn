package ch.epfl.gsn.vsensor.permasense;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

public class RotationYTest {
    @Test
    public void testConvert() {
        RotationY rotationY = new RotationY();

        String result1 = rotationY.convert(1.5, "45.0", "3");
        assertEquals("3.182", result1);

        String result2 = rotationY.convert("1.5", "45.0", "3");
        assertEquals("3.182", result2);

        String result3 = rotationY.convert(true, "45.0", "2.5");
        assertNull(result3);

        String result4 = rotationY.convert(1.5, "45.0", true);
        assertNull(result4);

        String result5 = rotationY.convert(null, "45.0", "2.5");
        assertNull(result5);

        String result6 = rotationY.convert(2.5, "45.0", null);
        assertNull(result6);

        String result7 = rotationY.convert(2.5, "invalid", "2.5");
        assertNull(result7);
    }

}
