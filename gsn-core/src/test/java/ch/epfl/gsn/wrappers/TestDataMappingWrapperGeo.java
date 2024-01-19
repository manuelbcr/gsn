package ch.epfl.gsn.wrappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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
import ch.epfl.gsn.beans.StreamSource;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import org.apache.commons.collections.KeyValue;
import ch.epfl.gsn.utils.KeyValueImp;
import ch.epfl.gsn.beans.InputStream;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

@Ignore
public class TestDataMappingWrapperGeo {
    
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

        DataField[] geoStructure = new DataField[] {
                new DataField("POSITION", DataTypes.INTEGER),
                new DataField("LONGITUDE", DataTypes.DOUBLE),
                new DataField("LATITUDE", DataTypes.DOUBLE),
                new DataField("ALTITUDE", DataTypes.DOUBLE),
                new DataField("COMMENT", DataTypes.VARCHAR)
        };

        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("geomapping");
		File someFile = File.createTempFile("geomapping", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.BridgeVirtualSensorPermasense");
		testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(geoStructure);

        VirtualSensor pool1 = new VirtualSensor(testVsensorConfig);
        Mappings.addVSensorInstance(pool1);

        sm.executeCreateTable("geomapping", geoStructure, true);
    
        StreamElement streamElement = new StreamElement(
                    new String[]{"POSITION", "LONGITUDE", "LATITUDE", "ALTITUDE", "COMMENT"},
                    new Byte[]{DataTypes.INTEGER, DataTypes.DOUBLE, DataTypes.DOUBLE, DataTypes.DOUBLE, DataTypes.VARCHAR},
                    new Serializable[]{1, 10.5, 10.5, 10.5, "postion 1"},
                    System.currentTimeMillis());
            
        sm.executeInsert("geomapping", geoStructure, streamElement);

        //initialize wrapper
        wrapper = new DataMappingWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp( "mapping-type" ,"geo" ) );
        AddressBean ab = new AddressBean("data-mapping",predicates.toArray(new KeyValueImp[] {}));

        ab.setVirtualSensorConfig(testVsensorConfig);
        ab.setVirtualSensorName("geomapping");

        wrapper.setActiveAddressBean(ab);

        assertTrue(wrapper.initialize());

    }

    @After
	public void teardown() throws SQLException {
        wrapper.dispose();
        sm.executeDropTable("geomapping");
	}

    @AfterClass
	public static void teardownAfterClass() throws Exception {
        sm.shutdown();
    }


    @Test
    public void testDataMappingWrapperGeo() {

        assertNull(wrapper.getCoordinate(1, "deployment", "vsname", "streamname"));
        assertEquals("(10.5, 10.5, 10.5)", wrapper.getCoordinate(1, "geomapping", "vsname", "streamname").toString());

        String action = "geo";
        String[] param_names = {"position", "longitude", "latitude", "altitude", "comment"};
        Serializable[] param_values = {"1", "10.5", "10.5", "10.5", "comment 1"};

        assertTrue(wrapper.sendToWrapper(action, param_names, param_values).toString().startsWith("mapping upload successfull"));

    }

}