package ch.epfl.gsn.wrappers.backlog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class AsyncCoreStationClientTest {

 
    @Test
    public void testRegisterListener() {
        try {
            AsyncCoreStationClient asyncCoreStationClient = AsyncCoreStationClient.getSingletonObject();
            assertNotNull(asyncCoreStationClient);

            CoreStationListener listener = new DummyCoreStationListener("TestCoreStation", "127.0.0.1" ,1234);
            asyncCoreStationClient.registerListener(listener);

            assertTrue(asyncCoreStationClient.isAlive());
            assertEquals("TestCoreStation", listener.getCoreStationName());
            assertEquals(1234, listener.getPort());
            asyncCoreStationClient.deregisterListener(listener);



        } catch (Exception e) {
            fail("Error registering listener: " + e.getMessage());
        }
    }

    @Test
    public void testAddSendRemoveDeviceId() {
        try {
            AsyncCoreStationClient asyncCoreStationClient = AsyncCoreStationClient.getSingletonObject();
            assertNotNull(asyncCoreStationClient);

            CoreStationListener listener = new DummyCoreStationListener("TestCoreStation", "127.0.0.1", 65535);
            asyncCoreStationClient.registerListener(listener);

            assertTrue(asyncCoreStationClient.isAlive());
            assertEquals("TestCoreStation", listener.getCoreStationName());
            assertEquals(65535, listener.getPort());


            asyncCoreStationClient.addDeviceId("TestDeployment", 1, listener);

            
            byte[] testData = "Hello, CoreStation!".getBytes();
            Serializable[] sendResult = asyncCoreStationClient.send("TestDeployment", 1, listener, 1, testData);

            //no corestation running
            assertFalse((Boolean) sendResult[0]);
            assertNull(sendResult[1]);

            Serializable[] sendResult1= asyncCoreStationClient.sendHelloMsg(listener);
            assertFalse((Boolean) sendResult1[0]);
            assertNull(sendResult1[1]);

            asyncCoreStationClient.reconnect(listener);

            asyncCoreStationClient.removeDeviceId("TestDeployment", 1);
            asyncCoreStationClient.deregisterListener(listener);
        
        } catch (Exception e) {
            fail("Error testing add, send, and remove device ID: " + e.getMessage());
        }
    }

    @Test
    public void testChangeRequestInitialization() {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            ChangeRequest changeRequest = new ChangeRequest(socketChannel, ChangeRequest.TYPE_REGISTER, SelectionKey.OP_CONNECT);

            assertEquals(socketChannel, changeRequest.socket);
            assertEquals(ChangeRequest.TYPE_REGISTER, changeRequest.type);
            assertEquals(SelectionKey.OP_CONNECT, changeRequest.ops);

        } catch (Exception e) {
            fail("Error initializing ChangeRequest: " + e.getMessage());
        }
    }

    @Test
    public void testPriorityDataInitialization() {
        try {
            PriorityData priorityData = new PriorityData();

            assertNotNull(priorityData.queue);
            assertNotNull(priorityData.writeBuffer);
            assertNotNull(priorityData.readBuffer);

        } catch (Exception e) {
            fail("Error initializing PriorityData: " + e.getMessage());
        }
    }

    @Test
    public void testPriorityDataElement() {
        PriorityDataElement element1 = new PriorityDataElement(5, new byte[]{1, 2, 3});
        PriorityDataElement element2 = new PriorityDataElement(8, new byte[]{4, 5, 6});
        PriorityDataElement element3 = new PriorityDataElement(5, new byte[]{7, 8, 9});

        assertEquals(1, element1.getData()[0]);
        assertTrue(element1.compareTo(element2) < 0);  
        assertTrue(element2.compareTo(element1) > 0);  
        assertEquals(0, element1.compareTo(element3)); 

        
        try {
            element1.compareTo(null);
            fail("NullPointerException should be thrown");
        } catch (NullPointerException e) {
            
        }
    }


    private static class DummyCoreStationListener implements CoreStationListener {
        private String coreStationName;
        private InetAddress inetAddress;
        private int port;
    
        public DummyCoreStationListener(String coreStationName, String ipAddress, int port) throws UnknownHostException {
            this.coreStationName = coreStationName;
            this.inetAddress = InetAddress.getByName(ipAddress);
            this.port = port;
        }
    
        @Override
        public void processData(byte[] data, int count) {
            System.out.println("Received data: " + new String(data, 0, count));
        }
    
        @Override
        public String getCoreStationName() {
            return coreStationName;
        }
    
        @Override
        public int getPort() {
            return port;
        }
    
        @Override
        public InetAddress getInetAddress() throws UnknownHostException {
            return inetAddress;
        }
    
        @Override
        public void connectionLost() {
            System.out.println("Connection lost for " + coreStationName);
        }
    
        @Override
        public void connectionEstablished() {
            System.out.println("Connection established for " + coreStationName);
        }
    }

}
