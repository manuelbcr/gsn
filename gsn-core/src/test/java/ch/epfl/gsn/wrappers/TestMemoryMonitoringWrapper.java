package ch.epfl.gsn.wrappers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import ch.epfl.gsn.beans.AddressBean;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.utils.KeyValueImp;

public class TestMemoryMonitoringWrapper {

    private MemoryMonitoringWrapper wrapper;

    @Before
	public void setup() throws SQLException, IOException {

        wrapper = new MemoryMonitoringWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("sampling-rate", "1"));

        AddressBean ab = new AddressBean("memoryMonitoringWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("memoryMonitoringWrapper");

        wrapper.setActiveAddressBean(ab);

        assertTrue(wrapper.initialize());
    }

    @Test
    public void testInitializeDefaultSampling(){

        MemoryMonitoringWrapper wrapper = new MemoryMonitoringWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        predicates.add( new KeyValueImp("sampling-rate", "-1"));
		
        AddressBean ab = new AddressBean("memoryMonitoringWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("neniryMonitoringWrapper");

        wrapper.setActiveAddressBean(ab);

        assertTrue(wrapper.initialize());

    }

    @Test
    public void testGetOutputFormat() {

        DataField[] expected = new DataField []{ 
            new DataField( "HEAP" , "bigint" , "Heap memory usage." ),
            new DataField( "NON_HEAP" , "bigint" , "Nonheap memory usage." ),
            new DataField( "PENDING_FINALIZATION_COUNT" , "int" , "The number of objects with pending finalization.")
        };
        
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

        wrapper.stop();
        wrapper.dispose();

    }
    
}
