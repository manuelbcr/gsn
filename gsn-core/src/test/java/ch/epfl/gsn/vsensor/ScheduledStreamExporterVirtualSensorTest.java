package ch.epfl.gsn.vsensor;

import static org.junit.Assert.*;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
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
import scala.slick.direct.AnnotationMapper.table;

public class ScheduledStreamExporterVirtualSensorTest {
    private static StorageManager sm;
    private VSensorConfig testVsensorConfig;
    private VirtualSensor pool;
    ArrayList < KeyValue > params;
    private static Connection connection;
    private  ClockedBridgeVirtualSensor vs;

    @BeforeClass
	public static void setUpBeforeClass() throws Exception {
        
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
	public void setup() throws Exception {
        DataField[] fields = new DataField[]{
            new DataField("value", DataTypes.INTEGER),
            new DataField("value1", DataTypes.INTEGER)
        };

        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("scheduledstreamexporter");
        File someFile = File.createTempFile("scheduledstreamexporter", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.ScheduledStreamExporterVirtualSensor");
        testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "rate" ,"5" ) );
        params.add( new KeyValueImp("table_name", "testtable") );
        testVsensorConfig.setMainClassInitialParams( params );
        vs = new ClockedBridgeVirtualSensor();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        
        sm.executeCreateTable("scheduledstreamexporter", fields, true);
    
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

        sm.executeInsert("scheduledstreamexporter", fields, streamElement1);
        sm.executeInsert("scheduledstreamexporter", fields, streamElement2);
        sm.executeInsert("scheduledstreamexporter", fields, streamElement3);
        sm.executeInsert("scheduledstreamexporter", fields, streamElement4);
    }

    @After
	public void teardown() throws Exception {
		sm.executeDropTable("scheduledstreamexporter");
	}

    @Test
    public void testInitialize1() {
        ScheduledStreamExporterVirtualSensor vs = new ScheduledStreamExporterVirtualSensor();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
    }
    @Test
    public void testInitializeTrue() throws SQLException, InterruptedException {
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp("user" ,"sa" ) );
        params.add( new KeyValueImp("url", "jdbc:h2:mem:coreTest") );
        params.add( new KeyValueImp("driver", "org.h2.Driver") );
        params.add( new KeyValueImp("password", "") );
        params.add( new KeyValueImp("table", "initialscheduledstreamexporterTable") );
        params.add( new KeyValueImp("start-time", "2024-01-10T12:30:45.123+0300") );
        params.add( new KeyValueImp("rate", "10") );
        
        testVsensorConfig.setMainClassInitialParams( params );
        ScheduledStreamExporterVirtualSensor vs = new ScheduledStreamExporterVirtualSensor();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
        StreamElement streamElement1 = new StreamElement(
                new String[]{"value", "value1"},
                new Byte[]{DataTypes.INTEGER, DataTypes.INTEGER},
                new Serializable[]{1,2},
                System.currentTimeMillis()+200);
        vs.dataAvailable("inputstream",streamElement1);
        Thread.sleep(100);
        sm.executeDropTable("initialscheduledstreamexporterTable");
        StreamElement streamElement2 = new StreamElement(
                new String[]{"value", "value1"},
                new Byte[]{DataTypes.INTEGER, DataTypes.INTEGER},
                new Serializable[]{1,2},
                System.currentTimeMillis()+200);
        vs.dataAvailable("inputstream",streamElement2);
        Thread.sleep(100);
        StreamElement streamElement3 = new StreamElement(
                    new String[]{"value", "value1"},
                    new Byte[]{DataTypes.INTEGER, DataTypes.VARCHAR},
                    new Serializable[]{1,"s"},
                    System.currentTimeMillis()+200);
        vs.dataAvailable("inputstream",streamElement3);
        Thread.sleep(100);
        vs.dispose();
    }

    @Test
    public void testInitializeFalse() throws SQLException {
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp("user" ,"sa" ) );
        params.add( new KeyValueImp("url", "jdbc:h2:mem:coreTest") );
        params.add( new KeyValueImp("driver", "wrong") );
        params.add( new KeyValueImp("password", "") );
        params.add( new KeyValueImp("table", "initialscheduledstreamexporterTable") );
        params.add( new KeyValueImp("start-time", "2024-01-10T12:30:45.123+0300") );
        params.add( new KeyValueImp("rate", "10") );
        
        testVsensorConfig.setMainClassInitialParams( params );
        ScheduledStreamExporterVirtualSensor vs = new ScheduledStreamExporterVirtualSensor();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
    }

    @Test
    public void testInitializeFalse1() throws SQLException {
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp("user" ,"sa" ) );
        params.add( new KeyValueImp("url", "jdbc:h2:mem:coreTest") );
        params.add( new KeyValueImp("driver", "org.h2.Driver") );
        params.add( new KeyValueImp("password", "") );
        params.add( new KeyValueImp("table", "scheduledstreamexporter") );
        params.add( new KeyValueImp("start-time", "2024-01-10T12:30:45.123+0300") );
        params.add( new KeyValueImp("rate", "10") );
        DataField[] fields = new DataField[]{
            new DataField("value", DataTypes.INTEGER),
            new DataField("value1", DataTypes.VARCHAR)
        };
        testVsensorConfig.setOutputStructure(fields);
        testVsensorConfig.setMainClassInitialParams( params );
        ScheduledStreamExporterVirtualSensor vs = new ScheduledStreamExporterVirtualSensor();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
    }
}