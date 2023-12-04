package ch.epfl.gsn.delivery;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;

import org.junit.Test;

public class Field4RestTest {
    @Test
    public void testConstructorAndAccessors() {
        String name = "fieldName";
        Byte type = 1;
        Serializable value = "fieldValue";


        Field4Rest field4Rest = new Field4Rest(name, type, value);

        assertEquals(name, field4Rest.getName());
        assertEquals(type.byteValue(), field4Rest.getType());
        assertEquals(value, field4Rest.getValue());
    }

    @Test
    public void testToString() {
        String name = "fieldName";
        Byte type = 1;
        Serializable value = "fieldValue";

        Field4Rest field4Rest = new Field4Rest(name, type, value);

        String expectedToString = "Field(name:fieldName,type:1,value:fieldValue)";
        assertEquals(expectedToString, field4Rest.toString());
    }
}
