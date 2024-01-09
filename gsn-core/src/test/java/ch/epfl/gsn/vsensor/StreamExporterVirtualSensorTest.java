package ch.epfl.gsn.vsensor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
import ch.epfl.gsn.VirtualSensor;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.KeyValueImp;

public class StreamExporterVirtualSensorTest {
        private static StorageManager sm;
    private VSensorConfig testVsensorConfig;
    private VirtualSensor pool;
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
		sm = StorageManagerFactory.getInstance( "org.h2.Driver","sa","" ,"jdbc:h2:mem:coreTest", Main.DEFAULT_MAX_DB_CONNECTIONS);

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
		testVsensorConfig.setName("teststreamexporter");
        File someFile = File.createTempFile("teststreamexporter", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.StreamExporterVirtualSensor");
        testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "user" , "sa" ) );
        params.add( new KeyValueImp( "password", "" ) );
        params.add( new KeyValueImp( "url" ,"jdbc:h2:mem:coreTest" ) );
        params.add( new KeyValueImp( "driver" ,"org.h2.Driver" ) );
        params.add( new KeyValueImp( "entries" ,"5" ) );
        params.add( new KeyValueImp( "table" ,"testtable" ) );
        testVsensorConfig.setMainClassInitialParams( params );

        sm.executeCreateTable("testtable", fields, true);
        sm.executeCreateTable("teststreamexporter", fields, true);
        
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

        sm.executeInsert("testtable", fields, streamElement1);
        sm.executeInsert("testtable", fields, streamElement2);
        sm.executeInsert("testtable", fields, streamElement3);
        sm.executeInsert("testtable", fields, streamElement4);
    }

    @After
	public void teardown() throws SQLException {
		sm.executeDropTable("teststreamexporter");
        sm.executeDropTable("testtable");
	}
    @Test
    public void testInitialize() throws SQLException {
        StreamExporterVirtualSensor vs = new StreamExporterVirtualSensor();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
        assertNotNull(vs.getConnection());

        for(int i =0; i<1200;i++){
            vs.dataAvailable("input", new StreamElement(
                new String[]{"value", "value1"},
                new Byte[]{DataTypes.INTEGER, DataTypes.INTEGER},
                new Serializable[]{i+1,i+9},
                System.currentTimeMillis()+i));
        }
        vs.dispose();
    }

    @Test
    public void testInitializeCreateTable() throws SQLException {
        StreamExporterVirtualSensor vs = new StreamExporterVirtualSensor();
        params.add( new KeyValueImp( "user" , "sa" ) );
        params.add( new KeyValueImp( "password", "" ) );
        params.add( new KeyValueImp( "url" ,"jdbc:h2:mem:coreTest" ) );
        params.add( new KeyValueImp( "driver" ,"org.h2.Driver" ) );
        params.add( new KeyValueImp( "entries" ,"10" ) );
        params.add( new KeyValueImp( "table" ,"testtablenew" ) );
        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
        sm.executeDropTable("testtablenew");
    }

    @Test
    public void testInitializeFalse1() throws SQLException {
        StreamExporterVirtualSensor vs = new StreamExporterVirtualSensor();
         params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "password", "" ) );
        params.add( new KeyValueImp( "url" ,"jdbc:h2:mem:coreTest" ) );
        params.add( new KeyValueImp( "driver" ,"org.h2.Driver" ) );
        params.add( new KeyValueImp( "entries" ,"10" ) );
        params.add( new KeyValueImp( "table" ,"testtable" ) );
        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
    }

    @Test
    public void testInitializeFalse2() throws SQLException {
        StreamExporterVirtualSensor vs = new StreamExporterVirtualSensor();
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "user" , "sa" ) );
        params.add( new KeyValueImp( "password", "" ) );
        params.add( new KeyValueImp( "url" ,"jdbc:h2:mem:coreTest" ) );
        params.add( new KeyValueImp( "driver" ,"invalid" ) );
        params.add( new KeyValueImp( "entries" ,"10" ) );
        params.add( new KeyValueImp( "table" ,"testtable" ) );
        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
    }
}
