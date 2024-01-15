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

public class TestDiskSpaceWrapper {

    private DiskSpaceWrapper wrapper;

    @Before
	public void setup() throws SQLException, IOException {

        wrapper = new DiskSpaceWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("rate", "1"));
        predicates.add( new KeyValueImp("rate", "1"));

        AddressBean ab = new AddressBean("diskSpace",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("diskSpace");

        wrapper.setActiveAddressBean(ab);

        assertTrue(wrapper.initialize());
        assertEquals("Free Disk Space", wrapper.getWrapperName());
    }

    @Test
    public void testGetOutputFormat() {

        DataField[] expected = new DataField[]{
            new DataField("FREE_SPACE", "bigint", "Free Disk Space")
        };
        
        wrapper.getOutputFormat();
        
        DataField[] actualOutput = wrapper.getOutputFormat();
        assertArrayEquals(expected, actualOutput);
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
