package ch.epfl.gsn.monitoring;

import static org.junit.Assert.assertEquals;

import java.util.Hashtable;

import org.junit.Test;

public class MemoryMonitorTest {
  @Test
  public void testGetStatistics() {
    MemoryMonitor memoryMonitor = new MemoryMonitor();
    Hashtable<String, Object> stats = memoryMonitor.getStatistics();


    assertEquals(true, stats.containsKey("core.memory.heap.gauge"));
    assertEquals(true, stats.containsKey("core.memory.nonHeap.gauge"));
    assertEquals(true, stats.containsKey("core.memory.pendingFinalizationCount.gauge"));
  }
}
