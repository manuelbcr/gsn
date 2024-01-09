package ch.epfl.gsn.vsensor.permasense;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class MultiplicationTest {
    @Test
    public void testConvert() {
        Multiplication multiplication= new Multiplication();
        
        String result1 = multiplication.convert(2, "3.5", null);
        assertEquals("7.000", result1);

        String result2 = multiplication.convert(0xffff, "2.0", null);
        assertNull(result2);
        
        String result3 = multiplication.convert(null, "999999.999", null);
        assertNull(result3);

        String result4 = multiplication.convert(-3, "2.0", null);
        assertEquals("-6.000", result4);

    }
}
