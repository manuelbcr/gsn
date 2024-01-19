package ch.epfl.gsn.vsensor.permasense;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class RotationXTest {
    @Test
    public void testConvert() {
        RotationX rotationX = new RotationX();

        String result1 = rotationX.convert(1.5, "45.0", "3");
        assertEquals("-1.061", result1);

        String result2 = rotationX.convert("1.5", "45.0", "3");
        assertEquals("-1.061", result2);

        String result3 = rotationX.convert(true, "45.0", "2.5");
        assertNull(result3);

        String result4 = rotationX.convert(1.5, "45.0", true);
        assertNull(result4);

        String result5 = rotationX.convert(null, "45.0", "2.5");
        assertNull(result5);

        String result6 = rotationX.convert(2.5, "45.0", null);
        assertNull(result6);

        String result7 = rotationX.convert(2.5, "invalid", "2.5");
        assertNull(result7);
    }
}
