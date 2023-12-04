package ch.epfl.gsn.statistics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class StatisticsElementTest {

    @Test
    public void testConstructorAndAccessors() {
        long timestamp = System.currentTimeMillis();
        String source = "Sensor1";
        String stream = "TemperatureStream";
        long volume = 100L;

        StatisticsElement statisticsElement = new StatisticsElement(timestamp, source, stream, volume);
        assertEquals(Long.valueOf(timestamp), statisticsElement.getProcessTime());
        assertEquals(source.toLowerCase().trim(), statisticsElement.getSource());
        assertEquals(stream.toLowerCase().trim(), statisticsElement.getStream());
        assertEquals(Long.valueOf(volume), statisticsElement.getVolume());
    }

    @Test
    public void testConstructorWithNullValues() {
        StatisticsElement statisticsElement = new StatisticsElement(0, null, null, 0);

        assertNull(statisticsElement.getSource());
        assertNull(statisticsElement.getStream());
        assertEquals(Long.valueOf(0L), statisticsElement.getVolume());
        assertEquals(Long.valueOf(0L), statisticsElement.getProcessTime());
    }
}
