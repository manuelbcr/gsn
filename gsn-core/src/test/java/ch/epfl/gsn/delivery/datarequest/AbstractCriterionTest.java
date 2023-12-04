package ch.epfl.gsn.delivery.datarequest;

import static org.junit.Assert.assertEquals;

import java.util.Hashtable;

import org.junit.Before;
import org.junit.Test;

public class AbstractCriterionTest {

    private AbstractCriterion abstractCriterion;
    private Hashtable<String, String> allowedValues;

    @Before
    public void setUp() {
        abstractCriterion = new AbstractCriterion();
        allowedValues = new Hashtable<>();
        allowedValues.put("key1", "value1");
        allowedValues.put("key2", "value2");
    }

    @Test
    public void testGetCriterionValid() throws DataRequestException {
        String result = abstractCriterion.getCriterion("key1", allowedValues);
        assertEquals("value1", result);
    }

    @Test(expected = DataRequestException.class)
    public void testGetCriterionInvalid() throws DataRequestException {
        abstractCriterion.getCriterion("invalidKey", allowedValues);
    }
}


