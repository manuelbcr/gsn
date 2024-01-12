package ch.epfl.gsn.vsensor;

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
import static org.junit.Assert.fail;

import ch.epfl.gsn.Main;
import ch.epfl.gsn.Mappings;
import ch.epfl.gsn.VirtualSensor;
import ch.epfl.gsn.beans.AddressBean;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.InputStream;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.StreamSource;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.KeyValueImp;
import ch.epfl.gsn.wrappers.AbstractWrapper;
import ch.epfl.gsn.wrappers.SystemTime;
import ch.epfl.gsn.wrappers.general.SerialWrapper;

public class EPuckVSTest {

    private static StorageManager sm;
    private VSensorConfig testVsensorConfig;
    private AbstractWrapper wrapper = new SystemTime();

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
            new DataField("raw_packet", DataTypes.BINARY),
        };
        wrapper.setActiveAddressBean(new AddressBean("system-time"));
        assertTrue(wrapper.initialize());
        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("testEPuckVS");
		File someFile = File.createTempFile("testEPuckVS", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.EPuckVS");
		testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);

        ArrayList < KeyValue > params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "raw_packet" , "binary" ) );
        testVsensorConfig.setMainClassInitialParams( params );

        InputStream is = new InputStream();
		is.setInputStreamName("input1");
		StreamSource ss1 = new StreamSource().setAlias("source1").setAddressing(new AddressBean[] {new AddressBean("system-time")}).setSqlQuery("select * from wrapper").setRawHistorySize("2").setInputStream(is);	
        ss1.setWrapper(wrapper);
		ss1.setSamplingRate(1);
		assertTrue(ss1.validate());
		is.setSources(ss1);
		assertTrue(is.validate());
		testVsensorConfig.setInputStreams(is);
		assertTrue(testVsensorConfig.validate());
        sm.executeCreateTable("testEPuckVS", fields, true);
    }

	@After
	public void teardown() throws SQLException {
		sm.executeDropTable("testEPuckVS");
	}

    @Test
    public void testInitialize() {
        EPuckVS vs = new EPuckVS();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
        StreamElement streamElement1 = new StreamElement(
            new String[]{"raw_packet"},
            new Byte[]{DataTypes.BINARY},
            new Serializable[]{new byte[] {1, 2, 3}},
            System.currentTimeMillis()+200);
        try{
            vs.dataAvailable("input1", streamElement1);
            fail("exception expected");
        } catch(Exception e){
            
        }
        try{
            vs.dataAvailable("input1", streamElement1);
            fail("exception expected");
        } catch(Exception e){
        }
    }

}
