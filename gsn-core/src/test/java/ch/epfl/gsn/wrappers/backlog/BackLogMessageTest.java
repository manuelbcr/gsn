package ch.epfl.gsn.wrappers.backlog;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.junit.Test;

public class BackLogMessageTest {
    @Test
    public void testConstructorWithType() {
        short messageType = 1;
        try {
            BackLogMessage message = new BackLogMessage(messageType);
            assertEquals(messageType, message.getType());
            assertEquals(0, message.getTimestamp());
            assertEquals(2, message.getSize());
        } catch (IOException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }


    @Test
    public void testConstructorWithTypeAndTimestamp() {
        short messageType = 1;
        long timestamp = System.currentTimeMillis();
        try {
            BackLogMessage message = new BackLogMessage(messageType, timestamp);
            assertEquals(messageType, message.getType());
            assertEquals(timestamp, message.getTimestamp());
            assertEquals(2, message.getSize());
        } catch (IOException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testConstructorWithTypeTimestampAndPayload() {
        short messageType = 1;
        long timestamp = System.currentTimeMillis();
        Serializable[] payload = {"test", 42, true};
        try {
            BackLogMessage message = new BackLogMessage(messageType, timestamp, payload);
            assertEquals(messageType, message.getType());
            assertEquals(timestamp, message.getTimestamp());
            assertArrayEquals(payload, message.getPayload());
            assertEquals(16, message.getSize());
        } catch (IOException | NullPointerException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testConstructorWithTypeTimestampAndPayload1() {
        short messageType = 1;
        long timestamp = System.currentTimeMillis();
        Serializable[] payload = {"test", 42, true};
        try {
            BackLogMessage message = new BackLogMessage(messageType, timestamp, payload);
            assertEquals(messageType, message.getType());
            assertEquals(timestamp, message.getTimestamp());
            assertArrayEquals(payload, message.getPayload());
            assertEquals(16, message.getSize());
        } catch (IOException | NullPointerException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testConstructorWithBinaryMessage() {
        short messageType = 1;
        long timestamp = System.currentTimeMillis();
        Serializable[] payload = {"test", 42, true,(byte) 1,null,2.2,(short)1,(long)1,new byte[]{1, 2, 3}};
        try {
            BackLogMessage originalMessage = new BackLogMessage(messageType, timestamp, payload);
            byte[] binaryMessage = originalMessage.getBinaryMessage();

            BackLogMessage reconstructedMessage = new BackLogMessage(binaryMessage);

            assertEquals(messageType, reconstructedMessage.getType());
            assertEquals(timestamp, reconstructedMessage.getTimestamp());
            assertArrayEquals(payload, reconstructedMessage.getPayload());
            assertEquals(originalMessage.getSize(), reconstructedMessage.getSize());
            assertArrayEquals(originalMessage.getBinaryMessage(), reconstructedMessage.getBinaryMessage());
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testSetType() {
        short messageType = 1;
        try {
            BackLogMessage message = new BackLogMessage(messageType);
            assertEquals(messageType, message.getType());

            short newType = 2;
            message.setType(newType);
            assertEquals(newType, message.getType());
        } catch (IOException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testSetTimestamp() {
        short messageType = 1;
        long timestamp = System.currentTimeMillis();
        try {
            BackLogMessage message = new BackLogMessage(messageType, timestamp);
            assertEquals(timestamp, message.getTimestamp());

            long newTimestamp = timestamp + 1000;
            message.setTimestamp(newTimestamp);
            assertEquals(newTimestamp, message.getTimestamp());
        } catch (IOException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testSetPayload() {
        short messageType = 1;
        long timestamp = System.currentTimeMillis();
        Serializable[] payload = {"test", 42, true};
        try {
            BackLogMessage message = new BackLogMessage(messageType, timestamp);

            message.setPayload(payload);
            assertArrayEquals(payload, message.getPayload());
        } catch (IOException | NullPointerException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }
  
}
