package ch.epfl.gsn.delivery.datarequest;

import static org.junit.Assert.*;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class QueriesBuilderTest {

    @Test
    public void testConstructorWithValidParameters() {
        Map<String, String[]> validParameters = new HashMap<>();
        validParameters.put(QueriesBuilder.PARAM_VSNAMES_AND_FIELDS, new String[]{"vs1:field1:field2", "vs2:field3"});
        validParameters.put(QueriesBuilder.PARAM_STANDARD_CRITERIA, new String[]{"and::vs1:field1:ge:10"});
        validParameters.put(QueriesBuilder.PARAM_MAX_NB, new String[]{"1:10"});
        validParameters.put(QueriesBuilder.PARAM_TIME_FORMAT, new String[]{"iso"});
        try {
            QueriesBuilder queriesBuilder = new QueriesBuilder(validParameters);
            assertNotNull(queriesBuilder);
            assertNull(queriesBuilder.getAggregationCriterion());
            assertNotNull(queriesBuilder.getStandardCriteria());
            assertNotNull(queriesBuilder.getVsnamesAndStreams());
            assertNotNull(queriesBuilder.getLimitCriterion());
            assertNotNull(queriesBuilder.getStandardCriteria());
            assertNotNull(queriesBuilder.getSdf());
        } catch (DataRequestException e) {
            fail("Failed to create QueriesBuilder with valid parameters: " + e.getMessage());
        }
    }

    @Test(expected = DataRequestException.class)
    public void testConstructorWithMissingParameters() throws DataRequestException {
        Map<String, String[]> invalidParameters = new HashMap<>();
        new QueriesBuilder(invalidParameters);
    }

    @Test
    public void testGetSqlQueries() {
        Map<String, String[]> validParameters = new HashMap<>();
        validParameters.put(QueriesBuilder.PARAM_VSNAMES_AND_FIELDS, new String[]{"vs1:field1:field2", "vs2:field3"});

        try {
            QueriesBuilder queriesBuilder = new QueriesBuilder(validParameters);
            assertNotNull(queriesBuilder.getSqlQueries());
        } catch (DataRequestException e) {
            fail("Failed to create QueriesBuilder with valid parameters: " + e.getMessage());
        }
    }

    @Test
    public void testAbstractDataRequest() throws DataRequestException {
        Map<String, String[]> validParameters = new HashMap<>();
        validParameters.put(QueriesBuilder.PARAM_VSNAMES_AND_FIELDS, new String[]{"vs1:field1:field2", "vs2:field3"});

        AbstractDataRequestTestImpl dataRequest = new AbstractDataRequestTestImpl(validParameters);
        assertNotNull(dataRequest.getQueryBuilder());
        dataRequest.process();
    }

    
    private static class AbstractDataRequestTestImpl extends AbstractDataRequest {

        public AbstractDataRequestTestImpl(Map<String, String[]> requestParameters) throws DataRequestException {
            super(requestParameters);
        }

        @Override
        public void process() throws DataRequestException {}

        @Override
        public void outputResult(OutputStream os) {}
    }
}
