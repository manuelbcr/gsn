package ch.epfl.gsn.wrappers.backlog.plugins;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Serializable;
import java.sql.DriverManager;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
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
import ch.epfl.gsn.wrappers.DataMappingWrapper;
import ch.epfl.gsn.wrappers.backlog.BackLogMessage;
import ch.epfl.gsn.wrappers.backlog.BackLogMessageMultiplexer;
import ch.epfl.gsn.wrappers.general.RemoteRestAPIWrapper;
import ch.epfl.gsn.wrappers.general.RemoteRestAPIWrapperTest.HttpRequestWithSpecificURLMatcher;

import org.mockito.Mockito;

public class DPPMessagePluginTest {

    private DPPMessagePlugin plugin;
    private BackLogWrapper backlogWrapper;
    private StreamSource ss;

    private static StorageManager sm;
    private static StorageManager windowSm;

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

        plugin = new DPPMessagePlugin();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("priority", "1"));
        
        AddressBean ab = new AddressBean("messagePlugin",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("messagePlugin");

        // =============== BacklogWrapper =================
        backlogWrapper = new BackLogWrapper();

        ArrayList < KeyValueImp > predicatesBacklog = new ArrayList < KeyValueImp >( );
		predicatesBacklog.add( new KeyValueImp("remote-connection", "localhost:8080"));
        predicatesBacklog.add( new KeyValueImp("plugin-classname", "ch.epfl.gsn.wrappers.backlog.plugins.DPPMessagePlugin"));
        predicatesBacklog.add( new KeyValueImp("message-classname", "ch.epfl.gsn.wrappers.backlog.plugins.dpp.EventMsg"));

        AddressBean abBacklog = new AddressBean("backlogWrapper",predicatesBacklog.toArray(new KeyValueImp[] {}));
        abBacklog.setVirtualSensorName("backlogWrapper");

        backlogWrapper.setActiveAddressBean(abBacklog);

        assertTrue(backlogWrapper.initialize());
        backlogWrapper.getBLMessageMultiplexer().setDeviceID(123);
        assertEquals("BackLogWrapper", backlogWrapper.getWrapperName());
        assertEquals("localhost:8080", backlogWrapper.getRemoteConnectionPoint());
        // =============== BacklogWrapper =================

        AddressBean[] addressing = new AddressBean[] {abBacklog};
        InputStream is = new InputStream();
        is.setQuery("select * from mystream");

        VSensorConfig config = new VSensorConfig();
		config.setName("testvs");
		config.setMainClass(new BridgeVirtualSensor().getClass().getName());
		config.setInputStreams(new InputStream[] { is });
		config.setStorageHistorySize("10");
		config.setOutputStructure(new DataField[] {});
		config.setFileName("dummy-vs-file");
		assertTrue(config.validate());

		VirtualSensor pool = new VirtualSensor(config);
		is.setPool(pool);


		ss = new StreamSource().setAlias("my-stream").setAddressing(addressing).setSqlQuery("select * from wrapper").setRawHistorySize("10m").setInputStream(is);
		assertTrue(ss.validate());
        is.setSources(new StreamSource[] { ss });

        System.out.println("ALIAS CODE" + backlogWrapper.getDBAlias());

        DataField[] fields = new DataField[]{
            new DataField("TIMESTAMP", DataTypes.BIGINT),
            new DataField("GENERATION_TIME", DataTypes.BIGINT),
            new DataField("GENERATION_TIME_MICROSEC", DataTypes.BIGINT),
            new DataField("DEVICE_ID", DataTypes.INTEGER),
            new DataField("MESSAGE_TYPE", DataTypes.INTEGER),
            new DataField("TARGET_ID", DataTypes.INTEGER),
            new DataField("SEQNR", DataTypes.INTEGER),
            new DataField("PAYLOAD_LENGTH", DataTypes.INTEGER),
            new DataField("COMPONENT_ID", DataTypes.SMALLINT),
            new DataField("TYPE", DataTypes.SMALLINT),
            new DataField("VALUE", DataTypes.BIGINT),
            new DataField("TIMED", DataTypes.BIGINT)
        };
    
    
        Serializable[] exampleData = new Serializable[]{
                1706271090000L,  // timestamp
                1706271090L,      // generation_time
                1234567890123L,   // generation_time_microsec
                4,                // device_id
                1,                // message_type
                10,               // target_id
                42,               // seqnr
                1024,             // payload_length
                (short) 1,        // component_id
                (short) 2,        // type
                9876543210L,      // value
                System.currentTimeMillis()  // timed
        };
    
        StreamElement streamElement1 = new StreamElement(
            new String[]{"TIMESTAMP", "GENERATION_TIME", "GENERATION_TIME_MICROSEC", "DEVICE_ID", "MESSAGE_TYPE", "TARGET_ID", "SEQNR", "PAYLOAD_LENGTH", "COMPONENT_ID", "TYPE", "VALUE", "TIMED"},
            new Byte[]{DataTypes.BIGINT, DataTypes.BIGINT, DataTypes.BIGINT, DataTypes.INTEGER, DataTypes.INTEGER, DataTypes.INTEGER, DataTypes.INTEGER, DataTypes.INTEGER, DataTypes.SMALLINT, DataTypes.SMALLINT, DataTypes.BIGINT, DataTypes.BIGINT},
            exampleData,
            System.currentTimeMillis());

        try{
            ss.setWrapper(backlogWrapper);
            sm.executeCreateTable(ss.getWrapper().getDBAliasInStr(), fields,true);
            sm.executeInsert(ss.getWrapper().getDBAliasInStr(), fields, streamElement1);
            windowSm.executeCreateTable(ss.getUIDStr(), fields,true);
            windowSm.executeInsert(ss.getUIDStr(), fields, streamElement1);
        }catch(Exception e){
            System.out.println("CANNOT CREATE TABLE" + e);
        }

        assertTrue(plugin.initialize(backlogWrapper, "coreStationName", "deploymentName"));
        assertEquals("DPPMessagPlugin-ch.epfl.gsn.wrappers.backlog.plugins.dpp.EventMsg", plugin.getPluginName());
    }

    @After
    public void tearDown(){
        backlogWrapper.dispose();
        plugin.dispose();
        try{
            sm.executeDropTable(ss.getWrapper().getDBAliasInStr());
            windowSm.executeDropTable(ss.getUIDStr());
        }catch(Exception e){
            System.out.println("CANNOT DELETE TABLE" + e);
        }
    }

    @Test
    public void testMessageReceived() {
        
        int deviceId = 123;
        long timestamp = System.currentTimeMillis();

        Serializable[] data = new Serializable[]{(short) 1, false, (short) 2, 10, 456, 789, System.currentTimeMillis()-100, new byte[]{1, 2, 3}};
        boolean result = plugin.messageReceived(deviceId, timestamp, data);
        assertTrue(result);
    }


    @Test
    public void testSendToPluginNotSuccessfull() {

        String action = "testAction";
        String[] paramNames = new String[] { "target_id", "param2" };
        Object[] paramValues = new Object[] { "1", "value2" };

        InputInfo result = plugin.sendToPlugin(action, paramNames, paramValues);
        assertTrue(result.toString().startsWith("DPP message upload not successfull"));
    }

    @Test
    public void testBacklogMultiplexerReceive() throws IOException {

        DPPMessageMultiplexer blMux = plugin.getDPPMessageMultiplexer();

        short messageType = 1;
        long timestamp = System.currentTimeMillis();
        
        Serializable[] payload = {"test", 42, 2, true};
        BackLogMessage message = new BackLogMessage(messageType, timestamp, payload);
           
        blMux.messageRecv(123, message);
    }
    
}
