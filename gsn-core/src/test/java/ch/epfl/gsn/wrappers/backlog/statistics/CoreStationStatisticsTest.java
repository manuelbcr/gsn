package ch.epfl.gsn.wrappers.backlog.statistics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class CoreStationStatisticsTest {
    
    private CoreStationStatistics coreStationStats;

    @Before
    public void setUp() {
        coreStationStats = new CoreStationStatistics("testCoreStation");
    }

    @Test
    public void testInitialGetters(){
        assertNull(coreStationStats.isConnected());
        assertNull(coreStationStats.getDeviceId());

        assertEquals(Long.valueOf(0),coreStationStats.getTotalMsgRecvByteCounter());
        assertNull(coreStationStats.getTotalSendByteCounter());
        assertNull(coreStationStats.getTotalRecvByteCounter());
        assertEquals(Long.valueOf(0),coreStationStats.getTotalMsgRecvCounter());
        assertEquals(Long.valueOf(0),coreStationStats.getTotalMsgSendCounter());
        assertEquals(Long.valueOf(0),coreStationStats.getTotalMsgSendByteCounter());
    }

    @Test
    public void testConnectionStatus() {
        coreStationStats.setConnected(true);
        assertTrue(coreStationStats.isConnected());
    }

    @Test
    public void testDeviceId() {
        coreStationStats.setDeviceId(1);
        assertEquals(Integer.valueOf(1), coreStationStats.getDeviceId());

        coreStationStats.msgReceived(1, 100);
        assertEquals(Long.valueOf(100),coreStationStats.getTotalMsgRecvByteCounter());
        coreStationStats.setDeviceId(2);
        assertEquals(Long.valueOf(0),coreStationStats.getTotalMsgRecvByteCounter());

    }

    @Test
    public void testMsgReceived() {
        coreStationStats.msgReceived(1, 100);
        assertEquals(Long.valueOf(1), coreStationStats.getMsgRecvCounter(1));
        assertEquals(Long.valueOf(100), coreStationStats.getMsgRecvByteCounter(1));
        coreStationStats.msgReceived(1, 200);
        coreStationStats.msgReceived(1, 300);
        assertEquals(Long.valueOf(3), coreStationStats.getMsgRecvCounter(1));
    }

    @Test
    public void testMsgSent() {
        coreStationStats.msgSent(1, 100);
        assertEquals(Long.valueOf(1), coreStationStats.getMsgSendCounter(1));
        assertEquals(Long.valueOf(100), coreStationStats.getMsgSendByteCounter(1));
    }

    @Test
    public void testBytesReceivedBytesSend() {
        assertNull(coreStationStats.getTotalSendByteCounter());
        assertNull(coreStationStats.getTotalRecvByteCounter());
        coreStationStats.bytesSent(100);
        coreStationStats.bytesReceived(100);
        assertNotNull(coreStationStats.getTotalSendByteCounter());
        assertNotNull(coreStationStats.getTotalRecvByteCounter());
    }
}
