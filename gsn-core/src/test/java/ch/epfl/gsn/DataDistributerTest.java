package ch.epfl.gsn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import org.junit.Ignore;

import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.delivery.DefaultDistributionRequest;
import ch.epfl.gsn.networking.zeromq.ZeroMQDeliveryAsync;
import ch.epfl.gsn.networking.zeromq.ZeroMQDeliverySync;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.KeyValueImp;
import ch.epfl.gsn.vsensor.BridgeVirtualSensorPermasense;

@Ignore
public class DataDistributerTest {
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
		testVsensorConfig.setName("datadistributertest");
		File someFile = File.createTempFile("datadistributertest", ".xml");
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
        
        sm.executeCreateTable("datadistributertest", fields, true);
    
        StreamElement streamElement1 = new StreamElement(
                new String[]{"device_id", "generation_time","position","sensortype","sensortype_serialid"},
                new Byte[]{DataTypes.INTEGER, DataTypes.BIGINT,DataTypes.INTEGER,DataTypes.VARCHAR,DataTypes.BIGINT},
                new Serializable[]{3, 4L,55,"test",123456721356789L},
                System.currentTimeMillis());

        sm.executeInsert("datadistributertest", fields, streamElement1);
    }

	@After
	public void teardown() throws SQLException {
		sm.executeDropTable("datadistributertest");
	}


    @Test
    public void testDataDistributer() throws IOException, SQLException, InterruptedException{
        long startTime = System.currentTimeMillis();
        ZeroMQDeliverySync d = new ZeroMQDeliverySync("datadistributertest", "tcp://127.0.0.1:50030");
        DataDistributer datadist= DataDistributer.getInstance(d.getClass());
        assertNotNull(datadist);
        final DefaultDistributionRequest distributionReq = DefaultDistributionRequest.create(d,testVsensorConfig, "select * from datadistributertest" , startTime);
        datadist.addListener(distributionReq);
        assertTrue(datadist.contains(d));
        assertTrue(datadist.vsLoading(testVsensorConfig));
        StreamElement streamElement = new StreamElement(
                new String[]{"device_id", "generation_time","position","sensortype","sensortype_serialid"},
                new Byte[]{DataTypes.INTEGER, DataTypes.BIGINT,DataTypes.INTEGER,DataTypes.VARCHAR,DataTypes.BIGINT},
                new Serializable[]{2, 43L,5544,"test",123452221356789L},
                System.currentTimeMillis());
        datadist.consume(streamElement, testVsensorConfig);
        assertTrue(datadist.vsUnLoading(testVsensorConfig));
        datadist.removeListener(distributionReq);
        assertFalse(datadist.contains(d));
        datadist.addListener(distributionReq);
        assertFalse(d.writeStreamElement(streamElement));
        assertTrue(d.writeKeepAliveStreamElement());
        d.close();
        assertTrue(d.isClosed()); 
        datadist.release();
    }

    @Test
    public void testDataDistributerAsyncZeromq()throws IOException, SQLException, InterruptedException{
        long startTime = System.currentTimeMillis();
        StreamElement streamElement = new StreamElement(
            new String[]{"device_id", "generation_time","position","sensortype","sensortype_serialid"},
            new Byte[]{DataTypes.INTEGER, DataTypes.BIGINT,DataTypes.INTEGER,DataTypes.VARCHAR,DataTypes.BIGINT},
            new Serializable[]{2, 43L,5544,"test",123452221356789L},
            System.currentTimeMillis());
        ZeroMQDeliveryAsync d1= new ZeroMQDeliveryAsync("datadistributertest");
        DataDistributer datadist= DataDistributer.getInstance(d1.getClass());
        assertNotNull(datadist);
        final DefaultDistributionRequest distributionReq = DefaultDistributionRequest.create(d1,testVsensorConfig, "select * from datadistributertest" , startTime);
        datadist.addListener(distributionReq);
        assertTrue(datadist.contains(d1));
        assertTrue(datadist.vsLoading(testVsensorConfig));
        assertTrue(d1.writeStreamElement(streamElement));
        assertTrue(d1.writeKeepAliveStreamElement());
        d1.close();
        assertTrue(d1.isClosed());
        datadist.release();
    }
    
}
