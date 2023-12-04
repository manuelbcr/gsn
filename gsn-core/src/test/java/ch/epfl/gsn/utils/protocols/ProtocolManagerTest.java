package ch.epfl.gsn.utils;

import ch.epfl.gsn.utils.protocols.ProtocolManager;
import ch.epfl.gsn.utils.protocols.AbstractHCIProtocol;
import ch.epfl.gsn.wrappers.AbstractWrapper;
import ch.epfl.gsn.utils.protocols.AbstractHCIQuery;
import javax.naming.OperationNotSupportedException;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.Vector;
import java.util.List;
import java.util.Collection;

public class ProtocolManagerTest {

    private ProtocolManager protocolManager;
    private AbstractHCIProtocol mockProtocol;
    private AbstractWrapper mockWrapper;

    @Before
    public void setUp() {
        mockProtocol = Mockito.mock(AbstractHCIProtocol.class);
        mockWrapper = Mockito.mock(AbstractWrapper.class);
        protocolManager = new ProtocolManager(mockProtocol, mockWrapper);
    }


    @Test
    public void testSendQueryInvalidQuery() throws OperationNotSupportedException {
        AbstractHCIQuery mockQuery = Mockito.mock(AbstractHCIQuery.class);
        Mockito.when(mockQuery.buildRawQuery(Mockito.any(Vector.class))).thenReturn(null);
        Mockito.when(mockProtocol.getQuery(Mockito.anyString())).thenReturn(mockQuery);

        byte[] result = protocolManager.sendQuery("InvalidQuery", new Vector<>());

        assertNull(result);
        assertEquals(ProtocolManager.ProtocolStates.READY, protocolManager.getCurrentState());
    }

    @Test
    public void testSendQueryNeedsAnswer() throws OperationNotSupportedException{
        AbstractHCIQuery mockQuery = Mockito.mock(AbstractHCIQuery.class);
        Mockito.when(mockQuery.buildRawQuery(Mockito.any(Vector.class))).thenReturn(new byte[]{1, 2, 3});
        Mockito.when(mockQuery.needsAnswer(Mockito.any(Vector.class))).thenReturn(true);
        Mockito.when(mockProtocol.getQuery(Mockito.anyString())).thenReturn(mockQuery);

        Mockito.when(mockWrapper.sendToWrapper(Mockito.isNull(), Mockito.isNull(), Mockito.any(Object[].class)))
               .thenReturn(true);

        byte[] result = protocolManager.sendQuery("TestQuery", new Vector<>());
        assertNotNull(result);
        assertEquals(ProtocolManager.ProtocolStates.WAITING, protocolManager.getCurrentState());

    }


    @Test
    public void testGetProtocolName() {
        AbstractHCIProtocol mockProtocol =Mockito.mock(AbstractHCIProtocol.class);
        Mockito.when(mockProtocol.getName()).thenReturn("MockProtocol");

        ProtocolManager protocolManager = new ProtocolManager(mockProtocol, null);

        String protocolName = protocolManager.getProtocolName();
        assertEquals("MockProtocol", protocolName);
    }

    @Test
    public void testGetQuery() {
        AbstractHCIProtocol mockProtocol = Mockito.mock(AbstractHCIProtocol.class);
        AbstractHCIQuery mockQuery = Mockito.mock(AbstractHCIQuery.class);


        Mockito.when(mockProtocol.getQuery("TestQuery")).thenReturn(mockQuery);
        ProtocolManager protocolManager = new ProtocolManager(mockProtocol, null);

        AbstractHCIQuery returnedQuery = protocolManager.getQuery("TestQuery");
        assertNotNull(returnedQuery);
        assertEquals(mockQuery, returnedQuery);
    }

    @Test
    public void testGetQueries() {
        AbstractHCIProtocol mockProtocol = Mockito.mock(AbstractHCIProtocol.class);
        AbstractHCIQuery mockQuery1 = Mockito.mock(AbstractHCIQuery.class);
        AbstractHCIQuery mockQuery2 = Mockito.mock(AbstractHCIQuery.class);

        Mockito.when(mockProtocol.getQueries()).thenReturn(List.of(mockQuery1, mockQuery2));

        ProtocolManager protocolManager = new ProtocolManager(mockProtocol, null);

        Collection<AbstractHCIQuery> queries = protocolManager.getQueries();
        assertNotNull(queries);
        assertEquals(2, queries.size());
        assertTrue(queries.contains(mockQuery1));
        assertTrue(queries.contains(mockQuery2));
    }


    @Test
    public void testGetters() {
        ProtocolManager protocolManager = new ProtocolManager(null, null);
        assertNull(protocolManager.getProtocolName());
        assertNull(protocolManager.getQuery("xy"));
        assertNull(protocolManager.getQueries());
    }
}
