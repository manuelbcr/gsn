package ch.epfl.gsn.wrappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.AfterClass;
import org.junit.Test;

import ch.epfl.gsn.Main;
import ch.epfl.gsn.Mappings;
import ch.epfl.gsn.VirtualSensor;
import ch.epfl.gsn.beans.AddressBean;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;

import ch.epfl.gsn.utils.KeyValueImp;
import java.util.ArrayList;

@Ignore
public class TestDataMappingWrapperPosition {
    
    private static StorageManager sm;
    private VSensorConfig testVsensorConfig;
    private DataMappingWrapper wrapper;

    @BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		// Setup current working directory
        String currentWorkingDir = System.getProperty("user.dir");
		if (!currentWorkingDir.endsWith("/gsn-core/")) {
			String newDirectory = currentWorkingDir + "/gsn-core/";
        	System.setProperty("user.dir", newDirectory);
		}

		DriverManager.registerDriver( new org.h2.Driver( ) );
		//sm = StorageManagerFactory.getInstance( "org.h2.Driver","sa","" ,"jdbc:h2:mem:coreTest", Main.DEFAULT_MAX_DB_CONNECTIONS);
        
		Main.setDefaultGsnConf("/gsn_test.xml");
		Main.getInstance();
        sm = StorageManagerFactory.getInstance( "org.h2.Driver","sa","" ,"jdbc:h2:mem:test", Main.DEFAULT_MAX_DB_CONNECTIONS);
	}

    @Before
	public void setup() throws SQLException, IOException {

        //connection = sm.getConnection();
    
        DataField[] positionStructure = new DataField[] {
            new DataField("DEVICE_ID", DataTypes.INTEGER),
            new DataField("DEVICE_TYPE", DataTypes.SMALLINT),
            new DataField("BEGIN", DataTypes.BIGINT),
            new DataField("END", DataTypes.BIGINT),
            new DataField("POSITION", DataTypes.INTEGER),
            new DataField("COMMENT", DataTypes.VARCHAR)
        };

        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("positionmapping");
		File someFile = File.createTempFile("positionmapping", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.BridgeVirtualSensorPermasense");
		testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(positionStructure);

        VirtualSensor pool = new VirtualSensor(testVsensorConfig);
        Mappings.addVSensorInstance(pool);

        sm.executeCreateTable("positionmapping", positionStructure, true);
    
        StreamElement streamElement = new StreamElement(
                    new String[]{"DEVICE_ID", "DEVICE_TYPE", "BEGIN", "END", "POSITION", "COMMENT"},
                    new Byte[]{DataTypes.INTEGER, DataTypes.SMALLINT, DataTypes.BIGINT, DataTypes.BIGINT, DataTypes.INTEGER, DataTypes.VARCHAR},
                    new Serializable[]{1, (short) 1, 123456L, 234567L, 1, "postion 1"},
                    System.currentTimeMillis());
            
        sm.executeInsert("positionmapping", positionStructure, streamElement);

        //initialize wrapper
        wrapper = new DataMappingWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp( "mapping-type" ,"position" ) );
        AddressBean ab =  new AddressBean("data-mapping",predicates.toArray(new KeyValueImp[] {}));

        ab.setVirtualSensorConfig(testVsensorConfig);
        ab.setVirtualSensorName("positionmapping");

        wrapper.setActiveAddressBean(ab);

        assertTrue(wrapper.initialize());

    }

    @After
	public void teardown() throws SQLException {
        wrapper.dispose();
        sm.executeDropTable("positionmapping");
		
	}

    @AfterClass
	public static void teardownAfterClass() throws Exception {
        sm.shutdown();
    }


    @Test
    public void testDataMappingWrapperPosition() {

        // position tests
        assertNull(wrapper.getPosition(1, 123457L, "deployment", "vsname", "streamname"));
        assertEquals(1, wrapper.getPosition(1, 123457L, "positionmapping", "vsname", "streamname"), 0.0);
        assertNotNull(wrapper.getAllPositions("positionmapping", "vsname", "streamname").get(1));
        
        // device type tests
        assertNull(wrapper.getDeviceType(1, 123457L, "deployment", "vsname", "streamname"));
        assertEquals(1, wrapper.getDeviceType(1, 123457L, "positionmapping", "vsname", "streamname"), 0.0);

        String action_no_comment = "position";
        String[] param_names_no_comment = {"device_id", "begin", "end", "position"};
        Serializable[] param_values_no_comment = {"2", "01/01/2020 08:00:00", "01/01/2021 08:00:00", "2"};

        assertTrue(wrapper.sendToWrapper(action_no_comment, param_names_no_comment, param_values_no_comment).toString().startsWith("comment has to be set"));

        String action = "position";
        String[] paramNames = { "device_id", "device_type", "begin", "end", "position", "comment"};
        Serializable[] paramValues = {"2", "2", "01/01/2020 08:00:00", "01/01/2021 08:00:00", "2", "position 2"};

        assertTrue(wrapper.sendToWrapper(action, paramNames, paramValues).toString().startsWith("mapping upload successfull"));

        String action_second = "position";
        String[] paramNames_second = { "device_id", "device_type", "begin", "end", "position", "comment"};
        Serializable[] paramValues_second = {"2", "2", "01/01/2019 08:00:00", "01/01/2020 08:00:00", "2", "position 2"};

        assertTrue(wrapper.sendToWrapper(action_second, paramNames_second, paramValues_second).toString().startsWith("mapping upload successfull"));

        String action_no_end = "position";
        String[] paramNames_no_end = { "device_id", "device_type", "begin", "position", "comment"};
        Serializable[] paramValues_no_end = {"3", "3", "01/01/2021 08:00:00", "3", "position 3"};

        assertTrue(wrapper.sendToWrapper(action_no_end, paramNames_no_end, paramValues_no_end).toString().startsWith("mapping upload successfull"));

        String action_no_begin = "position";
        String[] paramNames_no_begin = { "device_id", "device_type", "end", "position", "comment"};
        Serializable[] paramValues_no_begin = {"3", "3", "01/01/2022 08:00:00", "3", "position 3"};

        assertTrue(wrapper.sendToWrapper(action_no_begin, paramNames_no_begin, paramValues_no_begin).toString().startsWith("mapping upload successfull"));

        /*
        String action_no_test = "position";
        String[] paramNames_no_test = { "device_id", "device_type", "begin", "position", "comment"};
        Serializable[] paramValues_no_test = {"3", "3", "01/01/2023 08:00:00", "3", "position 3"};

        assertTrue(wrapper.sendToWrapper(action_no_test, paramNames_no_test, paramValues_no_test).toString().startsWith("mapping upload successfull"));

        
        StreamElement streamElement1 = new StreamElement(
                    new String[]{"DEVICE_ID", "GENERATION_TIME", "DEVICE_TYPE", "BEGIN", "END", "POSITION", "COMMENT"},
                    new Byte[]{DataTypes.INTEGER, DataTypes.BIGINT, DataTypes.SMALLINT, DataTypes.BIGINT, DataTypes.BIGINT, DataTypes.INTEGER, DataTypes.VARCHAR},
                    new Serializable[]{3, 1683237600000L, (short) 3, 1588629600000L, 1688680800000L, 3, "postion 3"},
                    System.currentTimeMillis());

        System.out.println("!!!!!!!!!!!!!!" + wrapper.getConvertedValues(streamElement1, "positionmapping", "vsname", "inputstreamname"));
        */
    }

    @Test
    public void testGetConvertedValues(){



    }

}