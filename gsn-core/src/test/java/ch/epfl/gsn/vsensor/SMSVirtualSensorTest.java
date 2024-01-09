package ch.epfl.gsn.vsensor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

import org.apache.commons.collections.KeyValue;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.epfl.gsn.Main;
import ch.epfl.gsn.Mappings;
import ch.epfl.gsn.VirtualSensor;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.KeyValueImp;

public class SMSVirtualSensorTest {
    
    private static StorageManager sm;
    private VSensorConfig testVsensorConfig;
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
            new DataField("message", DataTypes.VARCHAR),
            new DataField("phonenumber", DataTypes.VARCHAR),
            new DataField("receiver", DataTypes.VARCHAR)
        };

        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("testsms");
        File someFile = File.createTempFile("testsms", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.SMSVirtualSensor");
        testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);
        ArrayList < KeyValue > params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "phone-number" , "+436999999999" ) );
        params.add( new KeyValueImp( "password", "passwd" ) );
        params.add( new KeyValueImp( "sms-server" ,"server" ) );
        params.add( new KeyValueImp("message-format", "typical") );
        testVsensorConfig.setMainClassInitialParams( params );

        
        sm.executeCreateTable("testsms", fields, true);
    }

    @After
	public void teardown() throws SQLException {
		sm.executeDropTable("testsms");
	}

    @Test
    public void testInitialize(){
        SMSVirtualSensor vs = new SMSVirtualSensor();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());

        StreamElement streamElement1 = new StreamElement(
            new String[]{"message", "phonenumber","receiver"},
            new Byte[]{DataTypes.VARCHAR, DataTypes.VARCHAR,DataTypes.VARCHAR},
            new Serializable[]{"hello","+43699999999","+43699999999"},
            System.currentTimeMillis()+20000);

        

        assertTrue(SMSVirtualSensor.prepareMessage(streamElement1, "Value: $value$, Value1: $value1$").contains("Value"));
        
    
        String[] paramNames = {"phonenumber"};
        String validPhoneNumber = "1234567890";
        Serializable[] paramValues = {validPhoneNumber};
        Serializable[] emptyparamValues = {};


        boolean result = vs.addPhoneNumber(paramNames, paramValues);
        assertTrue(result);
        

        boolean removeResult = vs.removePhoneNumber(paramNames, paramValues);
        assertTrue(removeResult);

        String[] wrongparam = {"wrongphone"};
        assertFalse(vs.addPhoneNumber(wrongparam, paramValues));
        assertFalse(vs.removePhoneNumber(wrongparam, paramValues));
        assertFalse(vs.addPhoneNumber(paramNames, emptyparamValues));
        assertFalse(vs.removePhoneNumber(paramNames, emptyparamValues));

        assertNotNull(vs.getOutputFormat());

        assertTrue(vs.dataFromWeb("add-receiver",paramNames,paramValues));
        assertTrue(vs.dataFromWeb("remove-receiver",paramNames,paramValues));
        assertFalse(vs.dataFromWeb("wrong-action",paramNames,paramValues));
        
        vs.addPhoneNumber(paramNames, paramValues);
        vs.dataAvailable("input",streamElement1);

    }

    @Test
    public void testInitializationFailure() throws IOException{
        DataField[] fields = new DataField[]{
            new DataField("message", DataTypes.VARCHAR),
            new DataField("phonenumber", DataTypes.VARCHAR),
            new DataField("receiver", DataTypes.VARCHAR)
        };

        VSensorConfig testVsensorConfig1 = new VSensorConfig();
		testVsensorConfig1.setName("testsms");
        File someFile = File.createTempFile("testsms1", ".xml");
		testVsensorConfig1.setMainClass("ch.epfl.gsn.vsensor.SMSVirtualSensor");
        testVsensorConfig1.setFileName(someFile.getAbsolutePath());
        testVsensorConfig1.setOutputStructure(fields);
        ArrayList < KeyValue > params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "phone-number" , "+436999999999" ) );
        params.add( new KeyValueImp( "sms-server" ,"server" ) );
        params.add( new KeyValueImp("message-format", "typical") );
        testVsensorConfig1.setMainClassInitialParams( params );
        SMSVirtualSensor vs = new SMSVirtualSensor();
        vs.setVirtualSensorConfiguration(testVsensorConfig1);
        assertFalse(vs.initialize());




        VSensorConfig testVsensorConfig2 = new VSensorConfig();
		testVsensorConfig2.setName("testsms");
        File someFile1 = File.createTempFile("testsms2", ".xml");
		testVsensorConfig2.setMainClass("ch.epfl.gsn.vsensor.SMSVirtualSensor");
        testVsensorConfig2.setFileName(someFile1.getAbsolutePath());
        testVsensorConfig2.setOutputStructure(fields);
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "phone-number" , "+436999999999" ) );
        params.add( new KeyValueImp( "password", "passwd" ) );
        params.add( new KeyValueImp("message-format", "typical") );
        testVsensorConfig2.setMainClassInitialParams( params );

        SMSVirtualSensor vs1 = new SMSVirtualSensor();
        vs1.setVirtualSensorConfiguration(testVsensorConfig2);
        assertFalse(vs1.initialize());



        VSensorConfig testVsensorConfig3 = new VSensorConfig();
		testVsensorConfig3.setName("testsms");
        File someFile2 = File.createTempFile("testsms3", ".xml");
		testVsensorConfig3.setMainClass("ch.epfl.gsn.vsensor.SMSVirtualSensor");
        testVsensorConfig3.setFileName(someFile2.getAbsolutePath());
        testVsensorConfig3.setOutputStructure(fields);
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "phone-number" , "+436999999999" ) );
        params.add( new KeyValueImp( "password", "passwd" ) );
        params.add( new KeyValueImp( "sms-server" ,"server" ) );
        testVsensorConfig3.setMainClassInitialParams( params );

        SMSVirtualSensor vs2 = new SMSVirtualSensor();
        vs2.setVirtualSensorConfiguration(testVsensorConfig3);
        assertFalse(vs2.initialize());
    
    }
        
}
