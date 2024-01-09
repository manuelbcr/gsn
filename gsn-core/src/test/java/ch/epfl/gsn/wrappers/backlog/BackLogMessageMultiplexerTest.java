package ch.epfl.gsn.wrappers.backlog;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.Serializable;

import org.junit.Test;

public class BackLogMessageMultiplexerTest {

    @Test(expected = Exception.class)
    public void testSendMessageToNonExistenthost() throws Exception {
        short messageType = 1;
        long timestamp = System.currentTimeMillis();
        Serializable[] payload = {"test", 42, true};
        
        BackLogMessage message = new BackLogMessage(messageType, timestamp, payload);
        BackLogMessageMultiplexer multiplexer = BackLogMessageMultiplexer.getInstance("deployment", "127.0.0.1:8080");
        multiplexer.sendMessage(message, null, 1);
    }



    @Test
    public void testNewPluginMessage() throws Exception {
        BackLogMessageMultiplexer mockMultiplexer= Mockito.mock(BackLogMessageMultiplexer.class);
        PluginMessageHandler messageHandler= new PluginMessageHandler(mockMultiplexer, 10);
        BackLogMessage message = new BackLogMessage((short) 1);

        assertTrue(messageHandler.newPluginMessage(message));

        messageHandler.dispose();
    }

    @Test
    public void testIsMsgQueueReady() throws Exception {
        BackLogMessageMultiplexer mockMultiplexer= Mockito.mock(BackLogMessageMultiplexer.class);
        PluginMessageHandler messageHandler= new PluginMessageHandler(mockMultiplexer, 10);
        BackLogMessage message = new BackLogMessage((short) 1);

        messageHandler.newPluginMessage(message);

        assertTrue(messageHandler.isMsgQueueReady());
        assertFalse(messageHandler.isMsgQueueLimitReached());

        messageHandler.dispose();
    }


}
