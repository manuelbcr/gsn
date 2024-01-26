package ch.epfl.gsn.vsensor;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;

import org.apache.commons.collections.KeyValue;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.epfl.gsn.Main;
import ch.epfl.gsn.Mappings;
import ch.epfl.gsn.VirtualSensor;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.KeyValueImp;

public class ConditionalDeleteVirtualSensorTest {
    private static StorageManager sm;
    private VSensorConfig testVsensorConfig;
    private ArrayList < KeyValue > params;
    private VirtualSensor pool;

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
            new DataField("field2", DataTypes.VARCHAR),
            new DataField("field3", DataTypes.INTEGER),
            new DataField("field4",DataTypes.DOUBLE)
        };
        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("conditionaldelete");
		File someFile = File.createTempFile("conditionaldelete", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.BridgeVirtualSensorPermasense");
		testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);
        KeyValue[] emptyAddressingArray = new KeyValue[0];
        testVsensorConfig.setAddressing(emptyAddressingArray);
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "field1" , "field1" ) );
        params.add( new KeyValueImp( "field2" , "field2" ) );
        params.add( new KeyValueImp( "operation1" , "=" ) );
        params.add( new KeyValueImp( "operation2" , "=" ) );
        params.add( new KeyValueImp( "join2" , "AND" ) );
        testVsensorConfig.setMainClassInitialParams( params );
       
        
        sm.executeCreateTable("conditionaldelete", fields, true);
    }
	@After
	public void teardown() throws SQLException {
		sm.executeDropTable("conditionaldelete");
	}


    @Test
    public void testInitialize() {
        ConditionalDeleteVirtualSensor vs = new ConditionalDeleteVirtualSensor();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
        StreamElement streamElement1 = new StreamElement(
                new String[]{"field1","field2"},
                new Byte[]{DataTypes.VARCHAR,DataTypes.VARCHAR},
                new Serializable[]{"field1","field2"},
                System.currentTimeMillis()+200);
        vs.dataAvailable("inputstreamname", streamElement1);

        StreamElement streamElement2 = new StreamElement(
            new String[]{"field1"},
            new Byte[]{DataTypes.VARCHAR},
            new Serializable[]{"field1"},
            System.currentTimeMillis()+200);
        vs.dataAvailable("inputstreamname", streamElement2);
    }


    @Test
    public void testInitialize0() {
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "field1" , "field3" ) );
        params.add( new KeyValueImp( "field2" , "field4" ) );
        params.add( new KeyValueImp( "operation1" , "=" ) );
        params.add( new KeyValueImp( "operation2" , "=" ) );
        params.add( new KeyValueImp( "join2" , "AND" ) );
        testVsensorConfig.setMainClassInitialParams( params );
        ConditionalDeleteVirtualSensor vs = new ConditionalDeleteVirtualSensor();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
        StreamElement streamElement1 = new StreamElement(
                new String[]{"field3","field4"},
                new Byte[]{DataTypes.INTEGER,DataTypes.DOUBLE},
                new Serializable[]{1,2.1},
                System.currentTimeMillis()+200);
        vs.dataAvailable("inputstreamname", streamElement1);
    }
    
    @Test
    public void testInitialize1() {
        ConditionalDeleteVirtualSensor vs = new ConditionalDeleteVirtualSensor();
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "field1" , "fieldx" ) );
        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
    }

    @Test
    public void testInitialize2(){
        ConditionalDeleteVirtualSensor vs = new ConditionalDeleteVirtualSensor();
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "field1" , "field1" ) );
        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
    }
    @Test
    public void testInitialize3(){
        ConditionalDeleteVirtualSensor vs = new ConditionalDeleteVirtualSensor();
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "field1" , "field1" ) );
        params.add( new KeyValueImp( "field2" , "field2" ) );
        params.add( new KeyValueImp( "operation1" , "operation1" ) );
        params.add( new KeyValueImp( "operation2" , "operation2" ) );
        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
    }
}
