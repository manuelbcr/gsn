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

public class TestSensorMonitoringWrapper {

    private SensorMonitoringWrapper wrapper;

    @Before
	public void setup() throws SQLException, IOException {

        wrapper = new SensorMonitoringWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("sampling-rate", "1"));

        AddressBean ab = new AddressBean("sensorMonitoringWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("sensorMonitoringWrapper");

        wrapper.setActiveAddressBean(ab);

        assertTrue(wrapper.initialize());
        assertEquals("Sensor Monitoring", wrapper.getWrapperName());
    }

    @Test
    public void testInitializeDefaultSampling(){

        SensorMonitoringWrapper wrapper = new SensorMonitoringWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        predicates.add( new KeyValueImp("sampling-rate", "-1"));
		
        AddressBean ab = new AddressBean("sensorMonitoringWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("sensorMonitoringWrapper");

        wrapper.setActiveAddressBean(ab);

        assertTrue(wrapper.initialize());

    }

    @Test
    public void testGetOutputFormat() {

        DataField[] expected = new DataField [ ] { 
            new DataField( "SENSOR_NAME" , "varchar(50)" , "Name of the monitored sensor" ) ,
            new DataField( "TOTAL_CPU_TIME_COUNTER" , "bigint" , "cpu time of monitored sensor" ) ,
            new DataField( "LAST_OUTPUT_TIME" , "bigint" , "last output time of monitored sensor" ) ,
            new DataField( "OUTPUT_PRODUCED_COUNTER" , "bigint" , "output counter of monitored sensor" ),
            new DataField( "LAST_INPUT_TIME" , "bigint" , "last output time of monitored sensor" ),
            new DataField( "INPUT_PRODUCED_COUNTER" , "bigint" , "input counter of monitored sensor" )
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
