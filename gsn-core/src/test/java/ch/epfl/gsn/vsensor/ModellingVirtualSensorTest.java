package ch.epfl.gsn.vsensor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
import ch.epfl.gsn.utils.models.DummyModel;

public class ModellingVirtualSensorTest {
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
            new DataField("field1", DataTypes.VARCHAR),
        };
        

        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("modellingvs");
        File someFile = File.createTempFile("modellingvs", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.BridgeVirtualSensor");
        testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);
        
        sm.executeCreateTable("modellingvs", fields, true);
    }

    @After
	public void teardown() throws SQLException {
		sm.executeDropTable("modellingvs");
	}

    @Test
    public void testInitialize() {
        ModellingVirtualSensor vs = new ModellingVirtualSensor();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
        vs.dispose();
    }

    @Test 
    public void testInitialize2(){
        ModellingVirtualSensor vs = new ModellingVirtualSensor();
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "model" , "ch.epfl.gsn.utils.models.DummyModel,ch.epfl.gsn.utils.models.DummyModel" ) );
        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
        assertNotNull(vs.getModel(0));
        assertNull(vs.getModel(2));

        StreamElement streamElement1 = new StreamElement(
            new String[]{"field1"},
            new Byte[]{DataTypes.VARCHAR},
            new Serializable[]{"x"},
            System.currentTimeMillis()+200);

        vs.dataAvailable("input", streamElement1);
    }
}
