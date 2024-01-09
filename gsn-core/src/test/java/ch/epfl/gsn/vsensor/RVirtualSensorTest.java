package ch.epfl.gsn.vsensor;

import static org.junit.Assert.assertEquals;
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

public class RVirtualSensorTest {

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
		testVsensorConfig.setName("testrvs");
        File someFile = File.createTempFile("testrvs", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.RVirtualSensor");
        testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "window_size" , "2000" ) );
        params.add( new KeyValueImp( "script_type" , "plot" ) );
        params.add( new KeyValueImp( "step_size" , "1" ) );
        testVsensorConfig.setMainClassInitialParams( params );

        
        sm.executeCreateTable("testrvs", fields, true);
    }

    @After
	public void teardown() throws SQLException {
		sm.executeDropTable("testrvs");
	}

    @Test
    public void testInitialize() {
        RVirtualSensor vs = new RVirtualSensor();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
        assertEquals(2000,vs.getPredicateValueAsIntWithException("window_size"));
        
        StreamElement streamElement1 = new StreamElement(
            new String[]{"message", "phonenumber","receiver"},
            new Byte[]{DataTypes.VARCHAR, DataTypes.VARCHAR,DataTypes.VARCHAR},
            new Serializable[]{"hello","+43699999999","+43699999999"},
            System.currentTimeMillis()+20000);
        
        vs.dataAvailable("input", streamElement1);
        try{
            vs.getPredicateValueAsIntWithException("invalid_param");
        }catch(RuntimeException e){
            assertTrue(e.getMessage().contains("The required parameter: >"));;
        }
    }

    @Test
    public void testInitializeFalse1() {
        RVirtualSensor vs = new RVirtualSensor();
        params.set(0, new KeyValueImp( "server" , "2000" ));
        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
    }
    @Test
    public void testInitializeFalse2() {
        RVirtualSensor vs = new RVirtualSensor();
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "window_size" , "2000" ) );
        params.add( new KeyValueImp( "window_size" , "1" ) );
        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
    }

    @Test
    public void testInitializeFalse3() {
        RVirtualSensor vs = new RVirtualSensor();
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "window_size" , "1" ) );
        params.add( new KeyValueImp( "script_type" , "plot" ) );
        params.add( new KeyValueImp( "step_size" , "100" ) );
        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
    }
    
}
