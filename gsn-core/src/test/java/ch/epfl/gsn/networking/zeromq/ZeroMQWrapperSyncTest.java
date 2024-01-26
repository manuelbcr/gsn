package ch.epfl.gsn.networking.zeromq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.collections.KeyValue;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ch.epfl.gsn.Main;
import ch.epfl.gsn.beans.AddressBean;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.KeyValueImp;


public class ZeroMQWrapperSyncTest {
    private static StorageManager sm;
    private ZeroMQWrapperSync wrapper;
    private VSensorConfig testVsensorConfig;
    private ArrayList < KeyValue > params;

    @BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		// Setup current working directory
        String currentWorkingDir = System.getProperty("user.dir");
		if (!currentWorkingDir.endsWith("/gsn-core/")) {
			String newDirectory = currentWorkingDir + "/gsn-core/";
        	System.setProperty("user.dir", newDirectory);
		}

		DriverManager.registerDriver( new org.h2.Driver( ) );
		//sm = StorageManagerFactory.getInstance( "org.h2.Driver","sa","" ,"jdbc:h2:mem:coreTest", Main.DEFAULT_MAX_DB_CONNECTIONS);
        
		Main.setDefaultGsnConf("/gsn_test.xml");
		Main.getInstance();
        sm = StorageManagerFactory.getInstance( "org.h2.Driver","sa","" ,"jdbc:h2:mem:test", Main.DEFAULT_MAX_DB_CONNECTIONS);
	}

    @Before
	public void setup() throws SQLException, IOException {

        wrapper = new ZeroMQWrapperSync();
        
        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("address", "inproc://stream/127.0.0.1"));
        predicates.add( new KeyValueImp("local_address", "127.0.0.1"));
        predicates.add( new KeyValueImp("vsensor", "testvs"));
        predicates.add( new KeyValueImp("start-time", "1180000000000"));


                DataField[] fields = new DataField[]{
            new DataField("device_id", DataTypes.INTEGER),
            new DataField("generation_time", DataTypes.BIGINT),
            new DataField("position", DataTypes.INTEGER),
            new DataField("sensortype", DataTypes.VARCHAR),
            new DataField("sensortype_serialid",DataTypes.BIGINT)
        };
        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("testzeromq");
		File someFile = File.createTempFile("testzeromq", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.BridgeVirtualSensorPermasense");
		testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "position_mapping","true") );
        params.add( new KeyValueImp( "sensortype_mapping","true") );
        params.add( new KeyValueImp( "sensorvalue_conversion","true") );
        params.add( new KeyValueImp( "gps_time_conversion","true") );
        params.add( new KeyValueImp( "jpeg_scaled","JPEG_SCALED") );
        testVsensorConfig.setMainClassInitialParams( params );
        AddressBean ab = new AddressBean("testzeromq",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("testzeromq");
        ab.setVirtualSensorConfig(testVsensorConfig);

        wrapper.setActiveAddressBean(ab);
    }

    @After
	public void teardown() throws SQLException {
        wrapper.dispose();
        wrapper.stop();
	}

    @Test
    public void testInitialize(){
        assertTrue(wrapper.initialize());
        assertFalse(wrapper.isTimeStampUnique());
        assertEquals("ZeroMQ wrapper",wrapper.getWrapperName());
        System.out.println(wrapper.getOutputFormat());
    }
}