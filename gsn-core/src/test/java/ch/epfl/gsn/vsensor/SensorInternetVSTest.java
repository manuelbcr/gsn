package ch.epfl.gsn.vsensor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
import org.junit.Test;

import ch.epfl.gsn.Main;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.KeyValueImp;

public class SensorInternetVSTest {

    private static StorageManager sm;
    private VSensorConfig testVsensorConfig;
    ArrayList < KeyValue > params;

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
            new DataField("value", DataTypes.INTEGER),
            new DataField("value1", DataTypes.INTEGER)
        };

        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("testvs");
        File someFile = File.createTempFile("testvs", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.ChartVirtualSensor");
        testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp("si-url" , "http://localhost:22001" ) );
        params.add( new KeyValueImp("si-username", "test" ) );
        params.add( new KeyValueImp("si-password" ,"passwd" ) );
        params.add( new KeyValueImp("si-stream-mapping", "1,23") );
        testVsensorConfig.setMainClassInitialParams( params );

        
        sm.executeCreateTable("testvs", fields, true);
    
        StreamElement streamElement1 = new StreamElement(
                new String[]{"value", "value1"},
                new Byte[]{DataTypes.INTEGER, DataTypes.INTEGER},
                new Serializable[]{1,2},
                System.currentTimeMillis()+200);
    
        StreamElement streamElement2 = new StreamElement(
                new String[]{"value", "value1"},
                new Byte[]{DataTypes.INTEGER, DataTypes.INTEGER},
                new Serializable[]{2,3},
                System.currentTimeMillis()+400);
        
        StreamElement streamElement3 = new StreamElement(
                new String[]{"value", "value1"},
                new Byte[]{DataTypes.INTEGER, DataTypes.INTEGER},
                new Serializable[]{3,4},
                System.currentTimeMillis()+500);

        StreamElement streamElement4 = new StreamElement(
                new String[]{"value", "value1"},
                new Byte[]{DataTypes.INTEGER, DataTypes.INTEGER},
                new Serializable[]{4,9},
                System.currentTimeMillis()+600);

        sm.executeInsert("testvs", fields, streamElement1);
        sm.executeInsert("testvs", fields, streamElement2);
        sm.executeInsert("testvs", fields, streamElement3);
        sm.executeInsert("testvs", fields, streamElement4);
    }

    @After
	public void teardown() throws SQLException {
		sm.executeDropTable("testvs");
	}

    @Test
    public void testInitialize() {
        SensorInternetVS vs = new SensorInternetVS();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
        StreamElement streamElement1 = new StreamElement(
                new String[]{"value", "value1"},
                new Byte[]{DataTypes.INTEGER, DataTypes.INTEGER},
                new Serializable[]{1,2},
                System.currentTimeMillis()+200);
        vs.dataAvailable("input", streamElement1);
        vs.dispose();

        
    }

    @Test
    public void testInitialize1() {
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp("si-username", "test" ) );
        params.add( new KeyValueImp("si-password" ,"passwd" ) );
        params.add( new KeyValueImp("si-stream-mapping", "1,23") );
        testVsensorConfig.setMainClassInitialParams( params );
        SensorInternetVS vs = new SensorInternetVS();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
        vs.dispose();
    }

    @Test
    public void testInitialize2() {
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp("si-url" , "http://localhost:8080/gsn-core/testvs" ) );
        params.add( new KeyValueImp("si-password" ,"passwd" ) );
        params.add( new KeyValueImp("si-stream-mapping", "1,23") );
        testVsensorConfig.setMainClassInitialParams( params );
        SensorInternetVS vs = new SensorInternetVS();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
        vs.dispose();
    }

    @Test
    public void testInitialize3() {
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp("si-url" , "http://localhost:8080/gsn-core/testvs" ) );
        params.add( new KeyValueImp("si-username", "test" ) );
        params.add( new KeyValueImp("si-stream-mapping", "1,23") );
        testVsensorConfig.setMainClassInitialParams( params );
        SensorInternetVS vs = new SensorInternetVS();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
        vs.dispose();
    }

    @Test
    public void testInitialize4() {
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp("si-url" , "http://localhost:8080/gsn-core/testvs" ) );
        params.add( new KeyValueImp("si-username", "test" ) );
        params.add( new KeyValueImp("si-password" ,"passwd" ) );
        testVsensorConfig.setMainClassInitialParams( params );
        SensorInternetVS vs = new SensorInternetVS();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
        vs.dispose();
    }
}
