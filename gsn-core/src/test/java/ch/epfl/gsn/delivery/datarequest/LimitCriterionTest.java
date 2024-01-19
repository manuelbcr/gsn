package ch.epfl.gsn.delivery.datarequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

public class LimitCriterionTest {
    
    @Test
    public void testLimitCriterionCreation() {
        try {

            LimitCriterion limitCriterion = new LimitCriterion("10:50");
            assertEquals(10, limitCriterion.getOffset().intValue());
            assertEquals(50, limitCriterion.getSize().intValue());

            LimitCriterion limitCriterion2 = new LimitCriterion();
            assertNotNull(limitCriterion2);
            limitCriterion2.setOffset(10);
            limitCriterion2.setSize(200);
            assertNotNull(limitCriterion2.getSize());
            assertNotNull(limitCriterion2.getOffset());


            try {
                new LimitCriterion("1050");
                fail("Expected DataRequestException");
            } catch (DataRequestException e) {

            }


            try {
                new LimitCriterion("10:50:extra");
                fail("Expected DataRequestException");
            } catch (DataRequestException e) {
   
            }

            try {
                new LimitCriterion("invalid:50");
                fail("Expected NumberFormatException");
            } catch (NumberFormatException e) {
              
            }

        } catch (DataRequestException e) {
            fail("Unexpected DataRequestException: " + e.getMessage());
        }
    }

    @Test
    public void testToString() {
        try {
     
            LimitCriterion limitCriterion = new LimitCriterion("10:50");
            assertEquals("size: 50 offset: 10", limitCriterion.toString());


            limitCriterion = new LimitCriterion("5:100");
            assertEquals("size: 100 offset: 5", limitCriterion.toString());
        } catch (DataRequestException e) {
            fail("Unexpected DataRequestException: " + e.getMessage());
        }
    }
}
