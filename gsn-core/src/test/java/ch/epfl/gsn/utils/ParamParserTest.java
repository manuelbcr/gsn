package ch.epfl.gsn.utils;

import org.junit.Test;
import static org.junit.Assert.*;

public class ParamParserTest {
    @Test
    public void testGetInteger() {
        String input = "42";
        int defaultValue = 0;
        int result = ParamParser.getInteger(input, defaultValue);
        assertEquals(42, result);

        input = "not_an_integer";
        result = ParamParser.getInteger(input, defaultValue);
        assertEquals(defaultValue, result);

        input = null;
        result = ParamParser.getInteger(input, defaultValue);
        assertEquals(defaultValue, result);

    }

    @Test
    public void testGetIntegerFromObject() {
        Object input = 42;
        int defaultValue = 0;
        int result = ParamParser.getInteger(input, defaultValue);
        assertEquals(42, result);

        input = "not_an_integer";
        result = ParamParser.getInteger(input, defaultValue);
        assertEquals(defaultValue, result);

        input = null;
        result = ParamParser.getInteger(input, defaultValue);
        assertEquals(defaultValue, result);

        input = 42.5;
        result = ParamParser.getInteger(input, defaultValue);
        assertEquals(42, result);
        
        input = new Object(); 
        result = ParamParser.getInteger(input, defaultValue);
        assertEquals(defaultValue, result);
    }

}
