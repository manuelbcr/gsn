package ch.epfl.gsn.wrappers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.io.Serializable;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import org.eclipse.californium.core.coap.CoAP;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.epfl.gsn.Main;
import ch.epfl.gsn.beans.AddressBean;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.KeyValueImp;

public class TestCoAPWrapper {

    private static StorageManager sm;
    private CoAPWrapper wrapper;

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

        wrapper = new CoAPWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("uri", "coap://[::1]:5683/.temp"));

        AddressBean ab = new AddressBean("coapWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("coapWrapper");

        wrapper.setActiveAddressBean(ab);

        assertTrue(wrapper.initialize());
        assertEquals("CoAP Wrapper", wrapper.getWrapperName());
    }

    @After
	public void teardown() throws SQLException {
        wrapper.dispose();
	}

    @Test
    public void testInitializeDefaultSpeed(){

        CoAPWrapper wrapper = new CoAPWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		
        AddressBean ab = new AddressBean("coapWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("coapWrapper");

        wrapper.setActiveAddressBean(ab);

        assertFalse(wrapper.initialize());

    }

    @Test
    public void testGetOutputFormat() {

        DataField[] expected = new DataField[] {
            new DataField( "raw_packet" , "BINARY" , "The packet contains raw data received in the CoAP payload." )
        };
        
        DataField[] actualOutput = wrapper.getOutputFormat();
        assertArrayEquals(expected, actualOutput);
    }

    @Test
    public void testRun(){

        wrapper.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        wrapper.onError();
        wrapper.stop();

    }
    
}
