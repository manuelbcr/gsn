package ch.epfl.gsn.vsensor;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.TimerTask;

import org.junit.Before;
import org.junit.Test;

import ch.epfl.gsn.beans.AddressBean;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.VSensorConfig;

public class ScheduledBridgeVirtualSensorTest {
    private ScheduledBridgeVirtualSensor vs;
    private VSensorConfig testVsensorConfig;

    @Before
    public void setUp() throws IOException {
         DataField[] fields = new DataField[]{
            new DataField("raw_packet", DataTypes.BINARY),
        };
        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("schedulebridgevs");
		File someFile = File.createTempFile("schedulebridgevs", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.ScheduledBridgeVirtualSensor");
		testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);
        testVsensorConfig.getMainClassInitialParams().put("rate", "1000");
    }

    @Test
    public void testInitialization() {
        vs = new ScheduledBridgeVirtualSensor();
        vs.setVirtualSensorConfiguration(testVsensorConfig);

        assertTrue(vs.initialize());
        vs.dispose();

    }

    @Test
    public void testBridgeVirtualSensorInitialize() throws IOException{
         DataField[] fields = new DataField[]{
            new DataField("raw_packet", DataTypes.BINARY),
        };
        VSensorConfig testVsensorConfig1 = new VSensorConfig();
        testVsensorConfig1.setName("testbridgevirtualsensor");
		File someFile = File.createTempFile("testbridgevirtualsensor", ".xml");
		testVsensorConfig1.setMainClass("ch.epfl.gsn.vsensor.BridgeVirtualSensor");
		testVsensorConfig1.setFileName(someFile.getAbsolutePath());
        testVsensorConfig1.setOutputStructure(fields);
        testVsensorConfig1.getMainClassInitialParams().put("allow-nulls", "false");
        BridgeVirtualSensor vs = new BridgeVirtualSensor();
        vs.setVirtualSensorConfiguration(testVsensorConfig1);
        assertTrue(vs.initialize());
         StreamElement streamElement1 = new StreamElement(
            new String[]{"message", "phonenumber","receiver"},
            new Byte[]{DataTypes.VARCHAR, DataTypes.VARCHAR,DataTypes.VARCHAR},
            new Serializable[]{null,null,null},
            System.currentTimeMillis()+20000);
        vs.dataAvailable("input", streamElement1);
        assertTrue(vs.areAllFieldsNull(streamElement1));
        vs.dispose();
    }



}
