package ch.epfl.gsn.vsensor;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.epfl.gsn.beans.StreamElement;

public class MyFilterTest {
    @Test
    public void testInitialize() {
        MyFilter vs = new MyFilter();
        assertTrue(vs.initialize());
        vs.dataAvailable("input", new StreamElement());
        vs.dispose();
    }
}
