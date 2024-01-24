package ch.epfl.gsn;

import static org.junit.Assert.assertNotEquals;
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
import ch.epfl.gsn.networking.zeromq.ZeroMQDeliverySync;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.KeyValueImp;

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
		sm = StorageManagerFactory.getInstance( "org.h2.Driver","sa","" ,"jdbc:h2:mem:coreTest", Main.DEFAULT_MAX_DB_CONNECTIONS);

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

        sm.executeInsert("bridgevsname", fields, streamElement1);
    }

	@After
	public void teardown() throws SQLException {
		sm.executeDropTable("bridgevsname");
	}


    @Test
    public void testDataDistributer() throws IOException, SQLException{
        
        long startTime = System.currentTimeMillis();
        ZeroMQDeliverySync d = new ZeroMQDeliverySync("bridgevsname", "tcp://127.0.0.1:50030");
        DataDistributer datadist= DataDistributer.getInstance(d.getClass());
        
        final DefaultDistributionRequest distributionReq = DefaultDistributionRequest.create(d,testVsensorConfig, "select * from bridgevsname" , startTime);
        datadist.addListener(distributionReq);
    }
    
}
