package ch.epfl.gsn.vsensor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.event.ActionEvent;
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

public class ClockedBridgeVirtualSensorTest {
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
		sm = StorageManagerFactory.getInstance( "org.h2.Driver","sa","" ,"jdbc:h2:mem:coreTest", Main.DEFAULT_MAX_DB_CONNECTIONS);

		Main.setDefaultGsnConf("/gsn_test.xml");
		Main.getInstance();
	}

    @Before
	public void setup() throws SQLException, IOException {
         DataField[] fields = new DataField[]{
            new DataField("table_name", DataTypes.VARCHAR),
            new DataField("rate", DataTypes.INTEGER)
        };

        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("clockedtbl");
        File someFile = File.createTempFile("clockedtbl", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.ClockedBridgeVirtualSensor");
        testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "rate" , "10" ) );
        params.add( new KeyValueImp( "table_name" , "testtable" ) );
    
        testVsensorConfig.setMainClassInitialParams( params );

        DataField[] testtablefields = new DataField[]{
            new DataField("xy", DataTypes.VARCHAR),
            new DataField("z", DataTypes.VARCHAR)
        };
        
        sm.executeCreateTable("clockedtbl", fields, true);
        assertNotNull(sm.tableExists("clockedtbl"));
        StreamElement streamElement1 = new StreamElement(
                new String[]{"table_name", "rate"},
                new Byte[]{DataTypes.VARCHAR, DataTypes.INTEGER},
                new Serializable[]{"table1",2},
                System.currentTimeMillis()+200);
        sm.executeInsert("clockedtbl", fields, streamElement1);
        sm.executeCreateTable("testtable",testtablefields,true);

        StreamElement streamElement2 = new StreamElement(
                new String[]{"xy", "z"},
                new Byte[]{DataTypes.VARCHAR, DataTypes.VARCHAR},
                new Serializable[]{"xxy","abv"},
                System.currentTimeMillis()+200);
        
        
        sm.executeInsert("testtable", testtablefields, streamElement2);
    }   

    @After
	public void teardown() throws SQLException {
		sm.executeDropTable("clockedtbl");
        sm.executeDropTable("testtable");
	}


    @Test
    public void testInitialize() throws SQLException{
        ClockedBridgeVirtualSensor vs = new ClockedBridgeVirtualSensor();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(sm.tableExists("clockedtbl"));
        assertTrue(vs.initialize());
        
        StreamElement streamElement1 = new StreamElement(
                new String[]{"table_name", "rate"},
                new Byte[]{DataTypes.VARCHAR, DataTypes.INTEGER},
                new Serializable[]{"testtable",2},
                System.currentTimeMillis()+200);
        vs.dataAvailable("input1", streamElement1);
        vs.actionPerformed(null);
        vs.dispose();
    }

    @Test
    public void testInitializeWithoutRate() throws SQLException{
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "table_name" , "testtable" ) );
        testVsensorConfig.setMainClassInitialParams( params );
        ClockedBridgeVirtualSensor vs = new ClockedBridgeVirtualSensor();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(sm.tableExists("clockedtbl"));
        assertFalse(vs.initialize());
    }

    @Test
    public void testInitializeWithoutTableName() throws SQLException{
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "rate" , "10" ) );
        testVsensorConfig.setMainClassInitialParams( params );
        ClockedBridgeVirtualSensor vs1 = new ClockedBridgeVirtualSensor();
        vs1.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs1.initialize());
    }

}
