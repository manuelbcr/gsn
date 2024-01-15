package ch.epfl.gsn.wrappers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import ch.epfl.gsn.beans.AddressBean;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.utils.KeyValueImp;

public class TestSystemTimeWrapper {

    private SystemTime wrapper;

    @Before
	public void setup() throws SQLException, IOException {

        wrapper = new SystemTime();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        predicates.add( new KeyValueImp("clock-period", "1000"));
        predicates.add( new KeyValueImp("max-delay", "1"));
        
        AddressBean ab = new AddressBean("systemTime",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("systemTime");

        wrapper.setActiveAddressBean(ab);
        
        assertTrue(wrapper.initialize());
        assertEquals("System Time", wrapper.getWrapperName());
        
    }

    @Test
    public void testInitializeMaxDelay(){

        SystemTime wrapper = new SystemTime();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        predicates.add( new KeyValueImp("clock-period", "1000"));
        predicates.add( new KeyValueImp("max-delay", "2000"));
        
        AddressBean ab = new AddressBean("systemTime",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("systemTime");

        wrapper.setActiveAddressBean(ab);
        assertTrue(wrapper.initialize());

    }

    @Test
    public void testGetOutputFormat() {
    
        DataField[] expected = new DataField[] {};
        
        wrapper.getOutputFormat();
        
        DataField[] actualOutput = wrapper.getOutputFormat();
        assertArrayEquals(expected, actualOutput);
    }

    @Test
    public void testRun(){

        wrapper.start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        wrapper.dispose();
        wrapper.stop();

    }
    
}
