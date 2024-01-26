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
* File: src/ch/epfl/gsn/wrappers/TestAbstractWrapper.java
*
* @author Mehdi Riahi
* @author Ali Salehi
* @author Timotee Maret
*
*/

package ch.epfl.gsn.wrappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.OperationNotSupportedException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.epfl.gsn.Main;
import ch.epfl.gsn.beans.AddressBean;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.InputStream;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.StreamSource;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.GSNRuntimeException;
import ch.epfl.gsn.utils.KeyValueImp;
import ch.epfl.gsn.wrappers.AbstractWrapper;
import ch.epfl.gsn.wrappers.MockWrapper;
import ch.epfl.gsn.wrappers.SystemTime;

import org.junit.Ignore;

public class TestAbstractWrapper {

	private static StorageManager sm;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		String currentWorkingDir = System.getProperty("user.dir");
		if (!currentWorkingDir.endsWith("/gsn-core/")) {
			String newDirectory = currentWorkingDir + "/gsn-core/";
        	System.setProperty("user.dir", newDirectory);
		}

		DriverManager.registerDriver( new org.h2.Driver( ) );
		sm = StorageManagerFactory.getInstance( "org.h2.Driver","sa","" ,"jdbc:h2:mem:test", Main.DEFAULT_MAX_DB_CONNECTIONS);

		Main.setDefaultGsnConf("/gsn_test.xml");
	  	Main.getInstance();
	}

	//@Before
	//public void setUp() throws Exception {
	//	sm = StorageManager.getInstance();
	//	}


	@Test (expected=OperationNotSupportedException.class)
	public void testSendToWrapper1() throws OperationNotSupportedException {
		SystemTime systemTimeWrapper = new SystemTime();
		systemTimeWrapper.sendToWrapper("bla");
		systemTimeWrapper.dispose();
	}

	@Test
	public void testRemovingUselessData() throws SQLException, InterruptedException {
		SystemTime wrapper = new SystemTime();
		sm.executeCreateTable(wrapper.getDBAliasInStr(), new DataField[] {},true);
		wrapper.setActiveAddressBean(new AddressBean("system-time",new KeyValueImp(SystemTime.CLOCK_PERIOD_KEY,"100")));
		assertTrue(wrapper.initialize());
		Thread thread = new Thread(wrapper);
		InputStream is = new InputStream();
		StreamSource  ss = new StreamSource().setAlias("my-stream").setAddressing(new AddressBean[] {new AddressBean("system-time")}).setSqlQuery("select * from wrapper where TIMED <0").setRawHistorySize("2").setInputStream(is);   
		ss.setSamplingRate(1);
		ss.setWrapper(wrapper );
		assertTrue(ss.validate());
		assertEquals(wrapper.getTimerClockPeriod(), 100);
		thread.start();
		Thread.sleep(1000);
		
		ResultSet rs =sm.executeQueryWithResultSet(new StringBuilder("select count(*) from ").append(wrapper.getDBAliasInStr()), sm.getConnection());
		assertTrue(rs.next());
		//    System.out.println(rs.getInt(1));
		assertTrue(rs.getInt(1)<=(AbstractWrapper.GARBAGE_COLLECT_AFTER_SPECIFIED_NO_OF_ELEMENTS*2));
		wrapper.releaseResources();
		thread.stop();
	}

	@Test
	public void testOutOfOrderData() throws SQLException, InterruptedException{
		MockWrapper wrapper = new MockWrapper();
		sm.executeCreateTable(wrapper.getDBAliasInStr(), wrapper.getOutputFormat(),true);
		AddressBean addressBean = new AddressBean("test");
		wrapper.setActiveAddressBean(addressBean);
		assertTrue(wrapper.initialize());
		InputStream is = new InputStream();
		
		StreamSource  ss = new StreamSource().setAlias("my-stream").setAddressing(new AddressBean[] {addressBean}).setSqlQuery("select * from wrapper").setRawHistorySize("1").setInputStream(is);   
		ss.setSamplingRate(1);
		ss.setWrapper(wrapper );
		assertTrue(ss.validate());

		StreamElement se = new StreamElement(wrapper.getOutputFormat(), new Serializable[]{1000}, 1000L);
		assertTrue(wrapper.insertIntoWrapperTable(se));
		se = new StreamElement(wrapper.getOutputFormat(), new Serializable[]{2000}, 2000L);
		assertTrue(wrapper.insertIntoWrapperTable(se));
		se = new StreamElement(wrapper.getOutputFormat(), new Serializable[]{1500}, 1500L);
		assertFalse(wrapper.insertIntoWrapperTable(se));
		
		wrapper.releaseResources();
	}

}
