package ch.epfl.gsn.vsensor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

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

public class BridgeVirtualSensorPermasenseTest {
    
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
            new DataField("device_id", DataTypes.INTEGER),
            new DataField("generation_time", DataTypes.BIGINT),
            new DataField("position", DataTypes.INTEGER),
            new DataField("sensortype", DataTypes.VARCHAR),
            new DataField("sensortype_serialid",DataTypes.BIGINT)
        };
        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("bridgevsname");
		File someFile = File.createTempFile("bridgevsname", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.BridgeVirtualSensorPermasense");
		testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "position_mapping","true") );
        params.add( new KeyValueImp( "sensortype_mapping","true") );
        params.add( new KeyValueImp( "sensorvalue_conversion","true") );
        params.add( new KeyValueImp( "gps_time_conversion","true") );
        params.add( new KeyValueImp( "jpeg_scaled","JPEG_SCALED") );
        testVsensorConfig.setMainClassInitialParams( params );
        KeyValue[] emptyAddressingArray = new KeyValue[0];
        testVsensorConfig.setAddressing(emptyAddressingArray);
        pool = new VirtualSensor(testVsensorConfig);
        assertNotEquals(-1, pool.getLastModified());
        Mappings.addVSensorInstance(pool);
        



        
        sm.executeCreateTable("bridgevsname", fields, true);
    
        StreamElement streamElement1 = new StreamElement(
                new String[]{"device_id", "generation_time","position","sensortype","sensortype_serialid"},
                new Byte[]{DataTypes.INTEGER, DataTypes.BIGINT,DataTypes.INTEGER,DataTypes.VARCHAR,DataTypes.BIGINT},
                new Serializable[]{3, 4L,55,"test",123456721356789L},
                System.currentTimeMillis());
    
        StreamElement streamElement2 = new StreamElement(
                new String[]{"device_id", "generation_time","position","sensortype","sensortype_serialid"},
                new Byte[]{DataTypes.INTEGER, DataTypes.BIGINT,DataTypes.INTEGER,DataTypes.VARCHAR,DataTypes.BIGINT},
                new Serializable[]{4, 5L,65,"test",1234564123123456789L},
                System.currentTimeMillis()+1000);
        
        StreamElement streamElement3 = new StreamElement(
                new String[]{"device_id", "generation_time","position","sensortype","sensortype_serialid"},
                new Byte[]{DataTypes.INTEGER, DataTypes.BIGINT,DataTypes.INTEGER,DataTypes.VARCHAR,DataTypes.BIGINT},
                new Serializable[]{5, 45L,56,"test",123456789412456789L},
                System.currentTimeMillis()+2000);

        StreamElement streamElement4 = new StreamElement(
                new String[]{"device_id", "generation_time","position","sensortype","sensortype_serialid"},
                new Byte[]{DataTypes.INTEGER, DataTypes.BIGINT,DataTypes.INTEGER,DataTypes.VARCHAR,DataTypes.BIGINT},
                new Serializable[]{6, 46L,45,"test",1234567842133456789L},
                System.currentTimeMillis()+2500); 

        sm.executeInsert("bridgevsname", fields, streamElement1);
        sm.executeInsert("bridgevsname", fields, streamElement2);
        sm.executeInsert("bridgevsname", fields, streamElement3);
        sm.executeInsert("bridgevsname", fields, streamElement4);
    }

	@After
	public void teardown() throws SQLException {
		sm.executeDropTable("bridgevsname");
        pool.dispose();
        pool.closePool();
	}


    @Test
    public void testInitialize() {
         StreamElement streamElement1 = new StreamElement(
            new String[]{"device_id", "generation_time","position","sensortype","sensortype_serialid"},
            new Byte[]{DataTypes.INTEGER, DataTypes.BIGINT,DataTypes.INTEGER,DataTypes.VARCHAR,DataTypes.BIGINT},
            new Serializable[]{5, 444L,88,"test",1221389L},
            System.currentTimeMillis());

        BridgeVirtualSensorPermasense sensor = new BridgeVirtualSensorPermasense();
        sensor.setVirtualSensorConfiguration(testVsensorConfig);

        assertTrue(sensor.initialize());
        sensor.dataAvailable("inputStream", streamElement1);
        StreamElement wrongStreamElement = new StreamElement(
            new String[]{"device_id", "generation_time","position","sensortype","sensortype_serialid","wrong"},
            new Byte[]{DataTypes.INTEGER, DataTypes.BIGINT,DataTypes.INTEGER,DataTypes.VARCHAR,DataTypes.BIGINT,DataTypes.VARCHAR},
            new Serializable[]{6, 561L,321,"test",5446541645646L,"wrong"},
            System.currentTimeMillis());
        sensor.dataProduced(wrongStreamElement,false);
        assertTrue(sensor.initialize_wrapper());
        Hashtable<String, Object> statistics = sensor.getStatistics();
        //assertEquals(0L,statistics.get("vs.bridgevsname.output.produced.counter"));
        assertEquals(0L,statistics.get("vs.bridgevsname.input.produced.counter"));
        assertNotNull(sensor.getThreads());
        assertFalse(sensor.dataFromWeb("", new String[1], new Serializable[1]));
        
    }


    @Test
    public void testDataAvailableAllfieldsNull(){
        StreamElement streamElement1 = new StreamElement(
            new String[]{"device_id", "generation_time","position","sensortype","sensortype_serialid"},
            new Byte[]{DataTypes.INTEGER, DataTypes.BIGINT,DataTypes.INTEGER,DataTypes.VARCHAR,DataTypes.BIGINT},
            new Serializable[]{7, 5646512L,523,"test",123456784212L},
            System.currentTimeMillis());

        BridgeVirtualSensor sensor = new BridgeVirtualSensor();
        sensor.setVirtualSensorConfiguration(testVsensorConfig);

        assertTrue(sensor.initialize());
        sensor.dataAvailable("inputStream", streamElement1);
        assertFalse(sensor.areAllFieldsNull(streamElement1));
        sensor.dispose();
    }


    @Test
    public void testInitialize1(){
        BridgeVirtualSensorPermasense sensor = new BridgeVirtualSensorPermasense();
        sensor.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(sensor.initialize());


         StreamElement streamElement1 = new StreamElement(
            new String[]{"device_id", "generation_time","position","sensortype","sensortype_serialid"},
            new Byte[]{DataTypes.INTEGER, DataTypes.BIGINT,DataTypes.INTEGER,DataTypes.VARCHAR,DataTypes.BIGINT},
            new Serializable[]{7, 5646512L,2541,"TEST",5415132L},
            System.currentTimeMillis());

    
        sensor.dataAvailable("inputStream", streamElement1);
        try {
            StreamElement streamElement2 = new StreamElement(
                    new String[]{"generation_time","position","sensortype","sensortype_serialid"},
                    new Byte[]{ DataTypes.BIGINT,DataTypes.INTEGER,DataTypes.VARCHAR,DataTypes.BIGINT},
                    new Serializable[]{5646512L,2541,"TEST",45665465L},
                    System.currentTimeMillis());
            sensor.dataAvailable("input", streamElement2);

            fail("Expected NullPointerException, but no exception was thrown.");
        } catch (Exception e) {
           System.out.println(e.getMessage());
            
        }


        StreamElement streamElement3 = new StreamElement(
            new String[]{"gps_time","gps_week"},
            new Byte[]{ DataTypes.INTEGER,DataTypes.SMALLINT},
            new Serializable[]{2460319, (short)2296},
            System.currentTimeMillis());
        sensor.dataAvailable("input", streamElement3);

        

        try {
        byte[] jpegImageBytes = generateRandomJpegImageBytes(1024,1024);
        StreamElement image= new StreamElement(
                new String[]{"JPEG_SCALED"},
                new Byte[]{DataTypes.BINARY},
                new Serializable[]{jpegImageBytes},
                System.currentTimeMillis());
        sensor.dataAvailable("input", image);
        sensor.dispose();

            
        } catch (Exception e) {
           fail("Expected no exception, but exception was thrown." + e.getMessage());
            
        }
    }





    //IMAGE generation
    public static byte[] generateRandomJpegImageBytes(int width, int height) {
        try {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            
            // Generate random pixel values
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = getRandomRgb();
                    image.setRGB(x, y, rgb);
                }
            }

            // Convert BufferedImage to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpeg", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int getRandomRgb() {
        int r = (int) (Math.random() * 256);
        int g = (int) (Math.random() * 256);
        int b = (int) (Math.random() * 256);
        return (r << 16) | (g << 8) | b;
    }
    
}


