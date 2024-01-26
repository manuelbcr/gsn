package ch.epfl.gsn.delivery.datarequest;

import static org.junit.Assert.assertNotNull;
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


public class DownloadDataTest {
    
    private static StorageManager sm;
    private VSensorConfig testVsensorConfig;

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
            new DataField("name", DataTypes.VARCHAR),
            new DataField("value", DataTypes.DOUBLE)
        };
        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("testvsname");
		File someFile = File.createTempFile("testvsname", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.BridgeVirtualSensor");
		testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);
        KeyValue[] emptyAddressingArray = new KeyValue[0];
        testVsensorConfig.setAddressing(emptyAddressingArray);
        VirtualSensor pool = new VirtualSensor(testVsensorConfig);
        Mappings.addVSensorInstance(pool);

        
        sm.executeCreateTable("testvsname", fields, true);
    
        StreamElement streamElement1 = new StreamElement(
                new String[]{"name", "value"},
                new Byte[]{DataTypes.VARCHAR, DataTypes.DOUBLE},
                new Serializable[]{"xy", 45.5},
                System.currentTimeMillis());
    
        StreamElement streamElement2 = new StreamElement(
                new String[]{"name", "value"},
                new Byte[]{DataTypes.VARCHAR, DataTypes.DOUBLE},
                new Serializable[]{"xy", 45.4},
                System.currentTimeMillis()+1000);
        
        StreamElement streamElement3 = new StreamElement(
                new String[]{"name", "value"},
                new Byte[]{DataTypes.VARCHAR, DataTypes.DOUBLE},
                new Serializable[]{"xy", 45.4},
                System.currentTimeMillis()+2000);

        StreamElement streamElement4 = new StreamElement(
                new String[]{"name", "value"},
                new Byte[]{DataTypes.VARCHAR, DataTypes.DOUBLE},
                new Serializable[]{"xy", 45.4},
                System.currentTimeMillis()+2500); 

        sm.executeInsert("testvsname", fields, streamElement1);
        sm.executeInsert("testvsname", fields, streamElement2);
        sm.executeInsert("testvsname", fields, streamElement3);
        sm.executeInsert("testvsname", fields, streamElement4);
    }

	@After
	public void teardown() throws SQLException {
		sm.executeDropTable("testvsname");
	}

    @Test
    public void testOutputResultWithCSV() throws DataRequestException, IOException {
        Map<String, String[]> requestParameters = new HashMap<>();
        requestParameters.put("vsname", new String[]{"testvsname"});
        requestParameters.put("outputtype", new String[]{"csv"});
        requestParameters.put("delimiter", new String[]{"semicolon"});
        DownloadData downloadData = new DownloadData(requestParameters);
        assertNotNull(downloadData.getQueryBuilder());
        downloadData.process();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        downloadData.outputResult(outputStream);
        String result = outputStream.toString();
        assertTrue(result.contains("# vsname:testvsname"));
        assertTrue(result.contains("# query:select timed"));
        outputStream.close();

    }

    @Test
    public void testOutputResultWithCSV1() throws DataRequestException, IOException {
        Map<String, String[]> requestParameters = new HashMap<>();
        requestParameters.put("vsname", new String[]{"testvsname"});
        requestParameters.put("outputtype", new String[]{"csv"});
        requestParameters.put("delimiter", new String[]{"semicolon"});
        requestParameters.put("sample", new String[]{"true"});
        requestParameters.put("sampling_percentage", new String[]{"50"});
        DownloadData downloadData = new DownloadData(requestParameters);
        assertNotNull(downloadData.getQueryBuilder());
        downloadData.process();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        downloadData.outputResult(outputStream);
        String result = outputStream.toString();
        assertTrue(result.contains("# vsname:testvsname"));
        assertTrue(result.contains("# query:select timed"));


    }


     
    @Test
    public void testOutputResultWithXML() throws DataRequestException, IOException {
        Map<String, String[]> requestParameters = new HashMap<>();
        requestParameters.put("vsname", new String[]{"testvsname"});
        requestParameters.put("outputtype", new String[]{"xml"});
        DownloadData downloadData = new DownloadData(requestParameters);
        assertNotNull(downloadData.getQueryBuilder());
        downloadData.process();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        downloadData.outputResult(outputStream);
        String result = outputStream.toString();
        assertTrue(result.contains("<result>"));
        assertTrue(result.contains("<!-- select timed from testvsname order by timed desc  -->"));
        assertTrue(result.contains("</result>"));


    }

}
