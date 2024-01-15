package ch.epfl.gsn.wrappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.OperationNotSupportedException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.epfl.gsn.beans.AddressBean;
import ch.epfl.gsn.beans.InputInfo;
import ch.epfl.gsn.utils.KeyValueImp;
import ch.epfl.gsn.wrappers.backlog.BackLogMessageMultiplexer;
import ch.epfl.gsn.beans.DataField;

public class TestBacklogWrapper {

    private BackLogWrapper wrapper;

    @Before
	public void setup() throws SQLException, IOException {

        wrapper = new BackLogWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("remote-connection", "localhost:8080"));
        predicates.add( new KeyValueImp("plugin-classname", "ch.epfl.gsn.wrappers.backlog.plugins.DPPMessagePlugin"));
        predicates.add( new KeyValueImp("message-classname", "ch.epfl.gsn.wrappers.backlog.plugins.dpp.EventMsg"));

        AddressBean ab = new AddressBean("backlogWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("backlogWrapper");

        wrapper.setActiveAddressBean(ab);

        assertTrue(wrapper.initialize());
        assertEquals("BackLogWrapper", wrapper.getWrapperName());
        assertEquals("localhost:8080", wrapper.getRemoteConnectionPoint());
    }

    @Test
    public void testInitializeWithoutRemote() {

       
        wrapper = new BackLogWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        predicates.add( new KeyValueImp("plugin-classname", "ch.epfl.gsn.wrappers.backlog.plugins.DPPMessagePlugin"));
        predicates.add( new KeyValueImp("message-classname", "ch.epfl.gsn.wrappers.backlog.plugins.dpp.EventMsg"));

        AddressBean ab = new AddressBean("backlogWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("backlogWrapper");

        wrapper.setActiveAddressBean(ab);

        assertFalse(wrapper.initialize());
       
    }

    @Test
    public void testInitializeWithoutPlugin() {

        wrapper = new BackLogWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("remote-connection", "localhost:8080"));
        predicates.add( new KeyValueImp("message-classname", "ch.epfl.gsn.wrappers.backlog.plugins.dpp.EventMsg"));

        AddressBean ab = new AddressBean("backlogWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("backlogWrapper");

        wrapper.setActiveAddressBean(ab);

        assertFalse(wrapper.initialize());
       
    }

    @Test
    public void testInitializeNonExistantPlugin() {

        wrapper = new BackLogWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("remote-connection", "localhost:8080"));
        predicates.add( new KeyValueImp("plugin-classname", "plugin"));
        predicates.add( new KeyValueImp("message-classname", "ch.epfl.gsn.wrappers.backlog.plugins.dpp.EventMsg"));

        AddressBean ab = new AddressBean("backlogWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("backlogWrapper");

        wrapper.setActiveAddressBean(ab);

        assertFalse(wrapper.initialize());
       
    }


    @Test
    public void sendToWrapperCoreStationIdIsNotInteger() {

        String action = "someAction";
        String[] paramNames = {"core_station"};
        Serializable[] paramValues = {"abc"};
        InputInfo result = null;
        
        try {
            result = wrapper.sendToWrapper(action, paramNames, paramValues);
        } catch (OperationNotSupportedException e) {
            fail("OperationNotSupportedException should not be thrown in this scenario.");
        }

        assertNotNull(result);
        assertFalse(result.hasAtLeastOneSuccess());
        assertTrue(result.toString().startsWith("The device_id in the core station field has to be an integer"));
       
    }

    @Test
    public void sendToWrapperDeviceIdOutOfRange() {

        String action = "someAction";
        String[] paramNames = {"core_station"};
        Serializable[] paramValues = {"65536"};

        InputInfo result = null;
        try {
            result = wrapper.sendToWrapper(action, paramNames, paramValues);
        } catch (OperationNotSupportedException e) {
            fail("OperationNotSupportedException should not be thrown in this scenario.");
        }

        assertNotNull(result);
        assertFalse(result.hasAtLeastOneSuccess());
        assertTrue(result.toString().startsWith("device_id has to be a number between 0 and 65535 (inclusive)"));

    }

    @Test
    public void testGetOutputFormat() {
        DataField[] fields = wrapper.getOutputFormat();
        for (DataField field : fields) {
            System.out.println(field.toString());  
        }

        assertNotNull(fields);
    }

    @Test
    public void testDataProcessed() {
        long timestamp = System.currentTimeMillis();
        Serializable[] data = {
            timestamp,                      
            System.currentTimeMillis(),     
            System.nanoTime() / 1000,       
            123,                           
            456,                            
            789,                            
            101,                           
            1024,                       
            (short)1,                            
            (short)1,                           
            987654321L
        };
      
        boolean result = wrapper.dataProcessed(timestamp, data);
        assertFalse(result);
    }
  

    @Test
    public void testRun() {

        wrapper.start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        wrapper.stop();
        wrapper.dispose();

    }

    
}
