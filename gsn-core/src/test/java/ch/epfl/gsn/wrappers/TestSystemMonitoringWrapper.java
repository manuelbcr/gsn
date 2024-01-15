package ch.epfl.gsn.wrappers;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import ch.epfl.gsn.beans.AddressBean;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.utils.KeyValueImp;

public class TestSystemMonitoringWrapper {

    private SystemMonitoringWrapper wrapper;

    @Before
	public void setup() throws SQLException, IOException {

        wrapper = new SystemMonitoringWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("sampling-rate", "1"));

        AddressBean ab = new AddressBean("monitoringWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("monitoringWrapper");

        wrapper.setActiveAddressBean(ab);

        assertTrue(wrapper.initialize());
        assertEquals("System Monitoring", wrapper.getWrapperName());
    }

    @Test
    public void testInitializeDefaultSampling(){

        SystemMonitoringWrapper wrapper = new SystemMonitoringWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        predicates.add( new KeyValueImp("sampling-rate", "-1"));
		
        AddressBean ab = new AddressBean("monitoringWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("monitoringWrapper");

        wrapper.setActiveAddressBean(ab);

        assertTrue(wrapper.initialize());

    }

    @Test
    public void testGetOutputFormat() {
    
        DataField[] expected = new DataField [ ] { 
            new DataField( "HEAP" , "bigint" , "Heap memory usage." ) ,
            new DataField( "MAX_HEAP" , "bigint" , "Maximum amount of HEAP memory in bytes that can be used for memory management" ) ,
            new DataField( "NON_HEAP" , "bigint" , "Nonheap memory usage." ) , 
            new DataField( "PENDING_FINALIZATION_COUNT" , "int" , "The number of objects with pending finalization."),
            new DataField( "SYSTEM_LOAD_AVERAGE" , "double" , "System load average for the last minute"),
            new DataField( "THREAD_COUNT" , "int" , "Thread Count" ),
            new DataField( "PEAK_THREAD_COUNT" , "int" , "peak live thread count since the Java virtual machine started" ),
            new DataField( "UPTIME" , "bigint" , "uptime of the Java virtual machine in milliseconds" ),
            new DataField( "BLOCKED_THREADS" , "int" , "blocked threads counter" ),
            new DataField( "NEW_THREADS" , "int" , "new threads counter" ),
            new DataField( "RUNNABLE_THREADS" , "int" , "runnable threads counter" ),
            new DataField( "WAITING_THREADS" , "int" , "waiting threads counter" ),
            new DataField( "TERMINATED_THREADS" , "int" , "terminated threads counter" )   
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
