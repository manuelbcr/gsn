package ch.epfl.gsn.vsensor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

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

public class ChartVirtualSensorTest {

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
		sm = StorageManagerFactory.getInstance( "org.h2.Driver","sa","" ,"jdbc:h2:mem:test", Main.DEFAULT_MAX_DB_CONNECTIONS);

		Main.setDefaultGsnConf("/gsn_test.xml");
		Main.getInstance();
	}

    @Before
	public void setup() throws SQLException, IOException {
        DataField[] fields = new DataField[]{
            new DataField("value", DataTypes.INTEGER),
            new DataField("value1", DataTypes.INTEGER)
        };

        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("testchart");
        File someFile = File.createTempFile("testchart", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.ChartVirtualSensor");
        testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "history-size" , "10" ) );
        params.add( new KeyValueImp( "input-stream", "testchart" ) );
        params.add( new KeyValueImp( "title" ,"Test Chart" ) );
        params.add( new KeyValueImp("type", "Line") );
        params.add( new KeyValueImp("height", "480") );
        params.add( new KeyValueImp("width", "640") );
        params.add( new KeyValueImp("vertical-axis", "Values") );
        testVsensorConfig.setMainClassInitialParams( params );

        
        sm.executeCreateTable("testchart", fields, true);
    
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

        sm.executeInsert("testchart", fields, streamElement1);
        sm.executeInsert("testchart", fields, streamElement2);
        sm.executeInsert("testchart", fields, streamElement3);
        sm.executeInsert("testchart", fields, streamElement4);
    }

    @After
	public void teardown() throws SQLException {
		sm.executeDropTable("testchart");
	}


    @Test
    public void testChartInfoInitialization() {
        ChartInfo chartInfo = new ChartInfo();
        assertNotNull(chartInfo);

    
        chartInfo.setPlotTitle("Test Chart");
        chartInfo.setInputStreamName("testchart");
        chartInfo.setWidth(800);
        chartInfo.setHeight(600);
        chartInfo.setHistorySize(10);
        chartInfo.setType("Line");
        chartInfo.setVerticalAxisTitle("Values");
        chartInfo.initialize();

        assertTrue(chartInfo.toString().contains("Test Chart"));
        assertTrue(chartInfo.toString().contains("Width : 800"));
        assertTrue(chartInfo.toString().contains("Height : 600"));
        assertTrue(chartInfo.toString().contains("Type : Line"));

        assertEquals("".hashCode(), chartInfo.hashCode());
        chartInfo.addData( new StreamElement( new String[]{"value2","value2"},new Byte[]{DataTypes.INTEGER,DataTypes.INTEGER},new Serializable[]{1,3},System.currentTimeMillis()));
        chartInfo.addData( new StreamElement( new String[]{"value2","value2"},new Byte[]{DataTypes.INTEGER,DataTypes.INTEGER},new Serializable[]{2,4},System.currentTimeMillis()));
        chartInfo.addData( new StreamElement( new String[]{"value2","value2"},new Byte[]{DataTypes.INTEGER,DataTypes.INTEGER},new Serializable[]{3,7},System.currentTimeMillis()));
        chartInfo.addData( new StreamElement( new String[]{"value2","value2"},new Byte[]{DataTypes.INTEGER,DataTypes.INTEGER},new Serializable[]{4,9},System.currentTimeMillis()));
        assertNotNull(chartInfo.writePlot());
        assertFalse(chartInfo.equals(null));
        assertTrue(chartInfo.equals(chartInfo));
        assertEquals("testchart", chartInfo.getInputStreamName());
    }

    @Test
    public void testChartVirtualSensorInitialization(){
        ChartVirtualSensor vs = new ChartVirtualSensor();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
        vs.dataAvailable("testchart", new StreamElement(new String[]{"value", "value1"},new Byte[]{DataTypes.INTEGER, DataTypes.INTEGER},new Serializable[]{1,2},System.currentTimeMillis()+200));
        vs.dispose();
    }

    @Test
    public void testChartVirtualSensorInitialization1(){
        ChartVirtualSensor vs = new ChartVirtualSensor();
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "input-stream", "testchart" ) );
        params.add( new KeyValueImp( "title" ,"Test Chart" ) );
        params.add( new KeyValueImp("type", "Line") );
        params.add( new KeyValueImp("height", "480") );
        params.add( new KeyValueImp("width", "640") );
        params.add( new KeyValueImp("vertical-axis", "Values") );
        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
        vs.dataAvailable("testchart", new StreamElement(new String[]{"value", "value1"},new Byte[]{DataTypes.INTEGER, DataTypes.INTEGER},new Serializable[]{1,2},System.currentTimeMillis()+200));
        vs.dataAvailable("testchart", new StreamElement(new String[]{"value", "value1"},new Byte[]{DataTypes.INTEGER, DataTypes.INTEGER},new Serializable[]{1,2},System.currentTimeMillis()+200));
        vs.dataAvailable("testchart", new StreamElement(new String[]{"value", "value1"},new Byte[]{DataTypes.INTEGER, DataTypes.INTEGER},new Serializable[]{1,2},System.currentTimeMillis()+200));
        vs.dataAvailable("testchart", new StreamElement(new String[]{"value", "value1"},new Byte[]{DataTypes.INTEGER, DataTypes.INTEGER},new Serializable[]{1,2},System.currentTimeMillis()+200));
        vs.dispose();
    }

}
