package ch.epfl.gsn.utils.protocols;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

public class AbstractHCIProtocolTest {

    @Test
    public void testAddQuery() {
        AbstractHCIProtocol protocol = new TestHCIProtocol("TestProtocol");
        AbstractHCIQuery query = new TestHCIQuery("TestQuery", "Description", new String[]{"param1"});
        
        protocol.addQuery(query);
        
        assertTrue(protocol.getQueries().contains(query));
        assertEquals("Description",query.getQueryDescription());
        assertEquals("param1",query.getParamsDescriptions()[0]);
    }

    @Test
    public void testGetQueries() {
        AbstractHCIProtocol protocol = new TestHCIProtocol("TestProtocol");
        AbstractHCIQuery query1 = new TestHCIQuery("Query1", "Description1", new String[]{"param1"});
        AbstractHCIQuery query2 = new TestHCIQuery("Query2", "Description2", new String[]{"param2"});
        
        protocol.addQuery(query1);
        protocol.addQuery(query2);
        
        assertEquals(2, protocol.getQueries().size());
    }

    @Test
    public void testGetQuery() {
        AbstractHCIProtocol protocol = new TestHCIProtocol("TestProtocol");
        AbstractHCIQuery query = new TestHCIQuery("TestQuery", "Description", new String[]{"param1"});
        
        protocol.addQuery(query);
        
        assertEquals(query, protocol.getQuery("TestQuery"));
    }

    @Test
    public void testGetNames() {
        AbstractHCIProtocol protocol = new TestHCIProtocol("TestProtocol");
        AbstractHCIQuery query = new TestHCIQuery("TestQuery", "Description", new String[]{"param1"});
        
        protocol.addQuery(query);
        
        Collection<String> names = protocol.getNames();
        assertTrue(names.contains("TestQuery"));
    }

    @Test
    public void testGetName() {
        AbstractHCIProtocol protocol = new TestHCIProtocol("TestProtocol");
        assertEquals("TestProtocol", protocol.getName());
    }

    @Test
    public void testBuildRawQuery() {
        AbstractHCIProtocol protocol = new TestHCIProtocol("TestProtocol");
        AbstractHCIQuery query = new TestHCIQuery("TestQuery", "Description", new String[]{"param1"});
        
        protocol.addQuery(query);
        
        Vector<Object> params = new Vector<>();
        params.add("value1");

        byte[] rawQuery = protocol.buildRawQuery("TestQuery", params);
        assertNotNull(rawQuery);
    }



    @Test
    public void testGetWaitTime() {
        AbstractHCIQueryWithoutAnswer query = new TestHCIQueryWithoutAnswer("TestQuery", "Description", new String[]{"param1"});
        Vector<Object> params = new Vector<>();

        int waitTime = query.getWaitTime(params);
        assertEquals(AbstractHCIQueryWithoutAnswer.NO_WAIT_TIME, waitTime);
    }

    @Test
    public void testNeedsAnswer() {
        AbstractHCIQueryWithoutAnswer query = new TestHCIQueryWithoutAnswer("TestQuery", "Description", new String[]{"param1"});
        Vector<Object> params = new Vector<>();

        boolean needsAnswer = query.needsAnswer(params);
        assertFalse(needsAnswer);
    }

    @Test
    public void testGetAnswers() {
        AbstractHCIQueryWithoutAnswer query = new TestHCIQueryWithoutAnswer("TestQuery", "Description", new String[]{"param1"});
        byte[] rawAnswer = new byte[]{1, 2, 3};

        Object[] answers = query.getAnswers(rawAnswer);
        assertNull(answers);
    }


    private static class TestHCIQueryWithoutAnswer extends AbstractHCIQueryWithoutAnswer {
        public TestHCIQueryWithoutAnswer(String name, String queryDescription, String[] paramsDescriptions) {
            super(name, queryDescription, paramsDescriptions);
        }
        @Override
        public byte[] buildRawQuery(Vector<Object> params) {
            return null;
        }
    }




}

class TestHCIProtocol extends AbstractHCIProtocol {
    public TestHCIProtocol(String name) {
        super(name);
    }
}

class TestHCIQuery extends AbstractHCIQuery {
    public TestHCIQuery(String name, String queryDescription, String[] paramsDescriptions) {
        super(name, queryDescription, paramsDescriptions);
    }

    @Override
    public byte[] buildRawQuery(Vector<Object> params) {
        if (params != null && !params.isEmpty()) {
                    
            return params.firstElement().toString().getBytes();
        }
        return null;
    }

    @Override
    public boolean needsAnswer(Vector<Object> params) {
        return false; 
    }

    @Override
    public int getWaitTime(Vector<Object> params) {
        return 0; 
    }

    @Override
    public Object[] getAnswers(byte[] rawAnswer) {
        return null; 
    }
}