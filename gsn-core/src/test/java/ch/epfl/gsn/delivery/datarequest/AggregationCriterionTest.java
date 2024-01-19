package ch.epfl.gsn.delivery.datarequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class AggregationCriterionTest {
    
    @Test
    public void testAggregationCriterionCreation() {
        try {

            AggregationCriterion aggregationCriterion = new AggregationCriterion("100:max");
            assertEquals("100", aggregationCriterion.getTimeRange());
            assertEquals("max", aggregationCriterion.getGroupOperator());


            try {
                new AggregationCriterion("100max");
                fail("Expected DataRequestException");
            } catch (DataRequestException e) {
                
            }

            try {
                new AggregationCriterion("100:max:extra");
                fail("Expected DataRequestException");
            } catch (DataRequestException e) {
               
            }

            try {
                new AggregationCriterion("100:unknown");
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

            AggregationCriterion aggregationCriterion = new AggregationCriterion("100:avg");
            assertEquals("Select: AVG, group by: timed/100 (" + "100 ms" + ")", aggregationCriterion.toString());

            aggregationCriterion = new AggregationCriterion("200:min");
            assertEquals("Select: MIN, group by: timed/200 (" + "200 ms" + ")", aggregationCriterion.toString());
        } catch (DataRequestException e) {
            fail("Unexpected DataRequestException: " + e.getMessage());
        }
    }
}
