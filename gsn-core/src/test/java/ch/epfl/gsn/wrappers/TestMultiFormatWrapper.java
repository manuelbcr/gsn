package ch.epfl.gsn.wrappers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import ch.epfl.gsn.beans.AddressBean;
import ch.epfl.gsn.utils.KeyValueImp;

public class TestMultiFormatWrapper {

    private MultiFormatWrapper wrapper;

    @Before
	public void setup() throws SQLException, IOException {

        wrapper = new MultiFormatWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        predicates.add( new KeyValueImp("rate", "2000"));
        
        AddressBean ab = new AddressBean("multiformat-wrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("multiformat-wrapper");

        wrapper.setActiveAddressBean(ab);
    }

    @Test
    public void testInitialize() {
        assertTrue(wrapper.initialize());
        assertEquals("MultiFormat Sample Wrapper", wrapper.getWrapperName());
    }

    @Test
    public void testRun() {
        wrapper.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        wrapper.stop();
        wrapper.dispose();
    }
    
}
