package ch.epfl.gsn.wrappers.backlog.plugins;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Serializable;
import java.sql.DriverManager;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.epfl.gsn.Main;
import ch.epfl.gsn.VirtualSensor;
import ch.epfl.gsn.beans.AddressBean;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.InputInfo;
import ch.epfl.gsn.beans.InputStream;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.StreamSource;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.KeyValueImp;
import ch.epfl.gsn.vsensor.BridgeVirtualSensor;
import ch.epfl.gsn.wrappers.BackLogWrapper;
import ch.epfl.gsn.wrappers.backlog.BackLogMessage;

public class SchedulePluginTest {

    private SchedulePlugin plugin;
    private BackLogWrapper backlogWrapper;

    private static StorageManager sm;
    private static StorageManager windowSm;

    private StreamSource ss;

    @BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		// Setup current working directory
        String currentWorkingDir = System.getProperty("user.dir");
		if (!currentWorkingDir.endsWith("/gsn-core/")) {
			String newDirectory = currentWorkingDir + "/gsn-core/";
        	System.setProperty("user.dir", newDirectory);
		}

		DriverManager.registerDriver( new org.h2.Driver( ) );
        
		Main.setDefaultGsnConf("/gsn_test.xml");
		Main.getInstance();
        sm = StorageManagerFactory.getInstance( "org.h2.Driver","sa","" ,"jdbc:h2:mem:test", Main.DEFAULT_MAX_DB_CONNECTIONS);
        windowSm = Main.getWindowStorage();
	}


    @Before
	public void setup() throws IOException{

        plugin = new SchedulePlugin();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        AddressBean ab = new AddressBean("schedulePlugin",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("schedulePlugin");

        // =============== BacklogWrapper =================
        backlogWrapper = new BackLogWrapper();

        ArrayList < KeyValueImp > predicatesBacklog = new ArrayList < KeyValueImp >( );
		predicatesBacklog.add( new KeyValueImp("remote-connection", "localhost:8080"));
        predicatesBacklog.add( new KeyValueImp("plugin-classname", "ch.epfl.gsn.wrappers.backlog.plugins.SchedulePlugin"));
        predicatesBacklog.add( new KeyValueImp("message-classname", "ch.epfl.gsn.wrappers.backlog.plugins.dpp.EventMsg"));

        AddressBean abBacklog = new AddressBean("backlogWrapper",predicatesBacklog.toArray(new KeyValueImp[] {}));
        abBacklog.setVirtualSensorName("backlogWrapper");

        backlogWrapper.setActiveAddressBean(abBacklog);

        assertTrue(backlogWrapper.initialize());
        backlogWrapper.getBLMessageMultiplexer().setDeviceID(123);
        assertEquals("BackLogWrapper", backlogWrapper.getWrapperName());
        assertEquals("localhost:8080", backlogWrapper.getRemoteConnectionPoint());
        // =============== BacklogWrapper =================
        
        InputStream is = new InputStream();
        is.setQuery("select * from mystream");

        DataField[] fields = { 
            new DataField("DEVICE_ID", "INTEGER"),
			new DataField("GENERATION_TIME", "BIGINT"),
			new DataField("TRANSMISSION_TIME", "BIGINT"),
			new DataField("GENERATED_BY", "VARCHAR(256)"),
			new DataField("SCHEDULE", "binary")
        };

        VSensorConfig config = new VSensorConfig();
		config.setName("testvs");
		config.setMainClass(new BridgeVirtualSensor().getClass().getName());
		config.setInputStreams(new InputStream[] { is });
		config.setStorageHistorySize("10");
		config.setOutputStructure(fields);
		config.setFileName("dummy-vs-file");
		assertTrue(config.validate());

		VirtualSensor pool = new VirtualSensor(config);
		is.setPool(pool);

        AddressBean[] addressing = new AddressBean[] {abBacklog};
		ss = new StreamSource().setAlias("backlogWrapper").setAddressing(addressing).setSqlQuery("select * from wrapper").setRawHistorySize("10m").setInputStream(is);
        is.setSources(new StreamSource[] { ss });

        Serializable[] exampleData = new Serializable[]{  
                123,  
                1706343087000L,  
                null,                
                "generated_by",              
                new byte[]{1, 2, 3},               
        };
    
        StreamElement streamElement1 = new StreamElement(
            new String[]{"DEVICE_ID", "GENERATION_TIME", "TRANSMISSION_TIME", "GENERATED_BY", "SCHEDULE"},
            new Byte[]{DataTypes.INTEGER, DataTypes.BIGINT, DataTypes.BIGINT, DataTypes.VARCHAR, DataTypes.BINARY },
            exampleData,
            System.currentTimeMillis());

        try{
            ss.setWrapper(backlogWrapper);
            sm.executeCreateTable(ss.getAlias(), fields,true);
            sm.executeInsert(ss.getAlias(), fields, streamElement1);
            sm.executeCreateTable(ss.getWrapper().getDBAliasInStr(), fields,true);
            sm.executeInsert(ss.getWrapper().getDBAliasInStr(), fields, streamElement1);
            windowSm.executeCreateTable(ss.getUIDStr(), fields,true);
            windowSm.executeInsert(ss.getUIDStr(), fields, streamElement1);
            windowSm.executeCreateTable(ss.getWrapper().getDBAliasInStr(), fields,true);
            windowSm.executeInsert(ss.getWrapper().getDBAliasInStr(), fields, streamElement1);
        }catch(Exception e){
            System.out.println("CANNOT CREATE TALBE" + e);
        }

        System.out.println("ÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖ");
        System.out.println("SS ALIAS" + ss.getWrapper().getDBAliasInStr());
        System.out.println("UID STRING " + ss.getUIDStr());
        

        assertTrue(plugin.initialize(backlogWrapper, "coreStationName", "deploymentName"));
        assertEquals("SchedulePlugin", plugin.getPluginName());

    }

    @After
    public void tearDown(){
        backlogWrapper.dispose();
        plugin.dispose();
        try{
            sm.executeDropTable(ss.getAlias());
            sm.executeDropTable(ss.getWrapper().getDBAliasInStr());
            windowSm.executeDropTable(ss.getUIDStr());
            windowSm.executeDropTable(ss.getWrapper().getDBAliasInStr());
        }catch(Exception e){
            System.out.println("cannot drop table" + e);
        }
    }

    @Test
    public void testMessageReceived() {
        
        int deviceId = 123;
        long timestamp = System.currentTimeMillis();

        Serializable[] data = new Serializable[]{(byte) 3 ,(short) 1, 1706343087000L, 123456L, "generated_by", new byte[]{1, 2, 3}};
        boolean result = plugin.messageReceived(deviceId, timestamp, data);
        assertTrue(result);
    }

    @Test
    public void testMessageReceivedSchedule() {

        DataField[] fields = { 
            new DataField("DEVICE_ID", "INTEGER"),
			new DataField("GENERATION_TIME", "BIGINT"),
			new DataField("TRANSMISSION_TIME", "BIGINT"),
			new DataField("GENERATED_BY", "VARCHAR(256)"),
			new DataField("SCHEDULE", "binary")
        };

        Serializable[] exampleData = new Serializable[]{  
            123,  
            1706343087000L,  
            1234567L,                
            "generated_by",              
            new byte[]{1, 2, 3},               
        };

        StreamElement streamElement = new StreamElement(
            new String[]{"DEVICE_ID", "GENERATION_TIME", "TRANSMISSION_TIME", "GENERATED_BY", "SCHEDULE"},
            new Byte[]{DataTypes.INTEGER, DataTypes.BIGINT, DataTypes.BIGINT, DataTypes.VARCHAR, DataTypes.BINARY },
            exampleData,
            System.currentTimeMillis());

        try{
            sm.executeInsert(ss.getAlias(), fields, streamElement);
        }catch(Exception e){
            System.out.println("CANNOT CREATE TALBE" + e);
        }
        
        int deviceId = 123;
        long timestamp = System.currentTimeMillis();

        Serializable[] data = new Serializable[]{(byte) 3 ,(short) 1, 1706343087000L, 123456L, "generated_by", new byte[]{1, 2, 3}};
        boolean result = plugin.messageReceived(deviceId, timestamp, data);
        assertTrue(result);
    }

    @Test
    public void testMessageReceivedTypeSchedule() {
        
        int deviceId = 123;
        long timestamp = System.currentTimeMillis();


        Serializable[] data = new Serializable[]{(byte) 2 ,"string", "string2"};
        boolean result = plugin.messageReceived(deviceId, timestamp, data);
        assertTrue(result);
    }

    @Test
    public void testSendToPluginNotSuccessfull() {

        String action = "schedule_command";
        String[] paramNames = { "core_station" };
        Object[] paramValues = { "123" };

        InputInfo result = plugin.sendToPlugin(action, paramNames, paramValues);
        assertTrue(result.toString().startsWith("The backlogwrapper deployment is not connected or does not exist"));
    }
}
