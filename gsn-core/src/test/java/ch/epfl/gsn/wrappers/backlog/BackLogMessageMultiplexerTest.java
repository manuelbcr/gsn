package ch.epfl.gsn.wrappers.backlog;

import static org.junit.Assert.*;
import ch.epfl.gsn.Main;
import ch.epfl.gsn.VirtualSensor;
import ch.epfl.gsn.beans.AddressBean;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.InputStream;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.StreamSource;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.KeyValueImp;
import ch.epfl.gsn.vsensor.BridgeVirtualSensor;
import ch.epfl.gsn.wrappers.BackLogWrapper;
import ch.epfl.gsn.wrappers.backlog.plugins.DPPMessagePlugin;

import java.io.IOException;
import java.io.Serializable;
import java.sql.DriverManager;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class BackLogMessageMultiplexerTest {
    
    private DPPMessagePlugin plugin;
    private BackLogWrapper backlogWrapper;

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


		StreamSource ss = new StreamSource().setAlias("my-stream").setAddressing(addressing).setSqlQuery("select * from wrapper").setRawHistorySize("10m").setInputStream(is);
		assertTrue(ss.validate());
        is.setSources(new StreamSource[] { ss });
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
            System.out.println("CANNOT CREATE TALBE" + e);
        }

        assertTrue(plugin.initialize(backlogWrapper, "coreStationName", "deploymentName"));
        assertEquals("DPPMessagPlugin-ch.epfl.gsn.wrappers.backlog.plugins.dpp.EventMsg", plugin.getPluginName());
    }

    @After
    public void tearDown(){
        backlogWrapper.dispose();
        plugin.dispose();
    }

 
    @Test(expected = Exception.class)
    public void testSendMessageToNonExistenthost() throws Exception {
        short messageType = 1;
        long timestamp = System.currentTimeMillis();
        Serializable[] payload = {"test", 42, true};
        
        BackLogMessage message = new BackLogMessage(messageType, timestamp, payload);
        BackLogMessageMultiplexer multiplexer = BackLogMessageMultiplexer.getInstance("deployment", "127.0.0.1:8080");
        multiplexer.sendMessage(message, null, 1);
    }



    @Test
    public void testNewPluginMessage() throws Exception {
        BackLogMessageMultiplexer mockMultiplexer= Mockito.mock(BackLogMessageMultiplexer.class);
        PluginMessageHandler messageHandler= new PluginMessageHandler(mockMultiplexer, 10);
        BackLogMessage message = new BackLogMessage((short) 1);

        assertTrue(messageHandler.newPluginMessage(message));

        messageHandler.dispose();
    }

    @Test
    public void testIsMsgQueueReady() throws Exception {
        BackLogMessageMultiplexer mockMultiplexer= Mockito.mock(BackLogMessageMultiplexer.class);
        PluginMessageHandler messageHandler= new PluginMessageHandler(mockMultiplexer, 10);
        BackLogMessage message = new BackLogMessage((short) 1);

        messageHandler.newPluginMessage(message);

        assertTrue(messageHandler.isMsgQueueReady());
        assertFalse(messageHandler.isMsgQueueLimitReached());

        messageHandler.dispose();
    }

    @Test
    public void fullTest() throws Exception{
        try{
            BackLogMessageMultiplexer mmp= new BackLogMessageMultiplexer();
            fail("exception should be thrwon");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
        }

        try{
            BackLogMessageMultiplexer mmp= BackLogMessageMultiplexer.getInstance("testdepl", "xy");
            fail("exception should be thrwon");
        } catch (Exception e){
            assertTrue(e instanceof Exception);
        }
        BackLogMessageMultiplexer messageMultiplexer= BackLogMessageMultiplexer.getInstance("testdeployment", "127.0.0.1:5021");
        assertNotNull(messageMultiplexer);
        assertEquals("localhost",messageMultiplexer.getCoreStationName());
        assertNull(messageMultiplexer.getDeviceID());
        assertFalse(messageMultiplexer.isConnected());
        assertEquals(5021,messageMultiplexer.getPort());
        assertTrue(messageMultiplexer.getInetAddress().toString().contains("127.0.0.1"));
        byte[] input = new byte[] {1, 2, 3, 4, 5};
        int count = input.length;
        messageMultiplexer.processData(input, count);
        short messageType = 110;
        long timestamp = System.currentTimeMillis();
        Serializable[] payload = {"test", 42, true};
        
        try {
            BackLogMessage message = new BackLogMessage(messageType, timestamp, payload);
            
            messageMultiplexer.multiplexMessage(message);

        } catch (IOException | NullPointerException e) {
            fail("Exception thrown: " + e.getMessage());
        }

         


    }
    @Test
    public void backlogMessageMultiplexerTest(){
        BackLogMessageMultiplexer mp= backlogWrapper.getBLMessageMultiplexer();
        short messageType = 110;
        long timestamp = System.currentTimeMillis();
        Serializable[] payload = {"test", 42, true};

        try{
            BackLogMessage message = new BackLogMessage(messageType, timestamp, payload);
            mp.multiplexMessage(message);
        } catch (IOException | NullPointerException e) {
            fail("Exception thrown: " + e.getMessage());
        }
        try{
            BackLogMessage message1= new BackLogMessage(messageType, timestamp, payload);
            mp.sendMessage(message1, 1, 1);
        } catch (Exception e){
            assertTrue(e.getMessage().contains("The backlogwrapper deployment is not connected or does not exist"));
        }

        PingTimer timer= new PingTimer(mp);
        try{
            timer.run();
        } catch (Exception e){
            System.out.println("Exception thrown: " + e.getMessage());
        }

        PingWatchDog watchDog= new PingWatchDog(mp);
        try{
            watchDog.run();
        } catch (Exception e){
            System.out.println("Exception thrown: " + e.getMessage());
        }

        mp.connectionEstablished();
        mp.connectionLost();
       // mp.sendPing();
        //mp.sendQueueLimitMsg();
        //mp.sendQueueReadyMsg();
        try{
            backlogWrapper.start();
            Thread.sleep(1000);
            backlogWrapper.stop();
        }catch (Exception e){
            System.out.println("Exception thrown: " + e.getMessage());
        }
        
    }

    @Test
    public void testPluginMessageHandler() throws NullPointerException, IOException{
        PluginMessageHandler mh= new PluginMessageHandler(backlogWrapper.getBLMessageMultiplexer(), 2);
        assertTrue(mh.isMsgQueueReady());
        assertFalse(mh.isMsgQueueLimitReached());
        short messageType = 110;
        long timestamp = System.currentTimeMillis();
        Serializable[] payload = {"test", 42, true};
        BackLogMessage message = new BackLogMessage(messageType, timestamp, payload);
        mh.newPluginMessage(message);
        mh.newPluginMessage(message);
        assertFalse(mh.isMsgQueueLimitReached());
        mh.clearMsgQueue();
        assertFalse(mh.isMsgQueueLimitReached());
        /* 
        try{
            mh.run();
            Thread.sleep(10);
            mh.dispose();
        } catch (Exception e){
            fail("exception thrown");
        }*/
        PluginMessageHandler mh1= new PluginMessageHandler(backlogWrapper.getBLMessageMultiplexer(), BackLogMessageMultiplexer.PLUGIN_MESSAGE_QUEUE_WARN+1);
        for(int i=0; i<BackLogMessageMultiplexer.PLUGIN_MESSAGE_QUEUE_WARN+1; i++){
            mh1.newPluginMessage(message);
        }
        assertTrue(mh1.isMsgQueueLimitReached());
    }

}
