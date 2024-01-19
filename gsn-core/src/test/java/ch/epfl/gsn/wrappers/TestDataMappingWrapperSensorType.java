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
public class TestDataMappingWrapperSensorType {
    
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
        sm = StorageManagerFactory.getInstance( "org.h2.Driver","sa","" ,"jdbc:h2:mem:coreTest", Main.DEFAULT_MAX_DB_CONNECTIONS);
	}

    @Before
	public void setup() throws SQLException, IOException {

        DataField[] sensorStructure = new DataField[] {
            new DataField("POSITION", DataTypes.INTEGER),
            new DataField("BEGIN", DataTypes.BIGINT),
            new DataField("END", DataTypes.BIGINT),
            new DataField("SENSORTYPE", DataTypes.VARCHAR),
            new DataField("SENSORTYPE_ARGS", DataTypes.BIGINT),
            new DataField("COMMENT", DataTypes.VARCHAR)
        };

        testVsensorConfig = new VSensorConfig();
        testVsensorConfig.setName("sensormapping");
        File someFile = File.createTempFile("sensormapping", ".xml");
        testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.BridgeVirtualSensorPermasense");
        testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(sensorStructure);

        VirtualSensor pool2 = new VirtualSensor(testVsensorConfig);
        Mappings.addVSensorInstance(pool2);

        sm.executeCreateTable("sensormapping", sensorStructure, true);

        StreamElement streamElement = new StreamElement(
                    new String[]{"POSITION", "BEGIN", "END", "SENSORTYPE", "SENSORTYPE_ARGS", "COMMENT"},
                    new Byte[]{DataTypes.INTEGER, DataTypes.BIGINT, DataTypes.BIGINT, DataTypes.VARCHAR, DataTypes.BIGINT, DataTypes.VARCHAR},
                    new Serializable[]{1, 123456L, 234567L, "type1", 12L, "postion 1"},
                    System.currentTimeMillis());
            
        sm.executeInsert("sensormapping", sensorStructure, streamElement);

        //initialize wrapper
        wrapper = new DataMappingWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp( "mapping-type" ,"sensor" ) );
        AddressBean ab = new AddressBean("data-mapping",predicates.toArray(new KeyValueImp[] {}));

        ab.setVirtualSensorConfig(testVsensorConfig);
        ab.setVirtualSensorName("sensormapping");

        wrapper.setActiveAddressBean(ab);

        assertTrue(wrapper.initialize());

    }

    @After
	public void teardown() throws SQLException { 
        wrapper.dispose();
        sm.executeDropTable("sensormapping");
	}

    @AfterClass
	public static void teardownAfterClass() throws Exception {
        sm.shutdown();
    }


    @Test
    public void testDataMappingWrapperSensorType() {

        // sensortype tests
        assertNull(wrapper.getSensorType(1, 123457L, "deployment", "vsname", "streamname"));
        assertNotNull(wrapper.getSensorType(1, 123457L, "sensormapping", "vsname", "streamname"));

        String action_no_comment = "sensor";
        String[] param_names_no_comment = {"position", "begin", "end", "sensortype", "sensortype_args"};
        Serializable[] param_values_no_comment = {"2", "01/01/2020 08:00:00", "01/01/2021 08:00:00", "sensortype", "12"};

        assertTrue(wrapper.sendToWrapper(action_no_comment, param_names_no_comment, param_values_no_comment).toString().startsWith("comment has to be set"));

        String action = "sensor";
        String[] paramNames = { "position", "begin", "end", "sensortype", "sensortype_args", "comment"};
        Serializable[] paramValues = {"2", "01/01/2020 08:00:00", "01/01/2021 08:00:00", "sensortype", "12", "comment"};

        assertTrue(wrapper.sendToWrapper(action, paramNames, paramValues).toString().startsWith("mapping upload successfull"));

        String action_second = "sensor";
        String[] paramNames_second = { "position", "begin", "end", "sensortype", "sensortype_args", "comment"};
        Serializable[] paramValues_second = {"2", "01/01/2019 08:00:00", "01/01/2020 08:00:00", "sensortype", "12", "comment 2"};

        assertTrue(wrapper.sendToWrapper(action_second, paramNames_second, paramValues_second).toString().startsWith("mapping upload successfull"));

        String action_no_end = "sensor";
        String[] paramNames_no_end = { "position", "begin", "sensortype", "sensortype_args", "comment"};
        Serializable[] paramValues_no_end = {"3", "01/01/2021 08:00:00", "sensortype", "12", "comment 3"};

        assertTrue(wrapper.sendToWrapper(action_no_end, paramNames_no_end, paramValues_no_end).toString().startsWith("mapping upload successfull"));

        String action_no_begin = "sensor";
        String[] paramNames_no_begin = { "position", "end", "sensortype", "sensortype_args", "comment"};
        Serializable[] paramValues_no_begin = {"3", "01/01/2022 08:00:00", "sensortype", "12", "position 3"};

        assertTrue(wrapper.sendToWrapper(action_no_begin, paramNames_no_begin, paramValues_no_begin).toString().startsWith("mapping upload successfull"));

        /* 
        String action_no_test = "sensor";
        String[] paramNames_no_test = { "position", "begin", "sensortype", "sensortype_args", "comment", "physical_signal", "conversion", "input", "value"};
        Serializable[] paramValues_no_test = {"1", "01/01/2023 08:00:00", "sensortype", "12", "position 1", "1", "1", "1", "1"};

        */

        String action_no_test = "sensor";
        String[] paramNames_no_test = { "position", "begin", "sensortype", "sensortype_args", "comment", "signal_name", "physical_signal", "conversion", "input", "value" };
        Serializable[] paramValues_no_test = {"4", "01/01/2023 08:00:00", "sensortype", "12", "position 1", "sensortype", "4", "4", "4", "4"};


        assertTrue(wrapper.sendToWrapper(action_no_test, paramNames_no_test, paramValues_no_test).toString().startsWith("mapping upload successfull"));

        StreamElement streamElement = new StreamElement(
                new String[]{"POSITION", "GENERATION_TIME", "BEGIN", "END", "SENSORTYPE", "SENSORTYPE_ARGS", "COMMENT"},
                new Byte[]{DataTypes.INTEGER,DataTypes.BIGINT, DataTypes.BIGINT, DataTypes.BIGINT, DataTypes.VARCHAR, DataTypes.BIGINT, DataTypes.VARCHAR},
                new Serializable[]{4, 1588629800L, 1588629600L, 1688680800L, "type4", 12L, "postion 4"},
                System.currentTimeMillis());

        /*"SELECT st.physical_signal AS physical_signal, st.conversion AS conversion, st.input as input, CASEWHEN(st.input IS NULL OR sm.sensortype_args IS NULL,NULL,sta.value) as value " +
					"FROM " + deployment + "_sensor AS sm, sensortype AS st, sensortype_args AS sta WHERE sm.position = ? AND ((sm.end is null AND sm.begin <= ?) OR (? BETWEEN sm.begin AND sm.end)) AND sm.sensortype = st.sensortype " +
					"AND st.signal_name = ? AND CASEWHEN(st.input IS NULL OR sm.sensortype_args IS NULL,TRUE,sm.sensortype_args = sta.sensortype_args AND sta.physical_signal = st.physical_signal) LIMIT 1" */


    }


}
