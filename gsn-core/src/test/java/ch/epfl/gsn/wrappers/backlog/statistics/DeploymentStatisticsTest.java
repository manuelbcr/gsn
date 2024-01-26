package ch.epfl.gsn.wrappers.backlog.statistics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.epfl.gsn.Main;
import ch.epfl.gsn.Mappings;
import ch.epfl.gsn.VirtualSensor;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.wrappers.BackLogStatsWrapper;
import thredds.inventory.bdb.MetadataManager.KeyValue;

public class DeploymentStatisticsTest {

    private DeploymentStatistics deploymentStats;

    private static StorageManager sm;
    private BackLogStatsWrapper statsWrapper;

    @BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		// Setup current working directory
        String currentWorkingDir = System.getProperty("user.dir");
		if (!currentWorkingDir.endsWith("/gsn-core/")) {
			String newDirectory = currentWorkingDir + "/gsn-core/";
        	System.setProperty("user.dir", newDirectory);
		}

		DriverManager.registerDriver( new org.h2.Driver( ) );
		sm = StorageManagerFactory.getInstance( "org.h2.Driver","sa","" ,"jdbc:h2:mem:test", Main.DEFAULT_MAX_DB_CONNECTIONS);

		Main.setDefaultGsnConf("/gsn_test.xml");
		Main.getInstance();
	}

    @Before
	public void setup() throws SQLException, IOException {
        DataField[] fields = new DataField[]{
            new DataField("name", DataTypes.VARCHAR),
            new DataField("value", DataTypes.DOUBLE)
        };
        
        sm.executeCreateTable("testvsname", fields, true);
    
        StreamElement streamElement1 = new StreamElement(
                new String[]{"name", "value"},
                new Byte[]{DataTypes.VARCHAR, DataTypes.DOUBLE},
                new Serializable[]{"xy", 45.5},
                System.currentTimeMillis()+10000);
    
        StreamElement streamElement2 = new StreamElement(
                new String[]{"name", "value"},
                new Byte[]{DataTypes.VARCHAR, DataTypes.DOUBLE},
                new Serializable[]{"xy", 45.4},
                System.currentTimeMillis()+12000);
        
        StreamElement streamElement3 = new StreamElement(
                new String[]{"name", "value"},
                new Byte[]{DataTypes.VARCHAR, DataTypes.DOUBLE},
                new Serializable[]{"xy", 45.4},
                System.currentTimeMillis()+20000);

        StreamElement streamElement4 = new StreamElement(
                new String[]{"name", "value"},
                new Byte[]{DataTypes.VARCHAR, DataTypes.DOUBLE},
                new Serializable[]{"xy", 45.4},
                System.currentTimeMillis()+25000); 

        sm.executeInsert("testvsname", fields, streamElement1);
        sm.executeInsert("testvsname", fields, streamElement2);
        sm.executeInsert("testvsname", fields, streamElement3);
        sm.executeInsert("testvsname", fields, streamElement4);
    
        statsWrapper = new BackLogStatsWrapper();
        deploymentStats = new DeploymentStatistics(statsWrapper);
    }

	@After
	public void teardown() throws SQLException {
		sm.executeDropTable("testvsname");
	}

    @Test
    public void testInitialization() throws IOException {
        assertNotNull(deploymentStats.getStatsWrapper());
        assertNull(deploymentStats.isConnectedList());
        assertNull(deploymentStats.getTotalMsgRecvCounter());
        assertNull(deploymentStats.getMsgRecvCounterList(1));
        assertNull(deploymentStats.getTotalRecvByteCounter());
        assertNull(deploymentStats.getTotalMsgRecvByteCounter());
        assertNull(deploymentStats.getMsgRecvByteCounterList(1));
        assertNull(deploymentStats.getTotalMsgSendCounter());
        assertNull(deploymentStats.getMsgSendCounterList(1));
        assertNull(deploymentStats.getTotalSendByteCounter());
        assertNull(deploymentStats.getTotalMsgSendByteCounter());
        assertNull(deploymentStats.getMsgSendByteCounterList(1));
    }

    @Test
    public void testIsConnectedList() throws IOException {
        CoreStationStatistics coreStationStats = deploymentStats.newStatisticsClass("CoreStation1");
        coreStationStats.setConnected(true);
        coreStationStats.setDeviceId(1);

        Map<Integer, Boolean> isConnectedList = deploymentStats.isConnectedList();
        assertTrue(isConnectedList.containsKey(1));
        assertTrue(isConnectedList.get(1));
    }

    @Test
    public void testGetTotalMsgRecvCounter() throws IOException {
        CoreStationStatistics coreStationStats = deploymentStats.newStatisticsClass("CoreStation1");
        coreStationStats.setDeviceId(1);
        coreStationStats.msgReceived(1, 100);

        Map<Integer, Long> totalMsgRecvCounter = deploymentStats.getTotalMsgRecvCounter();
        assertTrue(totalMsgRecvCounter.containsKey(1));
        assertEquals(Long.valueOf(1), totalMsgRecvCounter.get(1));
    }

    @Test
    public void testGetMsgRecvCounterList() throws IOException {
        CoreStationStatistics coreStationStats = deploymentStats.newStatisticsClass("CoreStation1");
        coreStationStats.setDeviceId(1);
        coreStationStats.msgReceived(1, 100);

        Map<Integer, Long> msgRecvCounterList = deploymentStats.getMsgRecvCounterList(1);
        assertTrue(msgRecvCounterList.containsKey(1));
        assertEquals(Long.valueOf(1), msgRecvCounterList.get(1));
    }

    @Test
    public void testGetTotalRecvByteCounter() throws IOException {
        CoreStationStatistics coreStationStats = deploymentStats.newStatisticsClass("CoreStation1");
        coreStationStats.setDeviceId(1);
        coreStationStats.bytesReceived(100);

        Map<Integer, Long> totalRecvByteCounter = deploymentStats.getTotalRecvByteCounter();
        assertTrue(totalRecvByteCounter.containsKey(1));
        assertEquals(Long.valueOf(100), totalRecvByteCounter.get(1));
    }

    @Test
    public void testGetTotalMsgRecvByteCounter() throws IOException {
        CoreStationStatistics coreStationStats = deploymentStats.newStatisticsClass("CoreStation1");
        coreStationStats.setDeviceId(1);
        coreStationStats.bytesReceived(100);

        Map<Integer, Long> totalMsgRecvByteCounter = deploymentStats.getTotalMsgRecvByteCounter();
        assertTrue(totalMsgRecvByteCounter.containsKey(1));
        assertEquals(Long.valueOf(0), totalMsgRecvByteCounter.get(1));
    }

    @Test
    public void testGetMsgRecvByteCounterList() throws IOException {
        CoreStationStatistics coreStationStats = deploymentStats.newStatisticsClass("CoreStation1");
        coreStationStats.setDeviceId(1);
        coreStationStats.msgReceived(1, 100);

        Map<Integer, Long> msgRecvByteCounterList = deploymentStats.getMsgRecvByteCounterList(1);
        assertTrue(msgRecvByteCounterList.containsKey(1));
        assertEquals(Long.valueOf(100), msgRecvByteCounterList.get(1));
    }

    @Test
    public void testGetTotalMsgSendCounter() throws IOException {
        CoreStationStatistics coreStationStats = deploymentStats.newStatisticsClass("CoreStation1");
        coreStationStats.setDeviceId(1);
        coreStationStats.msgSent(1, 50);

        Map<Integer, Long> totalMsgSendCounter = deploymentStats.getTotalMsgSendCounter();
        assertTrue(totalMsgSendCounter.containsKey(1));
        assertEquals(Long.valueOf(1), totalMsgSendCounter.get(1));
    }

    @Test
    public void testGetMsgSendCounterList() throws IOException {
        CoreStationStatistics coreStationStats = deploymentStats.newStatisticsClass("CoreStation1");
        coreStationStats.setDeviceId(1);
        coreStationStats.msgSent(1, 50);

        Map<Integer, Long> msgSendCounterList = deploymentStats.getMsgSendCounterList(1);
        assertTrue(msgSendCounterList.containsKey(1));
        assertEquals(Long.valueOf(1), msgSendCounterList.get(1));
    }

    @Test
    public void testGetTotalSendByteCounter() throws IOException {
        CoreStationStatistics coreStationStats = deploymentStats.newStatisticsClass("CoreStation1");
        coreStationStats.setDeviceId(1);
        coreStationStats.bytesSent(100);

        Map<Integer, Long> totalSendByteCounter = deploymentStats.getTotalSendByteCounter();
        assertTrue(totalSendByteCounter.containsKey(1));
        assertEquals(Long.valueOf(100), totalSendByteCounter.get(1));
    }

    @Test
    public void testGetTotalMsgSendByteCounter() throws IOException {
        CoreStationStatistics coreStationStats = deploymentStats.newStatisticsClass("CoreStation1");
        coreStationStats.setDeviceId(1);
        coreStationStats.msgSent(1, 50);

        Map<Integer, Long> totalMsgSendByteCounter = deploymentStats.getTotalMsgSendByteCounter();
        assertTrue(totalMsgSendByteCounter.containsKey(1));
        assertEquals(Long.valueOf(50), totalMsgSendByteCounter.get(1));
    }

    @Test
    public void testGetMsgSendByteCounterList() throws IOException {
        CoreStationStatistics coreStationStats = deploymentStats.newStatisticsClass("CoreStation1");
        coreStationStats.setDeviceId(1);
        coreStationStats.msgSent(1, 50);

        Map<Integer, Long> msgSendByteCounterList = deploymentStats.getMsgSendByteCounterList(1);
        assertTrue(msgSendByteCounterList.containsKey(1));
        assertEquals(Long.valueOf(50), msgSendByteCounterList.get(1));

        
        BackLogStatsWrapper statsWrapper1 = new BackLogStatsWrapper();
        deploymentStats.setStatsWrapper(statsWrapper1);
        assertNotNull(deploymentStats.getStatsWrapper());
        deploymentStats.removeCoreStationStatsInstance("CoreStation1");
    }


}
