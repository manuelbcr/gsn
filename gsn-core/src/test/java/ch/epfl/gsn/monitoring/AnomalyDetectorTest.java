package ch.epfl.gsn.monitoring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.collections.KeyValue;
import org.junit.After;
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
import ch.epfl.gsn.vsensor.DemoVSensor;

public class AnomalyDetectorTest {
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
            new DataField("field1", DataTypes.FLOAT),
            new DataField("field2", DataTypes.INTEGER),
            new DataField("field3", DataTypes.INTEGER),
            new DataField("field4", DataTypes.DOUBLE),
        };
        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("testanomaly");
		File someFile = File.createTempFile("testanomaly", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.DemoVSensor");
		testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);
        KeyValue[] emptyAddressingArray = new KeyValue[0];
        testVsensorConfig.setAddressing(emptyAddressingArray);
        //VirtualSensor pool = new VirtualSensor(testVsensorConfig);
        //Mappings.addVSensorInstance(pool);

        
        sm.executeCreateTable("testanomaly", fields, true);
        StreamElement streamElement1 = new StreamElement(
            new String[]{"field1", "field2","field3","field4"},
            new Byte[]{DataTypes.FLOAT, DataTypes.INTEGER,DataTypes.INTEGER,DataTypes.DOUBLE},
            new Serializable[]{6.321f, 2,123,23.03},
            System.currentTimeMillis()+2500); 

        sm.executeInsert("testanomaly", fields, streamElement1);

    }

	@After
	public void teardown() throws SQLException {
		sm.executeDropTable("testanomaly");
	}
    @Test
    public void testInitialize(){
        DemoVSensor vs = new DemoVSensor();
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "anomaly.positive_outlier.field1" , "-1,6.321f" ) );
        params.add( new KeyValueImp( "anomaly.positive_outlier.field2" , "-1,2" ) );
        params.add( new KeyValueImp( "anomaly.positive_outlier.field3" , "-1,123" ) );
        params.add( new KeyValueImp( "anomaly.positive_outlier.field4" , "-1,23.03" ) );
        params.add( new KeyValueImp( "anomaly.iqr.field3" , "1" ) );
        params.add( new KeyValueImp( "anomaly.unique.field1" , "-1" ) );
        params.add( new KeyValueImp( "anomaly.positive_outlier.field1.field2" , "-1,6.321f" ) );
        testVsensorConfig.setMainClassInitialParams(params);
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
        AnomalyDetector ad = new AnomalyDetector(vs);
        Map<String, Object> stats = ad.getStatistics();
        assertEquals(4, stats.size());
        assertEquals("0",stats.get("vs.testanomaly.output.anomaly.positive_outlier.field1.6.321f.gauge"));
        assertEquals("0",stats.get("vs.testanomaly.output.anomaly.positive_outlier.field2.2.gauge"));
        
        assertTrue(ad.toString().contains("iqr"));
        assertTrue(ad.toString().contains("positive_outlier"));
        assertFalse(ad.toString().contains("negative_outlier"));
        assertTrue(ad.toString().contains("anomaly.positive_outlier.field1=-1,6.321f"));
    }

    @Test
    public void testInitialize2(){
        DemoVSensor vs = new DemoVSensor();
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "anomaly.unique.field1" , "-1" ) );
        params.add( new KeyValueImp( "anomaly.unique.field1.field2 " , "-1" ) );
        testVsensorConfig.setMainClassInitialParams(params);
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
        AnomalyDetector ad = new AnomalyDetector(vs);
        Map<String, Object> stats = ad.getStatistics();
        assertEquals(1, stats.size());
    }

    @Test
    public void testInitializeInvalidPartslength(){
        DemoVSensor vs = new DemoVSensor();
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "anomaly.invalidfunction" , "-1" ) );
        testVsensorConfig.setMainClassInitialParams(params);
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
        AnomalyDetector ad = new AnomalyDetector(vs);
        assertEquals(0, ad.getStatistics().size());
        
    }

    @Test
    public void testInitializeInvalidFunction(){
        DemoVSensor vs = new DemoVSensor();
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "anomaly.invalidfunction.field1" , "-1" ) );
        testVsensorConfig.setMainClassInitialParams(params);
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
        AnomalyDetector ad = new AnomalyDetector(vs);
        assertEquals(0, ad.getStatistics().size());
        
    }
}
