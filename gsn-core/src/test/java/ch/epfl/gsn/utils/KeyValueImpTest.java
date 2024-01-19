package ch.epfl.gsn.utils;

import org.junit.Test;
import static org.junit.Assert.*;

public class KeyValueImpTest {
    @Test
    public void testEquals() {
        KeyValueImp predicate1 = new KeyValueImp("key1", "value1");
        KeyValueImp predicate2 = new KeyValueImp("key1", "value1");
        assertEquals(predicate1, predicate2);
    }


    @Test
    public void testHashCode() {
        KeyValueImp predicate1 = new KeyValueImp("key", "value");
        KeyValueImp predicate2 = new KeyValueImp("key", "value");
        assertEquals(predicate1.hashCode(), predicate2.hashCode());
    }

    @Test
    public void testToString() {
        KeyValueImp predicate = new KeyValueImp("key", "value");
        assertEquals("Predicate ( Key = key, Value = value )\n", predicate.toString());
    }

    @Test
    public void testValueInBoolean() {
        KeyValueImp predicate = new KeyValueImp("key", "true");
        assertTrue(predicate.valueInBoolean());

        KeyValueImp invalidPredicate = new KeyValueImp("key", "invalid");
        assertFalse(invalidPredicate.valueInBoolean());
    }

    @Test
    public void testValueInInteger() {
        KeyValueImp predicate = new KeyValueImp("key", "42");
        assertEquals(42, predicate.valueInInteger());

        KeyValueImp invalidPredicate = new KeyValueImp("key", "invalid");
        assertEquals(0, invalidPredicate.valueInInteger());
    }


    @Test
    public void testGetKey() {
        KeyValueImp predicate = new KeyValueImp("key", "value");
        assertEquals("key", predicate.getKey());
    }

    @Test
    public void testGetValue() {
        KeyValueImp predicate = new KeyValueImp("key", "value");
        assertEquals("value", predicate.getValue());
    }

    @Test
    public void testSetKey() {
        KeyValueImp predicate = new KeyValueImp("oldKey", "value");
        predicate.setKey("newKey");
        assertEquals("newKey", predicate.getKey());
    }

    @Test
    public void testSetValue() {
        KeyValueImp predicate = new KeyValueImp("key", "oldValue");
        predicate.setValue("newValue");
        assertEquals("newValue", predicate.getValue());
    }
}
