package ch.epfl.gsn.wrappers.general;

import java.io.IOException; 
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.BeforeClass;

import ch.epfl.gsn.Main;
import ch.epfl.gsn.beans.AddressBean;
import ch.epfl.gsn.utils.KeyValueImp;
import ch.epfl.gsn.beans.DataField;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.mockito.Mockito;

public class HttpGetWrapperTest {
	
    private HttpGetWrapper wrapper;

    @Before
	public void setup() throws IOException{

        wrapper = new HttpGetWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        predicates.add( new KeyValueImp("url", "http://localhost:8080/"));
        predicates.add( new KeyValueImp("rate", "0"));
        
        AddressBean ab = new AddressBean("getWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("getWrapper");

        wrapper.setActiveAddressBean(ab);
        
        assertTrue(wrapper.initialize());
        assertEquals("Http Receiver", wrapper.getWrapperName());

        URL mockUrl = Mockito.mock(URL.class);
        HttpURLConnection mockConnection =  Mockito.mock(HttpURLConnection.class);

        byte[] testData = "Test response from server".getBytes();
        InputStream testInputStream = new ByteArrayInputStream(testData);

        // Mock HttpURLConnection behavior
        Mockito.when(mockUrl.openConnection()).thenReturn(mockConnection);
        Mockito.when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        Mockito.when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream(testData));

        wrapper.setUrl(mockUrl);
        
    }

    @After
    public void teardown(){
        wrapper.dispose();
    }

    @Test
    public void testInitialize(){

        HttpGetWrapper wrapper = new HttpGetWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        predicates.add( new KeyValueImp("url", "http://localhost:8080/"));
        
        AddressBean ab = new AddressBean("getWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("getWrapper");

        wrapper.setActiveAddressBean(ab);
        
        assertTrue(wrapper.initialize());

    }


    @Test
    public void testInitializeMalformed(){

        HttpGetWrapper wrapper = new HttpGetWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        predicates.add( new KeyValueImp("url", "127.0.0.1"));
        
        AddressBean ab = new AddressBean("getWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("getWrapper");

        wrapper.setActiveAddressBean(ab);
        
        assertFalse(wrapper.initialize());

    }


    @Test
    public void testRun(){

        wrapper.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        wrapper.stop();
        
    }

}
