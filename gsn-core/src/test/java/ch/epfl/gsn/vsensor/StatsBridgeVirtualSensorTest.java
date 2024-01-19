package ch.epfl.gsn.vsensor;

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
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.KeyValueImp;

public class StatsBridgeVirtualSensorTest {


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
		sm = StorageManagerFactory.getInstance( "org.h2.Driver","sa","" ,"jdbc:h2:mem:coreTest", Main.DEFAULT_MAX_DB_CONNECTIONS);

		Main.setDefaultGsnConf("/gsn_test.xml");
		Main.getInstance();
	}

    @Before
	public void setup() throws SQLException, IOException {
        DataField[] fields = new DataField[]{
            new DataField("field1", DataTypes.VARCHAR),
        };

        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("teststats");
        File someFile = File.createTempFile("teststats", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.StatsBridgeVirtualSensor");
        testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "logging-interval" , "2" ) );
        testVsensorConfig.setMainClassInitialParams( params );

        
        sm.executeCreateTable("teststats", fields, true);
    }

    @After
	public void teardown() throws SQLException {
		sm.executeDropTable("teststats");
        
	}


    @Test
    public void testInitialize() {
        StatsBridgeVirtualSensor vs = new StatsBridgeVirtualSensor();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
        vs.dataAvailable("input1", new StreamElement());
        vs.dataAvailable("input1", new StreamElement());
        vs.dataAvailable("input1", new StreamElement());
        vs.dispose();
    }
    @Test
    public void testInitialize2() {
        StatsBridgeVirtualSensor vs = new StatsBridgeVirtualSensor();
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "logging-interval" , "invalid" ) );
        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
        vs.dispose();
    }
}
