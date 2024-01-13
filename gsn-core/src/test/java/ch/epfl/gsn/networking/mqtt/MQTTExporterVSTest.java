package ch.epfl.gsn.networking.mqtt;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
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
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.KeyValueImp;


public class MQTTExporterVSTest {
        private static StorageManager sm;
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
		sm = StorageManagerFactory.getInstance( "org.h2.Driver","sa","" ,"jdbc:h2:mem:test", Main.DEFAULT_MAX_DB_CONNECTIONS);

		Main.setDefaultGsnConf("/gsn_test.xml");
		Main.getInstance();
	}
    @Before
	public void setup() throws SQLException, IOException {
        DataField[] fields = new DataField[]{
            new DataField("field1", DataTypes.DOUBLE),
        };

        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("mqttexportervs");
        File someFile = File.createTempFile("mqttexportervs", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.BridgeVirtualSensor");
        testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);
       

        
        sm.executeCreateTable("mqttexportervs", fields, true);
    }
    @After
	public void teardown() throws SQLException {
		sm.executeDropTable("mqttexportervs");
	}

    @Test
    public void testInitialize(){
        MQTTExporterVS mqttexportervs = new MQTTExporterVS();
        mqttexportervs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(mqttexportervs.initialize());
    }
    @Test
    public void testInitialize1(){
        ArrayList < KeyValue > params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "uri" , "wronguri" ) );
        testVsensorConfig.setMainClassInitialParams( params );
        MQTTExporterVS mqttexportervs = new MQTTExporterVS();
        mqttexportervs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(mqttexportervs.initialize());
    }

    @Test
    public void testInitialize2(){
        ArrayList < KeyValue > params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "uri" , "tcp://broker.mqttdashboard.com:1883" ) );
        testVsensorConfig.setMainClassInitialParams( params );
        MQTTExporterVS mqttexportervs = new MQTTExporterVS();
        mqttexportervs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(mqttexportervs.initialize());
    
        StreamElement streamElement1 = new StreamElement(
            new String[]{"raw_packet"},
            new Byte[]{DataTypes.BINARY},
            new Serializable[]{new byte[] {1, 2, 3}},
            System.currentTimeMillis()+200);
        mqttexportervs.dataAvailable("input", streamElement1);
        mqttexportervs.dispose();

        
    }
}
