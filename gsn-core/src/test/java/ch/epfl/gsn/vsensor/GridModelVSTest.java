package ch.epfl.gsn.vsensor;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.sql.SQLException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.DriverManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.apache.commons.collections.KeyValue;

import ch.epfl.gsn.Main;
import ch.epfl.gsn.Mappings;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.KeyValueImp;
import ch.epfl.gsn.VirtualSensor;



public class GridModelVSTest {
    private static StorageManager sm;
    private VSensorConfig testVsensorConfig;
    private VSensorConfig testVsensorConfig1;
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
		sm = StorageManagerFactory.getInstance( "org.h2.Driver","sa","" ,"jdbc:h2:mem:coreTest", Main.DEFAULT_MAX_DB_CONNECTIONS);

		Main.setDefaultGsnConf("/gsn_test.xml");
		Main.getInstance();
	}
    @Before
	public void setup() throws SQLException, IOException {
        DataField[] fields = new DataField[]{
            new DataField("STATUS", DataTypes.VARCHAR),
            new DataField("ID", DataTypes.VARCHAR)
        };
        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("gridmodelvs");
		File someFile = File.createTempFile("gridmodelvs", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.BridgeVirtualSensor");
		testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);
        KeyValue[] emptyAddressingArray = new KeyValue[0];
        testVsensorConfig.setAddressing(emptyAddressingArray);
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "model_VS" , "modellingvs" ) );
        params.add( new KeyValueImp( "model_index" , "1" ) );
        params.add( new KeyValueImp( "field" , "ID" ) );
        params.add( new KeyValueImp( "grid_size" , "10" ) );
        params.add( new KeyValueImp( "cell_size" , "10" ) );
        params.add( new KeyValueImp( "x_bottomLeft" , "1.1" ) );
        params.add( new KeyValueImp( "y_bottomLeft", "1.1"));
        testVsensorConfig.setMainClassInitialParams( params );



        testVsensorConfig1 = new VSensorConfig();
		testVsensorConfig1.setName("modellingvs");
        File someFile1 = File.createTempFile("modellingvs", ".xml");
		testVsensorConfig1.setMainClass("ch.epfl.gsn.vsensor.ModellingVirtualSensor");
        testVsensorConfig1.setFileName(someFile1.getAbsolutePath());
        testVsensorConfig1.setOutputStructure(fields);
        ArrayList < KeyValue > params1 = new ArrayList < KeyValue >( );
        params1.add( new KeyValueImp( "model" , "ch.epfl.gsn.utils.models.DummyModel,ch.epfl.gsn.utils.models.DummyModel" ) );
        testVsensorConfig1.setMainClassInitialParams( params1 );

       
        pool = new VirtualSensor(testVsensorConfig1);
        Mappings.addVSensorInstance(pool);
        try{
            pool.start();
        }catch(Exception e){
            System.out.println("exception occured "+ e.getMessage());
        }
       
        
        sm.executeCreateTable("gridmodelvs", fields, true);
    }

	@After
	public void teardown() throws SQLException {
		sm.executeDropTable("gridmodelvs");
	}


    @Test
    public void testInitialize(){
        GridModelVS vs = new GridModelVS();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
        assertNotNull(vs.getOutputFormat());
        vs.dataAvailable("SSTREAM", new StreamElement(new String[]{"STATUS", "ID"},new Byte[]{DataTypes.VARCHAR, DataTypes.VARCHAR},new Serializable[]{"add", "mica"},System.currentTimeMillis()));
    }

    @Test
    public void testInitialize1(){
        GridModelVS vs = new GridModelVS();
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "model_index" , "1" ) );
        params.add( new KeyValueImp( "field" , "ID" ) );
        params.add( new KeyValueImp( "grid_size" , "10" ) );
        params.add( new KeyValueImp( "cell_size" , "10" ) );
        params.add( new KeyValueImp( "x_bottomLeft" , "1.1" ) );
        params.add( new KeyValueImp( "y_bottomLeft", "1.1"));
        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());

        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "model_VS" , "wrongvs" ) );
        params.add( new KeyValueImp( "model_index" , "1" ) );
        params.add( new KeyValueImp( "field" , "ID" ) );
        params.add( new KeyValueImp( "grid_size" , "10" ) );
        params.add( new KeyValueImp( "cell_size" , "10" ) );
        params.add( new KeyValueImp( "x_bottomLeft" , "1.1" ) );
        params.add( new KeyValueImp( "y_bottomLeft", "1.1"));
        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
    }

    @Test
    public void testInitialize2(){
        GridModelVS vs = new GridModelVS();
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "model_VS" , "modellingvs" ) );
        params.add( new KeyValueImp( "field" , "ID" ) );
        params.add( new KeyValueImp( "grid_size" , "10" ) );
        params.add( new KeyValueImp( "cell_size" , "10" ) );
        params.add( new KeyValueImp( "x_bottomLeft" , "1.1" ) );
        params.add( new KeyValueImp( "y_bottomLeft", "1.1"));
        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
    }

    @Test
    public void testInitialize3(){
        GridModelVS vs = new GridModelVS();
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "model_VS" , "modellingvs" ) );
        params.add( new KeyValueImp( "model_index" , "1" ) );
        params.add( new KeyValueImp( "grid_size" , "10" ) );
        params.add( new KeyValueImp( "cell_size" , "10" ) );
        params.add( new KeyValueImp( "x_bottomLeft" , "1.1" ) );
        params.add( new KeyValueImp( "y_bottomLeft", "1.1"));
        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
    }

    @Test
    public void testInitialize4(){
        GridModelVS vs = new GridModelVS();
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "model_VS" , "modellingvs" ) );
        params.add( new KeyValueImp( "model_index" , "1" ) );
        params.add( new KeyValueImp( "field" , "ID" ) );
        params.add( new KeyValueImp( "grid_size" , "10" ) );
        params.add( new KeyValueImp( "cell_size" , "10" ) );
        params.add( new KeyValueImp( "y_bottomLeft", "1.1"));
        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
    }

    @Test
    public void testInitialize6(){
        GridModelVS vs = new GridModelVS();
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "model_VS" , "modellingvs" ) );
        params.add( new KeyValueImp( "model_index" , "1" ) );
        params.add( new KeyValueImp( "field" , "ID" ) );
        params.add( new KeyValueImp( "grid_size" , "10" ) );
        params.add( new KeyValueImp( "cell_size" , "10" ) );
        params.add( new KeyValueImp( "x_bottomLeft" , "1.1" ) );
        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
    }
    @Test
    public void testInitialize7(){
        GridModelVS vs = new GridModelVS();
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "model_VS" , "modellingvs" ) );
        params.add( new KeyValueImp( "model_index" , "1" ) );
        params.add( new KeyValueImp( "field" , "ID" ) );
        params.add( new KeyValueImp( "cell_size" , "10" ) );
        params.add( new KeyValueImp( "x_bottomLeft" , "1.1" ) );
        params.add( new KeyValueImp( "y_bottomLeft", "1.1"));
        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
    }
    @Test
    public void testInitialize8(){
        GridModelVS vs = new GridModelVS();
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "model_VS" , "modellingvs" ) );
        params.add( new KeyValueImp( "model_index" , "1" ) );
        params.add( new KeyValueImp( "field" , "ID" ) );
        params.add( new KeyValueImp( "grid_size" , "10" ) );
        params.add( new KeyValueImp( "x_bottomLeft" , "1.1" ) );
        params.add( new KeyValueImp( "y_bottomLeft", "1.1"));
        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
    }

    @Test
    public void testInitialize9(){
        GridModelVS vs = new GridModelVS();
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "model_VS" , "modellingvs" ) );
        params.add( new KeyValueImp( "model_index" , "1" ) );
        params.add( new KeyValueImp( "field" , "ID" ) );
        params.add( new KeyValueImp( "grid_size" , "10" ) );
        params.add( new KeyValueImp( "cell_size" , "10" ) );
        params.add( new KeyValueImp( "x_bottomLeft" , "x" ) );
        params.add( new KeyValueImp( "y_bottomLeft", "1.1"));
        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
    }

    @Test
    public void testInitialize10(){
        GridModelVS vs = new GridModelVS();
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "model_VS" , "modellingvs" ) );
        params.add( new KeyValueImp( "model_index" , "1" ) );
        params.add( new KeyValueImp( "field" , "ID" ) );
        params.add( new KeyValueImp( "grid_size" , "10" ) );
        params.add( new KeyValueImp( "cell_size" , "10" ) );
        params.add( new KeyValueImp( "x_bottomLeft" , "1.1" ) );
        params.add( new KeyValueImp( "y_bottomLeft", "y"));
        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());

    }
    @Test
    public void testInitialize11(){
        GridModelVS vs = new GridModelVS();
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "model_VS" , "modellingvs" ) );
        params.add( new KeyValueImp( "model_index" , "1" ) );
        params.add( new KeyValueImp( "field" , "ID" ) );
        params.add( new KeyValueImp( "grid_size" , "dad" ) );
        params.add( new KeyValueImp( "cell_size" , "10" ) );
        params.add( new KeyValueImp( "x_bottomLeft" , "1.1" ) );
        params.add( new KeyValueImp( "y_bottomLeft", "1.1"));
        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
    }


    @Test
    public void testInitialize12(){
        GridModelVS vs = new GridModelVS();
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "model_VS" , "modellingvs" ) );
        params.add( new KeyValueImp( "model_index" , "1" ) );
        params.add( new KeyValueImp( "field" , "ID" ) );
        params.add( new KeyValueImp( "grid_size" , "-2" ) );
        params.add( new KeyValueImp( "cell_size" , "10" ) );
        params.add( new KeyValueImp( "x_bottomLeft" , "1.1" ) );
        params.add( new KeyValueImp( "y_bottomLeft", "1.1"));
        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
    }

    @Test
    public void testInitialize13(){
        GridModelVS vs = new GridModelVS();
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "model_VS" , "modellingvs" ) );
        params.add( new KeyValueImp( "model_index" , "1" ) );
        params.add( new KeyValueImp( "field" , "ID" ) );
        params.add( new KeyValueImp( "grid_size" , "10" ) );
        params.add( new KeyValueImp( "cell_size" , "dad" ) );
        params.add( new KeyValueImp( "x_bottomLeft" , "1.1" ) );
        params.add( new KeyValueImp( "y_bottomLeft", "1.1"));
        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
    }

    @Test
    public void testInitialize14(){
        GridModelVS vs = new GridModelVS();
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "model_VS" , "modellingvs" ) );
        params.add( new KeyValueImp( "model_index" , "1" ) );
        params.add( new KeyValueImp( "field" , "ID" ) );
        params.add( new KeyValueImp( "grid_size" , "10" ) );
        params.add( new KeyValueImp( "cell_size" , "-2" ) );
        params.add( new KeyValueImp( "x_bottomLeft" , "1.1" ) );
        params.add( new KeyValueImp( "y_bottomLeft", "1.1"));
        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
        vs.dispose();
    }
}
