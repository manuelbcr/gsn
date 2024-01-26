package ch.epfl.gsn.vsensor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.collections.KeyValue;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ch.epfl.gsn.Main;
import ch.epfl.gsn.Mappings;
import ch.epfl.gsn.VirtualSensor;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.DeviceMappings;
import ch.epfl.gsn.beans.GeoMapping;
import ch.epfl.gsn.beans.PositionMap;
import ch.epfl.gsn.beans.PositionMappings;
import ch.epfl.gsn.beans.SensorMap;
import ch.epfl.gsn.beans.SensorMappings;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.KeyValueImp;

public class MappingVirtualSensorTest {
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
            new DataField("DATA", DataTypes.BINARY),
            new DataField("device_id", DataTypes.INTEGER),
            new DataField("device_type",DataTypes.SMALLINT),
            new DataField("generation_time", DataTypes.BIGINT),
            new DataField("position", DataTypes.INTEGER),
            new DataField("sensortype", DataTypes.VARCHAR),
            new DataField("sensortype_args",DataTypes.BIGINT),
            new DataField("latitude", DataTypes.DOUBLE),
            new DataField("longitude", DataTypes.DOUBLE),
            new DataField("altitude", DataTypes.DOUBLE),
            new DataField("begin", DataTypes.BIGINT),
            new DataField("end", DataTypes.BIGINT),
            new DataField("comment", DataTypes.VARCHAR),

        };
        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("devicemappingvs");
		File someFile = File.createTempFile("devicemappingvs", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.MappingVirtualSensor");
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
        VirtualSensor pool = new VirtualSensor(testVsensorConfig);
        Mappings.addVSensorInstance(pool);
        
        GeoMapping geoMapping = new GeoMapping(1, 10.5, 11.5, 12.5, "comment");
        assertNotNull(geoMapping);

        SensorMap sensorMap1 = new SensorMap(123L, null, "sensortype", 456L, "comment");
        ArrayList<SensorMap> sMappings = new ArrayList<>();
        sMappings.add(sensorMap1);
        SensorMappings sensorMapping = new SensorMappings(1, sMappings);
        assertNotNull(sensorMapping);

        PositionMap positionMap1 = new PositionMap(1, (short) 1, 123L, null, "comment");
        ArrayList<PositionMap> pMappings = new ArrayList<>();
        pMappings.add(positionMap1);

        PositionMappings positionMapping = new PositionMappings(1, pMappings);
        assertNotNull(positionMapping);

        ArrayList<PositionMappings> positionMappings = new ArrayList<>();
        positionMappings.add(positionMapping);
        ArrayList<SensorMappings> sensorMappings = new ArrayList<>();
        sensorMappings.add(sensorMapping);
        ArrayList<GeoMapping> geoMappings = new ArrayList<>();
        geoMappings.add(geoMapping);

        DeviceMappings deviceMappings = new DeviceMappings(positionMappings, sensorMappings, geoMappings);
        assertNotNull(deviceMappings);

        sm.executeCreateTable("devicemappingvs", fields, true);
        byte[] exampleBinaryData = { 0x48, 0x65, 0x6C, 0x6C, 0x6F };
        StreamElement streamElement1 = new StreamElement(
                new String[]{"DATA","device_id", "device_type", "generation_time","position","sensortype","sensortype_args","latitude","longitude","altitude","begin","end","comment"},
                new Byte[]{DataTypes.BINARY,DataTypes.INTEGER, DataTypes.SMALLINT,DataTypes.BIGINT,DataTypes.INTEGER,DataTypes.VARCHAR,DataTypes.BIGINT,DataTypes.DOUBLE,DataTypes.DOUBLE,DataTypes.DOUBLE,DataTypes.BIGINT,DataTypes.BIGINT,DataTypes.VARCHAR},
                new Serializable[]{exampleBinaryData,3,(short)2, 4L,55,"test",123456721356789L,45.0,45.0,45.0,123456789123456789L,123456789123456789L,"test"},
                System.currentTimeMillis());
    
        StreamElement streamElement2 = new StreamElement(
            new String[]{"DATA","device_id", "device_type", "generation_time","position","sensortype","sensortype_args","latitude","longitude","altitude","begin","end","comment"},
            new Byte[]{DataTypes.BINARY,DataTypes.INTEGER, DataTypes.SMALLINT,DataTypes.BIGINT,DataTypes.INTEGER,DataTypes.VARCHAR,DataTypes.BIGINT,DataTypes.DOUBLE,DataTypes.DOUBLE,DataTypes.DOUBLE,DataTypes.BIGINT,DataTypes.BIGINT,DataTypes.VARCHAR},
                new Serializable[]{exampleBinaryData,4,(short)1,5L,65,"test",1234564123123456789L,45.0,45.0,45.0,123456789123456789L,123456789123456789L,"test"},
                System.currentTimeMillis()+1000);
        
        StreamElement streamElement3 = new StreamElement(
            new String[]{"DATA","device_id", "device_type", "generation_time","position","sensortype","sensortype_args","latitude","longitude","altitude","begin","end","comment"},
            new Byte[]{DataTypes.BINARY,DataTypes.INTEGER, DataTypes.SMALLINT,DataTypes.BIGINT,DataTypes.INTEGER,DataTypes.VARCHAR,DataTypes.BIGINT,DataTypes.DOUBLE,DataTypes.DOUBLE,DataTypes.DOUBLE,DataTypes.BIGINT,DataTypes.BIGINT,DataTypes.VARCHAR},
                new Serializable[]{exampleBinaryData,5,(short)4, 45L,56,"test",123456789412456789L,45.0,45.0,45.0,123456789123456789L,123456789123456789L,"test"},
                System.currentTimeMillis()+2000);

        StreamElement streamElement4 = new StreamElement(
            new String[]{"DATA","device_id", "device_type", "generation_time","position","sensortype","sensortype_args","latitude","longitude","altitude","begin","end","comment"},
            new Byte[]{DataTypes.BINARY,DataTypes.INTEGER, DataTypes.SMALLINT,DataTypes.BIGINT,DataTypes.INTEGER,DataTypes.VARCHAR,DataTypes.BIGINT,DataTypes.DOUBLE,DataTypes.DOUBLE,DataTypes.DOUBLE,DataTypes.BIGINT,DataTypes.BIGINT,DataTypes.VARCHAR},
                new Serializable[]{exampleBinaryData,6, (short)2,46L,45,"test",1234567842133456789L, 45.0,45.0,45.0,123456789123456789L,123456789123456789L,"test"},
                System.currentTimeMillis()+2500); 

        sm.executeInsert("devicemappingvs", fields, streamElement1);
        sm.executeInsert("devicemappingvs", fields, streamElement2);
        sm.executeInsert("devicemappingvs", fields, streamElement3);
        sm.executeInsert("devicemappingvs", fields, streamElement4);
    }


	@After
	public void teardown() throws SQLException {
		sm.executeDropTable("devicemappingvs");
	}

    @Test
    public void testInitialize() {
        MappingVirtualSensor vs = new MappingVirtualSensor();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
        byte[] exampleBinaryData = { 0x48, 0x65, 0x6C, 0x6C, 0x6F };
        StreamElement streamElement = new StreamElement(
            new String[]{"DATA","device_id", "device_type", "generation_time","position","sensortype","sensortype_args","latitude","longitude","altitude","begin","end","comment"},
            new Byte[]{DataTypes.BINARY,DataTypes.INTEGER, DataTypes.SMALLINT,DataTypes.BIGINT,DataTypes.INTEGER,DataTypes.VARCHAR,DataTypes.BIGINT,DataTypes.DOUBLE,DataTypes.DOUBLE,DataTypes.DOUBLE,DataTypes.BIGINT,DataTypes.BIGINT,DataTypes.VARCHAR},
            new Serializable[]{exampleBinaryData,1, (short)2,43412L,443,"test",1234564123456789L,45.0,45.0,45.0,123456789123456789L,123456789123456789L,"test"},
            System.currentTimeMillis()+2500); 
            vs.dataAvailable("position_mapping", streamElement);
            vs.dataAvailable("sensor_mapping", streamElement);
            vs.dataAvailable("geo_mapping", streamElement);
    }
}
