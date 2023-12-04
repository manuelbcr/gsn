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

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.epfl.gsn.Main;
import ch.epfl.gsn.beans.AddressBean;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.KeyValueImp;
import thredds.inventory.bdb.MetadataManager.KeyValue;

public class LocalDeliveryWrapperTest {

	private static StorageManager sm;
    private VSensorConfig testVensorConfig;
    private LocalDeliveryWrapper localwrapper;

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
	public void setUp() throws Exception {	
        
		testVensorConfig = new VSensorConfig();
		testVensorConfig.setName("localdeliverywrapper");
		File someFile = File.createTempFile("bla", ".xml");
		testVensorConfig.setMainClass("ch.epfl.gsn.vsensor.BridgeVirtualSensor");
		testVensorConfig.setFileName(someFile.getAbsolutePath());
	
	}

    @Test
    public void testToString() throws SQLException, IOException{
        LocalDeliveryWrapper localwrapper= new LocalDeliveryWrapper();
        sm.executeCreateTable(localwrapper.getDBAliasInStr(), new DataField[] {new DataField("data","int")},false);
        System.out.println(sm.tableExists(localwrapper.getDBAliasInStr()));
        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        String nameString= localwrapper.getDBAliasInStr().toString();
		predicates.add( new KeyValueImp( "name" ,nameString ) );
        predicates.add( new KeyValueImp( "start-time" ,"continue" ) );

        AddressBean addressbean= new AddressBean("Local-wrapper",predicates.toArray(new KeyValueImp[] {}));
        addressbean.setVirtualSensorName(nameString);
        localwrapper.setActiveAddressBean(addressbean);
        

        localwrapper.writeStructure(new DataField[] {new DataField("data","int")});
        assertNotNull(localwrapper.getOutputFormat());
        assertTrue(localwrapper.writeKeepAliveStreamElement());
        assertTrue(localwrapper.writeStreamElement(new StreamElement(new DataField[] {},new Serializable[] {},System.currentTimeMillis())));
        assertNotNull(localwrapper.initialize());
        assertEquals("Local-wrapper", localwrapper.getWrapperName());
        assertFalse(localwrapper.isClosed());
        localwrapper.close();
        assertTrue(localwrapper.isClosed());
    }


}

