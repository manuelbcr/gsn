package ch.epfl.gsn.networking.mqtt;

import static org.junit.Assert.assertFalse;
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
import org.junit.Test;

import ch.epfl.gsn.Main;
import ch.epfl.gsn.beans.AddressBean;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.KeyValueImp;

public class MQTTWrapperTest {
    
    private static StorageManager sm;
    private MQTTWrapper wrapper;
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
        
		Main.setDefaultGsnConf("/gsn_test.xml");
		Main.getInstance();
        sm = StorageManagerFactory.getInstance( "org.h2.Driver","sa","" ,"jdbc:h2:mem:test", Main.DEFAULT_MAX_DB_CONNECTIONS);
	}


    @Before
	public void setup() throws SQLException, IOException {
        DataField[] fields = new DataField[]{
            new DataField("device_id", DataTypes.INTEGER),
            new DataField("generation_time", DataTypes.BIGINT),
            new DataField("position", DataTypes.INTEGER),
            new DataField("sensortype", DataTypes.VARCHAR),
            new DataField("sensortype_serialid",DataTypes.BIGINT)
        };
        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("testmqtt");
		File someFile = File.createTempFile("testmqtt", ".xml");
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
    }

    @Test
    public void testMQTTWrapper() {
        wrapper = new MQTTWrapper();
        
        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("uri", "tcp://broker.mqttdashboard.com:1883"));
        predicates.add( new KeyValueImp("client_id", "1"));
        predicates.add( new KeyValueImp("topic", "xy"));
        predicates.add( new KeyValueImp("qos", "2"));
        AddressBean ab = new AddressBean("testmqtt",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("testmqtt");
        ab.setVirtualSensorConfig(testVsensorConfig);

        wrapper.setActiveAddressBean(ab);
        assertTrue(wrapper.initialize());
        assertTrue(wrapper.getWrapperName().contains("MQTTWrapper"));
        wrapper.dispose();
    }

    @Test
    public void testMQTTWrapperWrongInitialize() {
        wrapper = new MQTTWrapper();
        
        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        AddressBean ab = new AddressBean("testmqtt",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("testmqtt");
        ab.setVirtualSensorConfig(testVsensorConfig);

        wrapper.setActiveAddressBean(ab);
        assertFalse(wrapper.initialize());
    }

    @Test
    public void testMQTTWrapperWrongInitialize2() {
        wrapper = new MQTTWrapper();
        
        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        predicates.add( new KeyValueImp("uri", "tcp://broker.mqttdashboard.com:1883"));
        AddressBean ab = new AddressBean("testmqtt",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("testmqtt");
        ab.setVirtualSensorConfig(testVsensorConfig);

        wrapper.setActiveAddressBean(ab);
        assertFalse(wrapper.initialize());
    }
    @Test
    public void testMQTTWrapperWrongInitialize3() {
        wrapper = new MQTTWrapper();
        
        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        predicates.add( new KeyValueImp("uri", "tcp://broker.mqttdashboard.com:1883"));
        predicates.add( new KeyValueImp("client_id", "1"));
        AddressBean ab = new AddressBean("testmqtt",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("testmqtt");
        ab.setVirtualSensorConfig(testVsensorConfig);

        wrapper.setActiveAddressBean(ab);
        assertFalse(wrapper.initialize());
    }

    @Test
    public void testMQTTWrapperWrongInitialize4() {
        wrapper = new MQTTWrapper();
        
        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        predicates.add( new KeyValueImp("uri", "tcp://broker.mqttdashboard.com:1883"));
        predicates.add( new KeyValueImp("client_id", "1"));
        predicates.add( new KeyValueImp("topic", "xy"));
        predicates.add( new KeyValueImp("qos", "5"));
        AddressBean ab = new AddressBean("testmqtt",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("testmqtt");
        ab.setVirtualSensorConfig(testVsensorConfig);

        wrapper.setActiveAddressBean(ab);
        assertFalse(wrapper.initialize());
        
    }
}
