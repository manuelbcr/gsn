package ch.epfl.gsn.vsensor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.collections.KeyValue;
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

public class BridgeVirtualSensorPermasenseTest {
    
    private static StorageManager sm;
    private VSensorConfig testVsensorConfig;

    @BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		// Setup current working directory
        String currentWorkingDir = System.getProperty("user.dir");
		if (!currentWorkingDir.endsWith("/gsn-core/")) {
			String newDirectory = currentWorkingDir + "/gsn-core/";
        	System.setProperty("user.dir", newDirectory);
		}

		DriverManager.registerDriver( new org.h2.Driver( ) );
		sm = StorageManagerFactory.getInstance( "org.h2.Driver","sa","" ,"jdbc:h2:mem:coreTest", Main.DEFAULT_MAX_DB_CONNECTIONS);

		Main.setDefaultGsnConf("/gsn_test.xml");
		Main.getInstance();
	}

    @Before
	public void setup() throws SQLException, IOException {
        DataField[] fields = new DataField[]{
            new DataField("name", DataTypes.VARCHAR),
            new DataField("value", DataTypes.DOUBLE)
        };
        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("testvsname");
		File someFile = File.createTempFile("testvsname", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.BridgeVirtualSensor");
		testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);
        KeyValue[] emptyAddressingArray = new KeyValue[0];
        testVsensorConfig.setAddressing(emptyAddressingArray);
        VirtualSensor pool = new VirtualSensor(testVsensorConfig);
        Mappings.addVSensorInstance(pool);

        
        sm.executeCreateTable("testvsname", fields, true);
    
        StreamElement streamElement1 = new StreamElement(
                new String[]{"name", "value"},
                new Byte[]{DataTypes.VARCHAR, DataTypes.DOUBLE},
                new Serializable[]{"xy", 45.5},
                System.currentTimeMillis());
    
        StreamElement streamElement2 = new StreamElement(
                new String[]{"name", "value"},
                new Byte[]{DataTypes.VARCHAR, DataTypes.DOUBLE},
                new Serializable[]{"xy", 45.4},
                System.currentTimeMillis()+1000);
        
        StreamElement streamElement3 = new StreamElement(
                new String[]{"name", "value"},
                new Byte[]{DataTypes.VARCHAR, DataTypes.DOUBLE},
                new Serializable[]{"xy", 45.4},
                System.currentTimeMillis()+2000);

        StreamElement streamElement4 = new StreamElement(
                new String[]{"name", "value"},
                new Byte[]{DataTypes.VARCHAR, DataTypes.DOUBLE},
                new Serializable[]{"xy", 45.4},
                System.currentTimeMillis()+2500); 

        sm.executeInsert("testvsname", fields, streamElement1);
        sm.executeInsert("testvsname", fields, streamElement2);
        sm.executeInsert("testvsname", fields, streamElement3);
        sm.executeInsert("testvsname", fields, streamElement4);
    }

	@After
	public void teardown() throws SQLException {
		sm.executeDropTable("testvsname");
	}


    @Test
    public void testInitialize() {
         StreamElement streamElement1 = new StreamElement(
            new String[]{"name", "value"},
            new Byte[]{DataTypes.VARCHAR, DataTypes.DOUBLE},
            new Serializable[]{"xy", 45.5},
            System.currentTimeMillis());

        BridgeVirtualSensorPermasense sensor = new BridgeVirtualSensorPermasense();
        sensor.setVirtualSensorConfiguration(testVsensorConfig);

        assertTrue(sensor.initialize());
        sensor.dataAvailable("inputStream", streamElement1);
    }


    @Test
    public void testDataAvailableAllfieldsNull(){
        StreamElement streamElement1 = new StreamElement(
            new String[]{"name", "value"},
            new Byte[]{DataTypes.VARCHAR, DataTypes.DOUBLE},
            new Serializable[]{"xy", 45.5},
            System.currentTimeMillis());

        BridgeVirtualSensor sensor = new BridgeVirtualSensor();
        sensor.setVirtualSensorConfiguration(testVsensorConfig);

        assertTrue(sensor.initialize());
        sensor.dataAvailable("inputStream", streamElement1);
        assertFalse(sensor.areAllFieldsNull(streamElement1));
    }
}
