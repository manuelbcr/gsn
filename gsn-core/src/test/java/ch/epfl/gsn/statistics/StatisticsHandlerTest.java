package ch.epfl.gsn.statistics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class StatisticsHandlerTest {

    private StatisticsHandler statisticsHandler;
    private TestStatisticsListener listener1;
    private TestStatisticsListener listener2;

    @Before
    public void setUp() {
        statisticsHandler = StatisticsHandler.getInstance();
        listener1 = new TestStatisticsListener("Listener1");
        listener2 = new TestStatisticsListener("Listener2");
    }

    @Test
    public void testRegisterListener() {
        statisticsHandler.registerListener(listener1);
        assertEquals("Listener1", listener1.getRegisteredName());
        statisticsHandler.deregisterListener(listener1);

    }

    @Test
    public void testInputEventWithListeners() {
        statisticsHandler.registerListener(listener1);
        boolean result = statisticsHandler.inputEvent("producer", null);
        assertTrue(result);
        statisticsHandler.deregisterListener(listener1);
    }

    @Test
    public void testInputEventWithoutListeners() {
        boolean result = statisticsHandler.inputEvent("producer", null);
        assertFalse(result);
        result = statisticsHandler.inputEvent(null, null);
        assertFalse(result);
    }

    @Test
    public void testOutputEventWithListeners() {
        statisticsHandler.registerListener(listener1);
        boolean result = statisticsHandler.outputEvent("producer", null);
        assertTrue(result);
        statisticsHandler.deregisterListener(listener1);
    }

    @Test
    public void testOutputEventWithoutListeners() {
        boolean result = statisticsHandler.outputEvent("producer", null);
        assertFalse(result);
        result = statisticsHandler.outputEvent(null, null);
        assertFalse(result);
    }


    private static class TestStatisticsListener implements StatisticsListener {

        private final String name;
        private String log;
        private String inputEventProducerName;
        private String outputEventProducerName;

        public TestStatisticsListener(String name) {
            this.name = name;
        }

        @Override
        public boolean inputEvent(String producerVS, StatisticsElement statisticsElement) {
            inputEventProducerName = producerVS;
            return true;
        }

        @Override
        public boolean outputEvent(String producerVS, StatisticsElement statisticsElement) {
            outputEventProducerName = producerVS;
            return true;
        }

        public String getRegisteredName() {
            return name;
        }

        public String getLog() {
            return log;
        }

        public String getInputEventProducerName() {
            return inputEventProducerName;
        }

        public String getOutputEventProducerName() {
            return outputEventProducerName;
        }

        @Override
        public String listenerName() {
            return name;
        }
    }
}
