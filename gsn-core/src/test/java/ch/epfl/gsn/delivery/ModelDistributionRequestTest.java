package ch.epfl.gsn.delivery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ch.epfl.gsn.Main;
import ch.epfl.gsn.beans.AddressBean;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.models.AbstractModel;
import ch.epfl.gsn.wrappers.AbstractWrapper;
import ch.epfl.gsn.wrappers.SystemTime;


public class ModelDistributionRequestTest {

    private AbstractWrapper wrapper = new SystemTime();
	private static StorageManager sm =  null;//StorageManager.getInstance();
    private VSensorConfig testVensorConfig;
   
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	public void setup() throws SQLException, Exception {
		sm.executeCreateTable(wrapper.getDBAliasInStr(), new DataField[] {},true);
		wrapper.setActiveAddressBean(new AddressBean("system-time"));
		assertTrue(wrapper.initialize());

        testVensorConfig = new VSensorConfig();
		testVensorConfig.setName("test");
		File someFile = File.createTempFile("bla", ".xml");
		testVensorConfig.setMainClass("ch.epfl.gsn.vsensor.BridgeVirtualSensor");
		testVensorConfig.setFileName(someFile.getAbsolutePath());
	}

	@After
	public void teardown() throws SQLException {
		sm.executeDropTable(wrapper.getDBAliasInStr());
	}

    @Test
    public void testCreate() throws IOException, SQLException {
        DeliverySystem deliverySystem = new TestDeliverySystem();
        String query = "SELECT * FROM "+wrapper.getDBAliasInStr();
        AbstractModel modelClass = new TestModel();
        ModelDistributionRequest request = ModelDistributionRequest.create(deliverySystem, testVensorConfig, query, modelClass);


        assertNotNull(request);
        assertEquals(deliverySystem, request.getDeliverySystem());
        assertEquals(testVensorConfig, request.getVSensorConfig());
        assertEquals(query, request.getQuery());

    }

    @Test
    public void testToString() throws IOException, SQLException {
        DeliverySystem deliverySystem = new TestDeliverySystem();
        String query = "SELECT * FROM "+wrapper.getDBAliasInStr();
        AbstractModel modelClass = new TestModel();
        ModelDistributionRequest request = ModelDistributionRequest.create(deliverySystem, testVensorConfig, query, modelClass);

        String expectedToString = new StringBuilder("ModelDistributionRequest Request[[ Delivery System: ")
            .append(deliverySystem.getClass().getName())
            .append("],[Query:").append(query)
            .append("],[model:")
            .append(modelClass.getClass().getName())
            .append("],[VirtualSensorName:")
            .append(testVensorConfig.getName())
            .append("]]").toString();
        assertEquals(expectedToString, request.toString());
    }

    @Test
    public void testGetters() throws IOException, SQLException {
        DeliverySystem deliverySystem = new TestDeliverySystem();
        String query = "SELECT * FROM "+wrapper.getDBAliasInStr();
        AbstractModel modelClass = new TestModel();
        ModelDistributionRequest request = ModelDistributionRequest.create(deliverySystem, testVensorConfig, query, modelClass);

        assertEquals(query, request.getQuery());
        assertEquals(deliverySystem, request.getDeliverySystem());
        assertEquals(testVensorConfig, request.getVSensorConfig());
        assertFalse(request.isClosed());
        StreamElement inputElement = new StreamElement(new String[] { "x", "y", "z" }, new Byte[] { DataTypes.DOUBLE, DataTypes.DOUBLE, DataTypes.DOUBLE },new Serializable[] { 1.0, 2.0, 3.0 });
        assertTrue(request.deliverStreamElement(inputElement));
        assertEquals(0,request.getLastVisitedPk());
        assertNotNull(request.getModel());
        assertNotNull(request.deliverKeepAliveMessage());

    }


    @Test
    public void testEquals() throws IOException, SQLException {
        DeliverySystem deliverySystem = new TestDeliverySystem();
        DeliverySystem deliverySystem1 = new TestDeliverySystem();
        String query = "SELECT * FROM "+wrapper.getDBAliasInStr();
        AbstractModel modelClass = new TestModel();
        AbstractModel modelClass1 = new TestModel();

        ModelDistributionRequest request = ModelDistributionRequest.create(deliverySystem, testVensorConfig, query, modelClass);
        assertTrue(request.equals(request));
        assertFalse(request.equals(null));

        ModelDistributionRequest request1 = ModelDistributionRequest.create(deliverySystem, testVensorConfig, query, modelClass);

        assertTrue(request.equals(request1));

        ModelDistributionRequest request2 = ModelDistributionRequest.create(deliverySystem1, testVensorConfig, query, modelClass1);
        assertFalse(request.equals(request2));
    }


    @Test
    public void testHashCode() throws IOException, SQLException {
        DeliverySystem deliverySystem = new TestDeliverySystem();
        String query = "SELECT * FROM "+wrapper.getDBAliasInStr();
        AbstractModel modelClass = new TestModel();

        ModelDistributionRequest request = ModelDistributionRequest.create(deliverySystem, testVensorConfig, query, modelClass);
        ModelDistributionRequest request1 = ModelDistributionRequest.create(deliverySystem, testVensorConfig, query, modelClass);
        assertEquals(request.hashCode(), request1.hashCode());
        request.close();

    }

    private static class TestModel extends AbstractModel {
        @Override
        public StreamElement[] pushData(StreamElement streamElement, String origin) {
            return null;
        }

        @Override
        public StreamElement[] query(StreamElement params) {
            return null;
        }

        @Override
        public void setParam(String k, String string) {
        }
    }

     public class TestDeliverySystem implements DeliverySystem {

        private List<StreamElement> streamElements = new ArrayList<>();
        private boolean closed = false;

        @Override
        public void writeStructure(DataField[] fields) throws IOException {
        }

        @Override
        public boolean writeStreamElement(StreamElement se) {
            if (!closed) {
                streamElements.add(se);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean writeKeepAliveStreamElement() {
            return false;
        }

        @Override
        public void close() {
            closed = true;
        }

        @Override
        public boolean isClosed() {
            return closed;
        }

        public List<StreamElement> getStreamElements() {
            return new ArrayList<>(streamElements);
         }
    }
}
