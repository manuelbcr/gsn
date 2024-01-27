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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.mockito.Mockito;

public class UDPWrapperTest {
	
    private UDPWrapper wrapper;

    @Before
	public void setup(){

        wrapper = new UDPWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        predicates.add( new KeyValueImp("port", "57000"));
        
        AddressBean ab = new AddressBean("udpWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("udpWrapper");

        wrapper.setActiveAddressBean(ab);

        assertTrue(wrapper.initialize());
        assertEquals("network udp", wrapper.getWrapperName());
        
    }

    @After
    public void teardown(){
        wrapper.dispose();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInitialize(){

        UDPWrapper wrapper = new UDPWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        predicates.add( new KeyValueImp("port", "abc"));
        
        AddressBean ab = new AddressBean("udpWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("udpWrapper");

        wrapper.setActiveAddressBean(ab);
        
        assertFalse(wrapper.initialize());

    }

    @Test
    public void testOutputFormat(){

        DataField[] expected = new DataField[] {
            new DataField("RAW_PACKET", "BINARY", "The packet contains raw data received as a UDP packet.") 
        };

        DataField[] actual = wrapper.getOutputFormat();
        assertEquals(expected, actual);

    }

    @Test
    public void testRun(){

        wrapper.start();

        // Create a DatagramSocket for sending data
        try (DatagramSocket socket = new DatagramSocket(55100)) {

            InetAddress destinationAddress = InetAddress.getByName("127.0.0.1");
            int destinationPort = 57000;

            byte[] sendData = "Hello, UDP!".getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, destinationAddress, destinationPort);
            socket.send(sendPacket);

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        wrapper.stop();
        
    }

}