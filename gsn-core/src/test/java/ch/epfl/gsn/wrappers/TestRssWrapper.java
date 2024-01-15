package ch.epfl.gsn.wrappers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import ch.epfl.gsn.beans.AddressBean;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.utils.KeyValueImp;

public class TestRssWrapper {

    private RssWrapper wrapper;

    @Before
	public void setup() throws SQLException, IOException {

        wrapper = new RssWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("url", "localhost:8080"));
        predicates.add( new KeyValueImp("rate", "60000"));

        AddressBean ab = new AddressBean("rssWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("rssWrapper");

        wrapper.setActiveAddressBean(ab);

        assertTrue(wrapper.initialize());
        assertEquals("Rss Wrapper", wrapper.getWrapperName());
    }

    @Test
    public void testInitializeWithoutUrl() {

       
        wrapper = new RssWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        predicates.add( new KeyValueImp("rate", "60000"));

        AddressBean ab = new AddressBean("rssWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("rssWrapper");

        wrapper.setActiveAddressBean(ab);

        assertFalse(wrapper.initialize());
       
    }

     @Test
    public void testGetOutputFormat() {

        DataField[] expected = new DataField [ ] { 
            new DataField( "title" , "varchar(100)" , "Title of this Feed Entry" ),new DataField("author","varchar(100)","Author of This Feed Entry."),new DataField("description","varchar(255)","Description Field of This Feed Entry."),new DataField("link","varchar(255)","Link of This Feed Entry.")
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
