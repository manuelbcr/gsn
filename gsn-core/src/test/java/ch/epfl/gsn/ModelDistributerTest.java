package ch.epfl.gsn;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.delivery.DefaultDistributionRequest;
import ch.epfl.gsn.delivery.ModelDistributionRequest;
import ch.epfl.gsn.networking.zeromq.ZeroMQDeliverySync;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.KeyValueImp;
import ch.epfl.gsn.utils.models.AbstractModel;
import ch.epfl.gsn.utils.models.DummyModel;

public class ModelDistributerTest {

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
		testVsensorConfig.setName("modeldistributertest");
		File someFile = File.createTempFile("modeldistributertest", ".xml");
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
        
        sm.executeCreateTable("modeldistributertest", fields, true);
    
        StreamElement streamElement1 = new StreamElement(
                new String[]{"device_id", "generation_time","position","sensortype","sensortype_serialid"},
                new Byte[]{DataTypes.INTEGER, DataTypes.BIGINT,DataTypes.INTEGER,DataTypes.VARCHAR,DataTypes.BIGINT},
                new Serializable[]{3, 4L,55,"test",123456721356789L},
                System.currentTimeMillis());

        sm.executeInsert("modeldistributertest", fields, streamElement1);
    }

	@After
	public void teardown() throws SQLException {
		sm.executeDropTable("modeldistributertest");
	}

    @Test
    public void testModelDistributer() throws IOException, SQLException{
        long startTime = System.currentTimeMillis();
        ZeroMQDeliverySync d = new ZeroMQDeliverySync("modeldistributertest", "tcp://127.0.0.1:50130");
        ModelDistributer modeldist= ModelDistributer.getInstance(d.getClass());
        assertNotNull(modeldist);
        final ModelDistributionRequest distributionReq = ModelDistributionRequest.create(d,testVsensorConfig, "select * from modeldistributertest where device_id = 1 and generation_time = 3" , new DummyModel());
    
        modeldist.addListener(distributionReq);
        assertTrue(modeldist.contains(d));
        assertTrue(modeldist.vsLoading(testVsensorConfig));
        assertTrue(modeldist.vsUnLoading(testVsensorConfig));
        modeldist.removeListener(distributionReq);
        assertFalse(modeldist.contains(d));
        modeldist.addListener(distributionReq);
        StreamElement streamElement = new StreamElement(
            new String[]{"device_id", "generation_time","position","sensortype","sensortype_serialid"},
            new Byte[]{DataTypes.INTEGER, DataTypes.BIGINT,DataTypes.INTEGER,DataTypes.VARCHAR,DataTypes.BIGINT},
            new Serializable[]{1, 4234213L,55,"test",123456721356789L},
            System.currentTimeMillis());
        modeldist.consume(streamElement, testVsensorConfig);
        d.close();
        modeldist.release();
        
    }
}
