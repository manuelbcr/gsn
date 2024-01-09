package ch.epfl.gsn.vsensor;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
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
    public void testInitialize() {
        ScheduledStreamExporterVirtualSensor vs = new ScheduledStreamExporterVirtualSensor();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        System.out.println(vs.initialize());
    }
}
