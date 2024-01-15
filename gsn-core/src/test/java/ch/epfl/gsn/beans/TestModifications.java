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
import org.junit.Ignore;

import java.io.IOException;
import java.sql.DriverManager;
import java.io.File;
import java.util.Collection;
import java.util.ArrayList;

import ch.epfl.gsn.VirtualSensor;
import ch.epfl.gsn.Main;
import ch.epfl.gsn.Mappings;

import ch.epfl.gsn.beans.InputStream;
import ch.epfl.gsn.beans.StreamSource;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;

import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.keyvalue.DefaultKeyValue;

public class TestModifications {

    VSensorConfig testVsensorConfig1;
    File file1;

    VSensorConfig testVsensorConfig2;
    File file2;

    Collection<String> fileNames;
    Collection<String> fileNamesRemove;
    ArrayList<VSensorConfig> add;
    Modifications modifications;

    @BeforeClass
	public static void setUpBeforeClass() throws Exception {
	  	String currentWorkingDir = System.getProperty("user.dir");
		if (!currentWorkingDir.endsWith("/gsn-core/")) {
			String newDirectory = currentWorkingDir + "/gsn-core/";
        	System.setProperty("user.dir", newDirectory);
		}

		DriverManager.registerDriver( new org.h2.Driver( ) );

		Main.setDefaultGsnConf("/gsn_test.xml");
	  	Main.getInstance();
		
	}


    @Before
	public void setUp() throws IOException {

        DataField[] fields = new DataField[]{
            new DataField("name", DataTypes.VARCHAR),
            new DataField("value", DataTypes.DOUBLE)
        };

        testVsensorConfig1 = new VSensorConfig();
		testVsensorConfig1.setName("testvsname1");
		file1 = File.createTempFile("testvsname1", ".xml");

		testVsensorConfig1.setMainClass("ch.epfl.gsn.vsensor.BridgeVirtualSensor");
		testVsensorConfig1.setFileName(file1.getAbsolutePath());
        testVsensorConfig1.setOutputStructure(fields);
        testVsensorConfig1.setDescription("desc");

        InputStream is1 = new InputStream();
		is1.setInputStreamName("t1");
		is1.setQuery("select * from my-stream1");
		StreamSource ss1 = new StreamSource().setAlias("my-stream1").setAddressing(new AddressBean[] {new AddressBean("mock-test")}).setSqlQuery("select * from wrapper").setRawHistorySize("2").setInputStream(is1);	
		ss1.setSamplingRate(1);
		assertTrue(ss1.validate());

        InputStream is2 = new InputStream();
		is2.setInputStreamName("t2");
		is2.setQuery("select * from my-stream2");

        KeyValue[] kv = new KeyValue[]{
                new DefaultKeyValue("address", "inproc://127.0.0.1"),
                new DefaultKeyValue("port", 1234),
                new DefaultKeyValue("query", "select * from testvsname2")
        };
		StreamSource ss2 = new StreamSource().setAlias("my-stream2").setAddressing(new AddressBean[] {new AddressBean("zeromq-sync", kv)}).setSqlQuery("select * from wrapper").setRawHistorySize("2").setInputStream(is2);	
		ss2.setSamplingRate(1);
		assertTrue(ss2.validate());
        
        StreamSource[] sourcesArray = {ss1, ss2};
        is1.setSources(sourcesArray);
        is2.setSources(sourcesArray);

        InputStream[] inputStreams = {is1, is2};
        testVsensorConfig1.setInputStreams(inputStreams);
        assertTrue(testVsensorConfig1.validate());

        testVsensorConfig2 = new VSensorConfig();
		testVsensorConfig2.setName("testvsname2");
		file2 = File.createTempFile("testvsname2", ".xml");

		testVsensorConfig2.setMainClass("ch.epfl.gsn.vsensor.BridgeVirtualSensor");
		testVsensorConfig2.setFileName(file2.getAbsolutePath());
        testVsensorConfig2.setOutputStructure(fields);

        fileNames = new ArrayList<>();
        fileNamesRemove = new ArrayList<>();
        fileNames.add(file1.getAbsolutePath());
        fileNames.add(file2.getAbsolutePath());

        KeyValue[] emptyAddressingArray = new KeyValue[0];
        testVsensorConfig1.setAddressing(emptyAddressingArray);
        testVsensorConfig2.setAddressing(emptyAddressingArray);
        
        VirtualSensor pool1 = new VirtualSensor(testVsensorConfig1);
        VirtualSensor pool2 = new VirtualSensor(testVsensorConfig2);
        Mappings.addVSensorInstance(pool1);
        Mappings.addVSensorInstance(pool2);

        modifications = new Modifications(fileNames, fileNamesRemove);
        assertNotNull(modifications);
        add = modifications.getAdd();

	}


    @Test
	public void testModificationsSetRemove() throws IOException {

        Collection<String> to_remove = new ArrayList<>();
        to_remove.add(file1.getAbsolutePath());
        modifications.setRemove(to_remove);

	}
	
}