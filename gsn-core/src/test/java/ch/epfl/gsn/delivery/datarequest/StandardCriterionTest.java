package ch.epfl.gsn.delivery.datarequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class StandardCriterionTest {
    @Test
    public void testStandardCriterionCreation() {
        try {
            StandardCriterion standardCriterion = new StandardCriterion("and::vsname:field:eq:value");
            assertEquals("and", standardCriterion.getCritJoin());
            assertEquals("", standardCriterion.getNegation());
            assertEquals("vsname", standardCriterion.getVsname());
            assertEquals("field", standardCriterion.getField());
            assertEquals(" equal", standardCriterion.getOperator());
            assertEquals("value", standardCriterion.getValue());
            
            StandardCriterion standardCriterion2 = new StandardCriterion();
            standardCriterion2.setCritField("field");
            standardCriterion2.setCritOperator("equal");
            standardCriterion2.setCritValue("value");
            standardCriterion2.setCritJoin("and");
            standardCriterion2.setCritNeg("");
            standardCriterion2.setCritVsname("vsname");
            assertEquals("and", standardCriterion2.getCritJoin());
            assertEquals("", standardCriterion2.getNegation());
            assertEquals("vsname", standardCriterion2.getVsname());
            assertEquals("field", standardCriterion2.getField());
            assertEquals("equal", standardCriterion2.getOperator());
            assertEquals("value", standardCriterion2.getValue());

            try {
                new StandardCriterion("and::vsname:field:eq");
                fail("Expected DataRequestException");
            } catch (DataRequestException e) {
               
            }

        } catch (DataRequestException e) {
            fail("Unexpected DataRequestException: " + e.getMessage());
        }
    }

    @Test
    public void testToString() {
        try {
            StandardCriterion standardCriterion = new StandardCriterion("and::vsname:field:eq:value");
            assertEquals("and  vsname field  equal value", standardCriterion.toString());

            standardCriterion = new StandardCriterion("or:not:vs:timed:geq:100");
            assertEquals("or not vs timed >= 01/01/1970 00:00:00 +0000", standardCriterion.toString());
        } catch (DataRequestException e) {
            fail("Unexpected DataRequestException: " + e.getMessage());
        }
    }

    @Test
    public void TestHashCodeEquals() {
        try {
            StandardCriterion standardCriterion = new StandardCriterion("and::vsname:field:eq:value");
            StandardCriterion standardCriterion2 = new StandardCriterion();
            assertFalse(standardCriterion.equals(standardCriterion2));
            standardCriterion2.setCritField("field1");
            standardCriterion2.setCritOperator("equal");
            standardCriterion2.setCritValue("value");
            standardCriterion2.setCritJoin("and");
            standardCriterion2.setCritNeg("");
            standardCriterion2.setCritVsname("vsname");
            assertFalse(standardCriterion.equals(standardCriterion2));
            assertFalse(standardCriterion2.equals(standardCriterion));
            assertTrue(standardCriterion.equals(standardCriterion));
            assertFalse(standardCriterion.equals(null));
            StandardCriterion standardCriterion3 = new StandardCriterion("and::vsname1:field:eq:value");
            assertFalse(standardCriterion.equals(standardCriterion3));
            StandardCriterion standardCriterion4 = new StandardCriterion("and::vsname:field:eq:value");
            assertTrue(standardCriterion.equals(standardCriterion4));
            assertNotNull(standardCriterion.hashCode());
            assertNotNull(standardCriterion2.hashCode());
        } catch (DataRequestException e) {
            fail("Unexpected DataRequestException: " + e.getMessage());
        }
    }
}
