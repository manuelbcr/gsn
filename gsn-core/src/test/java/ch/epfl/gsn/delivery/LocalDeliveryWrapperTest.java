package ch.epfl.gsn.delivery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import javax.naming.OperationNotSupportedException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ch.epfl.gsn.Main;
import ch.epfl.gsn.Mappings;
import ch.epfl.gsn.VirtualSensor;
import ch.epfl.gsn.VirtualSensorInitializationFailedException;
import ch.epfl.gsn.beans.AddressBean;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.KeyValueImp;
import ch.epfl.gsn.vsensor.AbstractVirtualSensor;
import thredds.inventory.bdb.MetadataManager.KeyValue;

@Ignore
public class LocalDeliveryWrapperTest {

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
		sm = StorageManagerFactory.getInstance( "org.h2.Driver","sa","" ,"jdbc:h2:mem:coreTest", Main.DEFAULT_MAX_DB_CONNECTIONS);

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
    public void testToString() throws SQLException, IOException, OperationNotSupportedException{
        LocalDeliveryWrapper localwrapper= new LocalDeliveryWrapper();
        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp( "name" ,"testvsname" ) );
        predicates.add( new KeyValueImp( "start-time" ,"continue" ) );
        AddressBean addressbean= new AddressBean("Local-wrapper",predicates.toArray(new KeyValueImp[] {}));
        addressbean.setVirtualSensorName("testvsname");
        localwrapper.setActiveAddressBean(addressbean);
        

        localwrapper.writeStructure(new DataField[] {new DataField("data","int")});
        assertNotNull(localwrapper.getOutputFormat());
        assertTrue(localwrapper.writeKeepAliveStreamElement());
        assertTrue(localwrapper.writeStreamElement(new StreamElement(new DataField[] {},new Serializable[] {},System.currentTimeMillis())));
        assertNotNull(localwrapper.initialize());
        assertEquals("Local-wrapper", localwrapper.getWrapperName());
        assertFalse(localwrapper.isClosed());
        assertTrue(localwrapper.toString().contains("LocalDistributionReq"));
        //has to return false
        assertFalse(localwrapper.sendToWrapper(null, null, null));
        assertNotNull(localwrapper.getVSensorConfig());
        localwrapper.close();
        assertTrue(localwrapper.isClosed());
    }


}

