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

import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.keyvalue.DefaultKeyValue;

import java.io.IOException;

public class TestAddressBean {

    @Test
	public void testBeansInitializerContructor() {

        AddressBean addressBean1 = new AddressBean();
        assertNotNull(addressBean1);
        AddressBean addressBean2 = new AddressBean("wrapper2", null);
        assertNotNull(addressBean2);

        KeyValue[] kv = new KeyValue[]{
                new DefaultKeyValue("key1", "value1"),
                new DefaultKeyValue("key2", "value2"),
                new DefaultKeyValue("key3", "1"),
        };
        AddressBean addressBean3 = new AddressBean("wrapper3", kv);
        assertNotNull(addressBean3);
        assertEquals("value2", addressBean3.getPredicateValueWithException("key2"));
        assertEquals(1, addressBean3.getPredicateValueAsInt("key3", 2));
        assertEquals(2, addressBean3.getPredicateValueAsInt("key1", 2));
        assertEquals(1, addressBean3.getPredicateValueAsIntWithException("key3"));
        assertEquals("value1", addressBean3.getPredicateValueWithDefault("key1", "value_tmp"));
        assertEquals("value_tmp", addressBean3.getPredicateValueWithDefault("key4", "value_tmp"));
        assertNotNull(addressBean3.hashCode());
        
        AddressBean addressBean4 = new AddressBean("wrapper4");
        assertNotNull(addressBean4);

        assertTrue(addressBean4.equals(addressBean4));
        assertFalse(addressBean4.equals(null));
        assertFalse(addressBean4.equals(new Object()));

        //AddressBean addressBean5 = new AddressBean("wrapper5", kv);
        //AddressBean addressBean6 = new AddressBean("wrapper5", kv);
        //assertTrue(addressBean5.equals(addressBean6));

	}
	
}