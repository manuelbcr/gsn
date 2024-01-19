package ch.epfl.gsn.vsensor;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
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

public class GridRendererTest {
    
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
		testVsensorConfig.setName("testgridrenderer");
        File someFile = File.createTempFile("testgridrenderer", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.GridRenderer");
        testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "cellpixels" , "2000" ) );
        params.add( new KeyValueImp( "mapoverlay" , "false" ) );
        params.add( new KeyValueImp( "max_value" , "3.1" ) );
        params.add( new KeyValueImp( "min_value" , "1.2" ) );

        testVsensorConfig.setMainClassInitialParams( params );

        
        sm.executeCreateTable("testgridrenderer", fields, true);
    }

    @After
	public void teardown() throws SQLException {
		sm.executeDropTable("testgridrenderer");
	}

    @Test
    public void testInitialize(){
        GridRenderer vs = new GridRenderer();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
        Double[][] grid = {
            {1.0, 2.0},
            {3.0, 4.0},
            {3.0, 4.0},
            {3.0, 4.0},
            {3.0, 4.0},
            {3.0, 4.0},
            {3.0, 4.0},
            {3.0, 4.0},
            {3.0, 4.0},
            {3.0, 4.0},
        };
        byte[] serializedGrid = serialize(grid);

        String[] fieldnames = {"ncols","nrows","xllcorner","yllcorner","cellsize","grid"};
        StreamElement streamElement1 = new StreamElement(
            fieldnames,
            new Byte[]{DataTypes.INTEGER,DataTypes.INTEGER,DataTypes.DOUBLE,DataTypes.DOUBLE,DataTypes.DOUBLE,DataTypes.BINARY},
            new Serializable[]{1,2,33.54,42.274,41.8,serializedGrid},
            System.currentTimeMillis()+10
        );

        vs.dataAvailable("input", streamElement1);
        vs.dispose();
    }


    @Test
    public void testInitialize2() {
        GridRenderer vs = new GridRenderer();
        params.set(1, new KeyValueImp("mapoverlay", "true"));
        testVsensorConfig.setMainClassInitialParams(params);
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
    
        int ncols = 1000;
        int nrows = 1000;
    
        Double[][] grid = new Double[nrows][ncols];
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                grid[i][j] = 1.8; 
            }
        }
    
        byte[] serializedGrid = serialize(grid);
    
        String[] fieldnames = {"ncols", "nrows", "xllcorner", "yllcorner", "cellsize", "grid"};
        StreamElement streamElement1 = new StreamElement(
                fieldnames,
                new Byte[]{DataTypes.INTEGER, DataTypes.INTEGER, DataTypes.DOUBLE, DataTypes.DOUBLE, DataTypes.DOUBLE, DataTypes.BINARY},
                new Serializable[]{ncols, nrows,1000.0, 1000.0, 1000.0, serializedGrid},
                System.currentTimeMillis() + 10
        );
    
        vs.dataAvailable("input", streamElement1);
        vs.dispose();
    }

    private static byte[] serialize(Serializable object) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(object);
            oos.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
