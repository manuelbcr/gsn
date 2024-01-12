package ch.epfl.gsn.vsensor;

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

public class CounterVSTest {

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
            new DataField("field1", DataTypes.DOUBLE),
        };

        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("countervstest");
        File someFile = File.createTempFile("countervstest", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.BridgeVirtualSensor");
        testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);
        ArrayList < KeyValue > params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "allow-nulls" , "true" ) );
        testVsensorConfig.setMainClassInitialParams( params );

        
        sm.executeCreateTable("countervstest", fields, true);
    }

    @After
	public void teardown() throws SQLException {
		sm.executeDropTable("countervstest");
	}
    @Test
    public void testInitialize() {
        CounterVS vs = new CounterVS();
        assertTrue(vs.initialize());
         StreamElement streamElement1 = new StreamElement(
                new String[]{"field1"},
                new Byte[]{DataTypes.DOUBLE},
                new Serializable[]{1.5},
                System.currentTimeMillis()+200);
        vs.dataAvailable("inputstreamname", streamElement1);
        vs.dispose();   
    }
}
