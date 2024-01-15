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
import java.io.FileNotFoundException;

import java.util.HashMap;
import java.util.TimeZone;

public class TestContainerConfig {
    
    StorageConfig storageConfig;
    SlidingConfig slidingConfig;

    ContainerConfig containerConfig;

    @Before
	public void setUp() {

        storageConfig = new StorageConfig();
        slidingConfig = new SlidingConfig();
        containerConfig = new ContainerConfig(12345, "timeFormat", false, 23456, 34567, storageConfig, slidingConfig, 10, 10);
        assertNotNull(containerConfig);
        ContainerConfig containerConfig_tmp = new ContainerConfig();
	}


    @Test
	public void testContainerConfigSetDatabaseSystem() {

        containerConfig.setdatabaseSystem("H2 in Memory");
        StorageConfig sc = containerConfig.getStorage();
        assertEquals("org.h2.Driver", sc.getJdbcDriver());
        assertEquals("", sc.getJdbcPassword());
        assertEquals("sa", sc.getJdbcUsername());
        assertEquals("jdbc:h2:mem:.", sc.getJdbcURL());
        assertEquals("H2 in Memory",containerConfig.getdatabaseSystem());

        containerConfig.setdatabaseSystem("H2 in File");
        sc = containerConfig.getStorage();
        assertEquals("org.h2.Driver", sc.getJdbcDriver());
        assertEquals("", sc.getJdbcPassword());
        assertEquals("sa", sc.getJdbcUsername());
        assertEquals("jdbc:h2:file:/path/to/file", sc.getJdbcURL());
        assertEquals("H2 in File",containerConfig.getdatabaseSystem());

        containerConfig.setdatabaseSystem("MySql");
        sc = containerConfig.getStorage();
        assertEquals("com.mysql.jdbc.Driver", sc.getJdbcDriver());
        assertEquals("jdbc:mysql://localhost:3306/ch.epfl.gsn", sc.getJdbcURL());
        assertEquals("MySql",containerConfig.getdatabaseSystem());

        containerConfig.setdatabaseSystem("SQL Server");
        sc = containerConfig.getStorage();
        assertEquals("net.sourceforge.jtds.jdbc.Driver", sc.getJdbcDriver());
        assertEquals("jdbc:jtds:sqlserver://localhost/ch.epfl.gsn", sc.getJdbcURL());
        assertEquals("SQL Server",containerConfig.getdatabaseSystem());

        containerConfig.setdatabaseSystem("No Driver");
        sc = containerConfig.getStorage();
        assertEquals("", sc.getJdbcDriver());
    
        assertEquals("timeFormat", containerConfig.getTimeFormat());
        assertEquals(TimeZone.getTimeZone("UTC"), containerConfig.getTimeZone());
        assertEquals("changeit", containerConfig.getSSLKeyStorePassword());
        assertEquals(-1, containerConfig.getSSLPort());
        assertEquals(22001, containerConfig.getContainerPort());
        containerConfig.setContainerConfigurationFileName("test.xml");
        assertEquals("test.xml", containerConfig.getContainerFileName());

	}

    @Test
	public void testContainerConfigGetDatabaseSystem() {

        StorageConfig stc = new StorageConfig();
        stc.setJdbcURL("jdbc:h2:mem:.");
        SlidingConfig slc = new SlidingConfig();
        ContainerConfig cc = new ContainerConfig(12345, "timeFormat", false, 23456, 34567, stc, slc, 10, 10);
        assertNotNull(cc);
        assertEquals("H2 in Memory", cc.getdatabaseSystem());

	}

    @Test
	public void testContainerConfigGetDefaultConfiguration() {

        /*
        ContainerConfig cc = ContainerConfig.getDefaultConfiguration();
        StorageConfig sc = cc.getStorage();
        assertEquals("org.h2.Driver", sc.getJdbcDriver());
        assertEquals("", sc.getJdbcPassword());
        assertEquals("sa", sc.getJdbcUsername());
        assertEquals("jdbc:h2:mem:.", sc.getJdbcURL());
        assertEquals("H2 in Memory",cc.getdatabaseSystem());
        */

	}

    @Test
    public void testContainerConfigGetConfigurationFromFile() throws FileNotFoundException {

        ContainerConfig cfg = ContainerConfig.getConfigurationFromFile("/home/vagrant/gsn/conf/gsn_test.xml");
        assertNotNull(cfg);
        assertEquals(16, cfg.getMaxDBConnections());
        assertEquals(16, cfg.getMaxSlidingDBConnections());
        cfg.setMaxDBConnections(10);
        cfg.setMaxSlidingDBConnections(10);
        cfg.setMonitorPort(1234);
        assertEquals(10, cfg.getMaxDBConnections());
        assertEquals(10, cfg.getMaxSlidingDBConnections());
        assertEquals(1234, cfg.getMonitorPort());
        assertEquals(-1, cfg.getStoragePoolSize());
        

    }

    @Test
    public void testContainerConfigGetMsrMap() {

        HashMap<String, String> map = containerConfig.getMsrMap();
        assertNotNull(map);

    }

	
}