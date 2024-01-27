package ch.epfl.gsn.wrappers.backlog.plugins;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.Serializable;

import org.junit.Test;

import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;

public class AbstractPluginTest {

    @Test
    public void testCheckAndCastData() throws Exception {
        // Define test data
        Serializable[] data = {1, 234567L, (short)3, (byte)4, 5.5};
        int dataoffset = 0;
        DataField[] datafields = {
            new DataField("field1", DataTypes.INTEGER),
            new DataField("field2", DataTypes.BIGINT),
            new DataField("field3", DataTypes.SMALLINT),
            new DataField("field4", DataTypes.TINYINT),
            new DataField("field5", DataTypes.DOUBLE),
        };
        int datafieldoffset = 0;

        // Call the method
        Serializable[] result = AbstractPlugin.checkAndCastData(data, dataoffset, datafields, datafieldoffset);

        // Assert the result
        assertArrayEquals(data, result);
    }

    @Test(expected = Exception.class)
    public void testCheckAndCastData_DataLengthMismatch() throws Exception {
        // Define test data
        Serializable[] data = {1, 2, 3};
        int dataoffset = 0;
        DataField[] datafields = {
            new DataField("field1", DataTypes.INTEGER),
            new DataField("field2", DataTypes.INTEGER)
        };
        int datafieldoffset = 0;

        // Call the method (expecting an exception)
        AbstractPlugin.checkAndCastData(data, dataoffset, datafields, datafieldoffset);
    }

    @Test(expected = Exception.class)
    public void testCheckAndCastData_InvalidDataType() throws Exception {
        // Define test data
        Serializable[] data = {1, "two", 3};
        int dataoffset = 0;
        DataField[] datafields = {
            new DataField("field1", DataTypes.INTEGER),
            new DataField("field2", DataTypes.INTEGER),
            new DataField("field3", DataTypes.INTEGER)
        };
        int datafieldoffset = 0;

        // Call the method (expecting an exception)
        AbstractPlugin.checkAndCastData(data, dataoffset, datafields, datafieldoffset);
    }

    @Test
    public void testConcat() {
        Integer[] first = {1, 2, 3};
        Integer[] second = {4, 5, 6};
        Integer[] expected = {1, 2, 3, 4, 5, 6};
        Integer[] result = AbstractPlugin.concat(first, second);
        assertArrayEquals(expected, result);
    }

    @Test
    public void testToLongWithNullValue() {
        try {
            Long result = AbstractPlugin.toLong(null);
            assertNull(result);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testToLongWithByteValue() {
        try {
            Byte value = 42;
            Long result = AbstractPlugin.toLong(value);
            assertEquals(value.longValue(), result.longValue());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testToLongWithShortValue() {
        try {
            Short value = 42;
            Long result = AbstractPlugin.toLong(value);
            assertEquals(value.longValue(), result.longValue());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testToLongWithIntegerValue() {
        try {
            Integer value = 42;
            Long result = AbstractPlugin.toLong(value);
            assertEquals(value.longValue(), result.longValue());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testToLongWithLongValue() {
        try {
            Long value = 42L;
            Long result = AbstractPlugin.toLong(value);
            assertEquals(value, result);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testToLongWithInvalidValue() {
        try {
            String value = "42";
            AbstractPlugin.toLong(value);
            fail("Expected exception was not thrown.");
        } catch (Exception e) {
            assertEquals("value can not be cast to Long.", e.getMessage());
        }
    }

    @Test
    public void testToDoubleWithByte() {
        try {
            Byte value = 10;
            Double result = AbstractPlugin.toDouble(value);
            assertEquals(10.0, result, 0.0);
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    public void testToDoubleWithShort() {
        try {
            Short value = 20;
            Double result = AbstractPlugin.toDouble(value);
            assertEquals(20.0, result, 0.0);
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    public void testToDoubleWithInteger() {
        try {
            Integer value = 30;
            Double result = AbstractPlugin.toDouble(value);
            assertEquals(30.0, result, 0.0);
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    public void testToDoubleWithLong() {
        try {
            Long value = 40L;
            Double result = AbstractPlugin.toDouble(value);
            assertEquals(40.0, result, 0.0);
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    public void testToDoubleWithDouble() {
        try {
            Double value = 50.0;
            Double result = AbstractPlugin.toDouble(value);
            assertEquals(50.0, result, 0.0);
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    public void testToDoubleWithNull() {
        try {
            Double result = AbstractPlugin.toDouble(null);
            assertNull(result);
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    public void testToDoubleWithInvalidType() {
        try {
            String value = "invalid";
            AbstractPlugin.toDouble(value);
            fail("Exception should be thrown");
        } catch (Exception e) {
            assertEquals("value can not be cast to Double.", e.getMessage());
        }
    }

    @Test
    public void testToIntegerWithNullValue() {
        try {
            Integer result = AbstractPlugin.toInteger(null);
            assertNull(result);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testToIntegerWithByteValue() {
        try {
            Byte value = 10;
            Integer result = AbstractPlugin.toInteger(value);
            assertEquals(Integer.valueOf(10), result);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testToIntegerWithShortValue() {
        try {
            Short value = 100;
            Integer result = AbstractPlugin.toInteger(value);
            assertEquals(Integer.valueOf(100), result);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testToIntegerWithIntegerValue() {
        try {
            Integer value = 1000;
            Integer result = AbstractPlugin.toInteger(value);
            assertEquals(Integer.valueOf(1000), result);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testToIntegerWithInvalidValue() {
        try {
            String value = "invalid";
            AbstractPlugin.toInteger(value);
            fail("Expected exception was not thrown.");
        } catch (Exception e) {
            assertEquals("value (type=java.lang.String, value=invalid) can not be cast to Integer.", e.getMessage());
        }
    }

    @Test
    public void testToShortWithNullValue() {
        try {
            Short result = AbstractPlugin.toShort(null);
            assertNull(result);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testToShortWithByteValue() {
        try {
            Byte value = 10;
            Short result = AbstractPlugin.toShort(value);
            assertEquals(value.shortValue(), result.shortValue());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testToShortWithShortValue() {
        try {
            Short value = 100;
            Short result = AbstractPlugin.toShort(value);
            assertEquals(value, result);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testToShortWithInvalidValue() {
        try {
            String value = "invalid";
            AbstractPlugin.toShort(value);
            fail("Expected exception was not thrown.");
        } catch (Exception e) {
            assertEquals("value can not be cast to Short.", e.getMessage());
        }
    }
    
}
