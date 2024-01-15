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

import ch.epfl.gsn.config.FieldConf;
import ch.epfl.gsn.config.SourceConf;
import ch.epfl.gsn.config.WrapperConf;
import ch.epfl.gsn.config.StreamConf;
import ch.epfl.gsn.config.WebInputCommand;
import ch.epfl.gsn.config.WebInputConf;
import ch.epfl.gsn.config.ProcessingConf;
import ch.epfl.gsn.config.StorageConf; 
import ch.epfl.gsn.config.VsConf; 
import ch.epfl.gsn.config.ZmqConf;
import ch.epfl.gsn.config.GsnConf;

import scala.Option;
import scala.collection.Seq;
import scala.collection.JavaConverters;
import scala.collection.immutable.Map;
import scala.collection.immutable.HashMap;
import scala.Tuple2;

import java.util.List;

import java.io.IOException;

public class TestBeansInitializer {

    FieldConf fieldConf;
    WrapperConf wrapperConf;
    SourceConf sourceConf;
    StreamConf streamConf;
    WebInputCommand webInputCommand;
    WebInputConf webInputConf;
    VsConf vsConf;
    GsnConf gsnConf;

    @Before
	public void setUp() {

        // Field Conf
        fieldConf = new FieldConf("fieldName", "INTEGER", "Description", Option.apply("Unit"), Option.apply("true"));
        assertNotNull(fieldConf);

        // Seq Field Conf
        Seq<FieldConf> output = JavaConverters.asScalaBuffer(List.of(fieldConf)).toSeq();

        // Wrapper Conf
        Option<String> partialKey = Option.apply("partialKey");
        Map<String, String> paramsScala = new HashMap<>();
        Tuple2<String, String> entry1 = new Tuple2<>("param1", "value1");
        Tuple2<String, String> entry2 = new Tuple2<>("param2", "value2");
        paramsScala = paramsScala.$plus(entry1);
        paramsScala = paramsScala.$plus(entry2);
        wrapperConf = new WrapperConf("wrapperName", partialKey, paramsScala, output);
        assertNotNull(wrapperConf);

        // Seq Wrapper Conf
        Seq<WrapperConf> wrappers = JavaConverters.asScalaBuffer(List.of(wrapperConf)).toSeq();
        
        // Source Conf
        Option<String> storageSize = Option.apply("storageSize");
        Option<String> slide = Option.apply("slide");
        Option<Object> disconnectBufferSize = Option.apply((Object) 10);
        Option<Object> samplingRate = Option.apply((Object) 0.5);
        sourceConf = new SourceConf("alias", "query", storageSize, slide, disconnectBufferSize, samplingRate, wrappers);
        assertNotNull(sourceConf);

        // Seq Source Conf
        Seq<SourceConf> sources = JavaConverters.asScalaBuffer(List.of(sourceConf)).toSeq();

        // Stream Conf
        streamConf = new StreamConf("streamName", 100, 5, "streamQuery", sources);
        assertNotNull(streamConf);

        // Seq Stream Conf
        Seq<StreamConf> streams = JavaConverters.asScalaBuffer(List.of(streamConf)).toSeq();

        // Web Input Command
        webInputCommand = new WebInputCommand("webInputCommand", output);
        assertNotNull(webInputCommand);

        //Seq Web Input Commands
        Seq<WebInputCommand> commands = JavaConverters.asScalaBuffer(List.of(webInputCommand)).toSeq();

        // Web Input Conf
        WebInputConf webInputConf = new WebInputConf("password", commands);
        assertNotNull(webInputConf);

        // Processing Conf
        ProcessingConf processingConf = new ProcessingConf("className", true, paramsScala, Option.apply((Object) 10), output, Option.apply(webInputConf), Option.apply("partitionField"));
        assertNotNull(processingConf);

        // Storage Conf
        StorageConf storageConf = new StorageConf("driver", "url", "user", "pass", Option.apply("identifier"));
        assertNotNull(storageConf);

        // VS Conf
        Map<String, String> addressScala = new HashMap<>();
        Tuple2<String, String> address1 = new Tuple2<>("address1", "value1");
        Tuple2<String, String> address2 = new Tuple2<>("address2", "value2");
        addressScala = addressScala.$plus(address1);
        addressScala = addressScala.$plus(address2);
        vsConf = new VsConf("name", true, 1, true, "timeZone", "description", Option.apply(10), addressScala, Option.apply(storageConf), Option.apply("storageSize"), processingConf, streams);
        assertNotNull(vsConf);

        ZmqConf zmqConf = new ZmqConf(false, 123, 234);

        // GSN Conf
        gsnConf = new GsnConf(123, "timeFormat", zmqConf, storageConf, Option.apply(storageConf), 10, 10);
    
	}

