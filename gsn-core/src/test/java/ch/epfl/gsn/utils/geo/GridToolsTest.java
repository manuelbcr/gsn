package ch.epfl.gsn.utils.geo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.DriverManager;
import java.sql.SQLException;
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

public class GridToolsTest {


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
		sm = StorageManagerFactory.getInstance( "org.h2.Driver","sa","" ,"jdbc:h2:mem:test", Main.DEFAULT_MAX_DB_CONNECTIONS);

		Main.setDefaultGsnConf("/gsn_test.xml");
		Main.getInstance();
	}

    @Before
	public void setup() throws SQLException, IOException {
        DataField[] fields = new DataField[]{
            new DataField("name", DataTypes.VARCHAR),
            new DataField("value", DataTypes.DOUBLE)
        };
        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("testvsname");
		File someFile = File.createTempFile("testvsname", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.BridgeVirtualSensor");
		testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);
        KeyValue[] emptyAddressingArray = new KeyValue[0];
        testVsensorConfig.setAddressing(emptyAddressingArray);
        VirtualSensor pool = new VirtualSensor(testVsensorConfig);
        Mappings.addVSensorInstance(pool);

        
        sm.executeCreateTable("testvsname", fields, true);
    
        StreamElement streamElement1 = new StreamElement(
                new String[]{"name", "value"},
                new Byte[]{DataTypes.VARCHAR, DataTypes.DOUBLE},
                new Serializable[]{"xy", 45.5},
                System.currentTimeMillis());
    
        StreamElement streamElement2 = new StreamElement(
                new String[]{"name", "value"},
                new Byte[]{DataTypes.VARCHAR, DataTypes.DOUBLE},
                new Serializable[]{"xy", 45.4},
                System.currentTimeMillis()+1000);
        
        StreamElement streamElement3 = new StreamElement(
                new String[]{"name", "value"},
                new Byte[]{DataTypes.VARCHAR, DataTypes.DOUBLE},
                new Serializable[]{"xy", 45.4},
                System.currentTimeMillis()+2000);

        StreamElement streamElement4 = new StreamElement(
                new String[]{"name", "value"},
                new Byte[]{DataTypes.VARCHAR, DataTypes.DOUBLE},
                new Serializable[]{"xy", 45.4},
                System.currentTimeMillis()+2500); 

        sm.executeInsert("testvsname", fields, streamElement1);
        sm.executeInsert("testvsname", fields, streamElement2);
        sm.executeInsert("testvsname", fields, streamElement3);
        sm.executeInsert("testvsname", fields, streamElement4);
    }

	@After
	public void teardown() throws SQLException {
		sm.executeDropTable("testvsname");
	}

    @Test
    public void testDeSerializeToString() {
        Double[][] mockData = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(mockData);
            out.close();
            byte[] mockSerializedData = bos.toByteArray();

            String result = GridTools.deSerializeToString(mockSerializedData);

            String expected = "1.0 2.0 3.0 \n4.0 5.0 6.0 \n";
            assertEquals(expected, result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testDeSerializeToCell() {
        Double[][] mockData = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}, {7.0, 8.0, 9.0}};

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(mockData);
            out.close();
            byte[] mockSerializedData = bos.toByteArray();
            int xcell = 1;
            int ycell = 2;
            double result = GridTools.deSerializeToCell(mockSerializedData, xcell, ycell);

            double expected = mockData[ycell][xcell];
            assertEquals(expected, result, 0.0001); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeSerializeToStringWithBoundaries() {
        Double[][] mockData = {
                {1.0, 2.0, 3.0, 4.0},
                {5.0, 6.0, 7.0, 8.0},
                {9.0, 10.0, 11.0, 12.0},
                {13.0, 14.0, 15.0, 16.0}
        };

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(mockData);
            out.close();
            byte[] mockSerializedData = bos.toByteArray();

            int xmin = 1;
            int xmax = 2;
            int ymin = 0;
            int ymax = 2;
            String result = GridTools.deSerializeToStringWithBoundaries(mockSerializedData, xmin, xmax, ymin, ymax);
            String expected = "2.0 3.0 \n6.0 7.0 \n10.0 11.0 \n";
            assertEquals(expected, result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeSerialize() {
        Double[][] mockData = {
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0},
                {7.0, 8.0, 9.0}
        };

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(mockData);
            out.close();
            byte[] mockSerializedData = bos.toByteArray();

            Double[][] result = GridTools.deSerialize(mockSerializedData);

            assertEquals(mockData, result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testExecuteQueryForGridAsString(){
        String result = GridTools.executeQueryForGridAsString("SELECT * FROM testvsname", "sensor");
        assertTrue(result.contains("# Query:"));
        assertTrue(result.contains("# PK"));
    }   

    @Test
    public void testExecuteQueryForCell2TimeSeriesAsListOfDoubles(){
        Map<Long, Double> result = GridTools.executeQueryForCell2TimeSeriesAsListOfDoubles("SELECT * FROM testvsname", 1, 1, "testvsname");
        assertNotNull(result);
    }   

    @Test
    public void testExecuteQueryForSubGridAsListOfStrings(){
        Map<Long, String> result = GridTools.executeQueryForSubGridAsListOfStrings("SELECT * FROM testvsname", 1, 1, 1,1,"testvsname");
        assertNotNull(result);
    }   

    @Test
    public void testExecuteQueryForGridAsListOfStrings(){
        Map<Long, String> result = GridTools.executeQueryForGridAsListOfStrings("SELECT * FROM testvsname","testvsname");
        assertNotNull(result);
    } 

   
}

