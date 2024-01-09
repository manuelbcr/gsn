package ch.epfl.gsn.vsensor;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;

public class CounterVSTest {
    @Test
    public void testInitialize() {
        CounterVS vs = new CounterVS();
        assertTrue(vs.initialize());
        //vs.dataAvailable("inputstreamname", null);
    }
}