    @Test
	public void testBeansInitializerDataField() {

        DataField df = BeansInitializer.dataField(fieldConf);
        assertEquals("fieldname",df.getName());
        assertEquals("INTEGER", df.getType());
        assertEquals("Description", df.getDescription());
        assertEquals("Unit", df.getUnit());
        assertEquals(true, df.getIndex());

        fieldConf = new FieldConf("fieldName", "INTEGER", "Description", Option.empty(), Option.empty());
        DataField df1 = BeansInitializer.dataField(fieldConf);
        assertNotNull(df1);

        
	}

    @Test
	public void testBeansInitializerSourceConf() {

        StreamSource ss = BeansInitializer.source(sourceConf);

        assertEquals("alias", ss.getAlias());
        assertEquals("query", ss.getSqlQuery());
        assertEquals("storageSize", ss.getStorageSize());
        assertEquals("slide",ss.getSlideValue());
        assertEquals(10, ss.getDisconnectedBufferSize());
        assertEquals(0.5, ss.getSamplingRate(), 0.0);
        AddressBean[] ab = ss.getAddressing();
        assertEquals(1, ab.length);
        
	}

    @Test
	public void testBeansInitializerStreamConf() {

        InputStream is = BeansInitializer.stream(streamConf);
        assertEquals("streamName", is.getInputStreamName());
        assertEquals(100, is.getRate());
        StreamSource[] ss = is.getSources();
        assertEquals(1, ss.length);
        assertEquals("alias", ss[0].getAlias());
        assertEquals("storageSize", ss[0].getRawHistorySize());
        assertNotNull(ss);
        assertNull(ss[0].getActiveAddressBean());
        
	}

    @Test
	public void testBeansInitializerWebInputCommand() {

        WebInput wi = BeansInitializer.webInput(webInputCommand);
        assertEquals("webInputCommand", wi.getName());
        DataField[] df = wi.getParameters();
        assertEquals(1, df.length);
        assertEquals("fieldname", df[0].getName());
        assertEquals("INTEGER", df[0].getType());
        assertEquals("Description", df[0].getDescription());
        
	}

    @Test
	public void testBeansInitializerVsConf() {
        
        VSensorConfig vc = BeansInitializer.vsensor(vsConf);
        assertEquals("name", vc.getName());
        assertEquals(true, vc.getIsTimeStampUnique());
        assertEquals(1, vc.getPriority());
        assertEquals(true, vc.hasInitPriority());
        assertEquals(10, vc.getLifeCyclePoolSize());
        assertEquals("password", vc.getWebParameterPassword());
        WebInput[] wi = vc.getWebinput();
        assertEquals(1, wi.length);
        assertEquals("webInputCommand", wi[0].getName());
        assertEquals("description", vc.getDescription());
        StorageConfig sc = vc.getStorage();
        assertEquals("identifier",sc.getIdentifier());
        assertTrue(sc.isStorageSize());
        assertTrue(sc.isDefined());
        assertTrue(sc.isIdentifierDefined());
        assertEquals(false, vc.getPublishToSensorMap());
        assertNull(vc.getDirectoryQuery());
        assertNull(vc.getLastModified());
        vc.setLastModified(1234567L);
        assertEquals(1234567L, vc.getLastModified(), 0.0);
        assertNotNull(vc.getMainClassInitialParams());
        assertNotNull(vc.toString());
        assertNull(vc.getSDF());

	}

    @Test
	public void testBeansInitializerGsnConf() {

        ContainerConfig cc = BeansInitializer.container(gsnConf);
        assertNotNull(cc);
        assertEquals(123, cc.getZMQProxyPort());
        assertEquals(234, cc.getZMQMetaPort());
        SlidingConfig sc = cc.getSliding();
        assertNotNull(sc);
        assertNotNull(sc.getStorage());
	}

    @Test
	public void testBeansInitializerContructor() {
        
        BeansInitializer bi = new BeansInitializer();
        assertNotNull(bi);

	}
	
}