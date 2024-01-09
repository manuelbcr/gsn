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
import ch.epfl.gsn.VirtualSensor;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.KeyValueImp;

public class SMACleanerTest {
    
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
            new DataField("field1", DataTypes.VARCHAR),
        };

        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("testsma");
        File someFile = File.createTempFile("testsma", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.SMACleaner");
        testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);
        ArrayList < KeyValue > params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "size" , "20" ) );
        params.add( new KeyValueImp( "error-threshold", "0.5" ) );
        testVsensorConfig.setMainClassInitialParams( params );

        
        sm.executeCreateTable("testsma", fields, true);
    }

    @After
	public void teardown() throws SQLException {
		sm.executeDropTable("testsma");
	}

    @Test
    public void testInitialize(){
        SMACleaner vs = new SMACleaner();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
        vs.dispose();
    }
}
