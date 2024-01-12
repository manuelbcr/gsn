package ch.epfl.gsn.vsensor;

import static org.junit.Assert.assertFalse;
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

import ch.epfl.gsn.Main;
import ch.epfl.gsn.VirtualSensor;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.KeyValueImp;

public class StreamRRDExporterVirtualSensorTest {
    
    private static StorageManager sm;
    private VSensorConfig testVsensorConfig;
    ArrayList < KeyValue > params;

    @BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
            new DataField("value", DataTypes.INTEGER),
            new DataField("value1", DataTypes.INTEGER)
        };

        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("teststreamrrdexporter");
        File someFile = File.createTempFile("teststreamrrdexporter", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.StreamExporterVirtualSensor");
        testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);

        sm.executeCreateTable("teststreamrrdexporter", fields, true);
        

    
       
    }

    @After
	public void teardown() throws SQLException {
		sm.executeDropTable("teststreamrrdexporter");
	}

    @Test
    public void testInitialize() {
        StreamRRDExporterVirtualSensor vs= new StreamRRDExporterVirtualSensor();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
    }

    @Test
    public void testInitialize2() {
        StreamRRDExporterVirtualSensor vs= new StreamRRDExporterVirtualSensor();
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "rrdfile" , "sa" ) );
        params.add( new KeyValueImp( "field", "" ) );
        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
        StreamElement streamElement1 = new StreamElement(
            new String[]{"value", "value1"},
            new Byte[]{DataTypes.INTEGER, DataTypes.INTEGER},
            new Serializable[]{1,2},
            System.currentTimeMillis()+200);
        vs.dataAvailable("inputStream", streamElement1);
        vs.dispose();
    }
}
