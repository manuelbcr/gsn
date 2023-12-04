package ch.epfl.gsn.delivery.datarequest;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FieldsCollectionTest {
    @Test
    public void testFieldsCollectionInitialization() {
        String[] inputFields = {"input1", "input2","timed"};
        FieldsCollection fieldsCollection = new FieldsCollection(inputFields);
        assertTrue(fieldsCollection.isWantTimed());
        assertArrayEquals(inputFields, fieldsCollection.getFields());

        inputFields = new String[]{"input1", "input2"};
        fieldsCollection = new FieldsCollection(inputFields);
        assertFalse(fieldsCollection.isWantTimed());
        String[] expectedFields = {"input1", "input2", "timed"};
        assertArrayEquals(expectedFields, fieldsCollection.getFields());
    }
}
