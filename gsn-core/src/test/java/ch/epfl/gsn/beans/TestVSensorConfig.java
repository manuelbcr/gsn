/**
* Global Sensor Networks (GSN) Source Code
* Copyright (c) 2006-2016, Ecole Polytechnique Federale de Lausanne (EPFL)
* 
* This file is part of GSN.
* 
* GSN is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* GSN is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with GSN.  If not, see <http://www.gnu.org/licenses/>.
* 
* File: src/ch/epfl/gsn/beans/TestStreamSource.java
*
* @author gsn_devs
* @author Mehdi Riahi
* @author Ali Salehi
* @author Timotee Maret
*
*/

package ch.epfl.gsn.beans;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.epfl.gsn.Main;

import org.junit.Ignore;

import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.keyvalue.DefaultKeyValue;

import java.io.IOException;
import java.sql.DriverManager;
import java.io.File;


public class TestVSensorConfig {

    VSensorConfig testVSensorConfig;

    @Before
	public void setUp() throws IOException {

        testVSensorConfig = new VSensorConfig();
		testVSensorConfig.setName("test");
		File someFile = File.createTempFile("bla", ".xml");
		testVSensorConfig.setMainClass("ch.epfl.gsn.vsensor.BridgeVirtualSensor");
		testVSensorConfig.setFileName(someFile.getAbsolutePath());

        KeyValue[] addressing = new KeyValue[]{
                new DefaultKeyValue("altitude", "577.0"),
                new DefaultKeyValue("longitude", "11.400375"),
                new DefaultKeyValue("latitude", "47.259659"),
        };
        testVSensorConfig.setAddressing(addressing);

        DataField[] outputStructure = new DataField[] {
			new DataField("DEVICE_ID", "INTEGER"),
			new DataField("DEVICE_TYPE", "SMALLINT"),
		};
        testVSensorConfig.setOutputStructure(outputStructure);

        InputStream is = new InputStream();
		is.setInputStreamName("t1");
		is.setQuery("select * from my-stream1");

		StreamSource ss1 = new StreamSource().setAlias("my-stream1").setAddressing(new AddressBean[] {new AddressBean("mock-test")}).setSqlQuery("select * from wrapper").setRawHistorySize("2").setInputStream(is);
		ss1.setSamplingRate(1);
		assertTrue(ss1.validate());

		is.setSources(ss1);
		assertTrue(is.validate());
		testVSensorConfig.setInputStreams(is);
		assertTrue(testVSensorConfig.validate());
	}

	@Test
	public void testVSensorConfigPreprocesAddressing(){
        
        assertEquals((Double)577.0, testVSensorConfig.getAltitude(), 0.0);
        assertEquals((Double)11.400375, + testVSensorConfig.getLongitude(), 0.0);
        assertEquals((Double)47.259659, + testVSensorConfig.getLatitude(), 0.0);

	}

    @Test
	public void testVSensorConfigRPCFriendlyAddressing(){
        
       String[][] expectedAddressing = {
            {"altitude", "577.0"},
            {"longitude", "11.400375"},
            {"latitude", "47.259659"},  
        };

        String[][] rpcAddressing = testVSensorConfig.getRPCFriendlyAddressing();
        assertArrayEquals(expectedAddressing, rpcAddressing);
	}

    @Test
	public void testVSensorConfigRPCFriendlyOutputStructure(){
        
        String[][] expectedOutputStructure = {
            {"device_id", "INTEGER"},
            {"device_type", "SMALLINT"}
		};

        String[][] rpcOutputStructure = testVSensorConfig.getRPCFriendlyOutputStructure();
        assertArrayEquals(expectedOutputStructure, rpcOutputStructure);
	}

    @Test
	public void testVSensorConfigGetAddressingKeys(){
        String[] expectedAddressingKeys = {
            "altitude",
            "longitude",
            "latitude",
		};

        String[] addressingKeys = testVSensorConfig.getAddressingKeys();
        assertArrayEquals(expectedAddressingKeys, addressingKeys);
	}

    @Test
	public void testVSensorConfigGetAddressingValues(){
        String[] expectedAddressingValues = {
            "577.0",
            "11.400375",
            "47.259659",
		};

        String[] addressingValues = testVSensorConfig.getAddressingValues();
        assertArrayEquals(expectedAddressingValues, addressingValues);
	}

    @Test
	public void testVSensorConfigEquals(){
        VSensorConfig newConfig = new VSensorConfig();
		newConfig.setName("test");

        assertTrue(newConfig.equals(testVSensorConfig));

        newConfig.setName("nottest");
        assertFalse(newConfig.equals(testVSensorConfig));
	}

    @Test
	public void testVSensorConfigHashCode(){
        VSensorConfig newConfig = new VSensorConfig();
		newConfig.setName("test");
        assertNotNull(newConfig.hashCode());
        newConfig.setName(null);
        assertNotNull(newConfig.hashCode());
	}

    @Test
	public void testVSensorConfigProducingStatistics(){
        assertFalse(testVSensorConfig.isProducingStatistics());
	}

    @Test
	public void testVSensorConfigIsAccessProtected() {
        assertFalse(testVSensorConfig.isAccess_protected());
	}

    @Test
	public void testVSensorConfigToString(){
        assertNotNull(testVSensorConfig.toString());
	}

}