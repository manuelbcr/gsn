package ch.epfl.gsn.delivery.datarequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;

import org.junit.Test;

public class AbstractQueryTest {
    
    @Test
    public void testGetStandardQueryWithoutCriteriaAndAggregation() {
        AbstractQuery query = new AbstractQuery(null, null, "vsName", new String[]{"field1", "field2"}, null);
        String expectedQuery = "select field1, field2 from vsName order by timed desc";
        assertEquals(expectedQuery, query.getStandardQuery().toString().trim());
    }

    @Test
    public void testGetStandardQueryWithCriteriaAndAggregation() throws DataRequestException {
        StandardCriterion standardCriterion = new StandardCriterion("and::vsname:field:eq:value");
        StandardCriterion standardCriterion1 = new StandardCriterion("and::vsname:field:eq:value");
        LimitCriterion limitCriterion = new LimitCriterion("10:50");
        AggregationCriterion aggregationCriterion = new AggregationCriterion("10:max");
        ArrayList<StandardCriterion> criteria = new ArrayList<>();
        criteria.add(standardCriterion);
        AbstractQuery query = new AbstractQuery(limitCriterion, aggregationCriterion, "vsName", new String[]{"field1", "field2"}, criteria);
        String expected= "select max(field1) as field1, max(field2) as field2, floor(timed/10) as aggregation_interval from vsName where  field  equal value group by aggregation_interval desc";
        assertEquals(expected, query.getStandardQuery().toString().trim());


        assertEquals(limitCriterion, query.getLimitCriterion());
        assertEquals(aggregationCriterion, query.getAggregation());
        assertEquals("vsName",query.getVsName());
        assertEquals(criteria,query.getCriteria());

        query.updateCriterion(standardCriterion1);
        query.setCriteria(criteria);
        assertEquals(criteria, query.getCriteria());
        query.setAggregation(new AggregationCriterion("11:max"));
        assertNotEquals(aggregationCriterion, query.getAggregation());
        query.setLimitCriterion(new LimitCriterion("5:10"));
        assertNotEquals(limitCriterion, query.getLimitCriterion());
        query.setVsName("vsName1");
        assertEquals("vsName1", query.getVsName());
        query.setFields(new String[]{"field11", "field12"});
        assertEquals("field11", query.getFields()[0]);
        query.addField("field3");
        assertEquals("field3", query.getFields()[0]);
        
    }
}
