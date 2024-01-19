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

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;

import ch.epfl.gsn.Main;
import ch.epfl.gsn.beans.AddressBean;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.InputStream;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.StreamSource;
import ch.epfl.gsn.beans.windowing.WindowType;
import ch.epfl.gsn.storage.DataEnumerator;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.GSNRuntimeException;
import ch.epfl.gsn.wrappers.AbstractWrapper;
import ch.epfl.gsn.wrappers.SystemTime;

import java.lang.reflect.Method;
import java.util.TreeMap;

public class TestStreamElement {

	@Test
	public void testStreamElementEquals(){

		DataField[] datafields = new DataField[] {
			new DataField("DEVICE_ID", "INTEGER"),
			new DataField("DEVICE_TYPE", "SMALLINT"),
			new DataField("BEGIN", "BIGINT"),
			new DataField("END", "BIGINT"),
			new DataField("POSITION", "TINYINT"),
			new DataField("VALUE", "DOUBLE"),
			new DataField("DESCRIPTION", "VARCHAR(255)"),
			new DataField("COMMENT", "VARCHAR(255)"),
			new DataField("BINARY", "BINARY"),
		};

		Serializable[] data1 = new Serializable[] {
			12345,     
			(short) 1,         
			1234567L,          
			2345678L,         
			(byte) 1,
			10.5,
			"same description",
			"comment",
			new byte[]{1, 2, 3},
		};

		Serializable[] data2 = new Serializable[] {
			12345,     
			(short) 1,         
			1234567L,          
			2345678L, 
			(byte) 1,
			10.5,
			"same description",
			"different comment",
			new byte[]{1, 2, 3}
		};

		String[] fieldNamesToBeIgnored = new String[] {
			"COMMENT"
		};

        StreamElement se1 = new StreamElement(datafields, data1, System.currentTimeMillis()/2);
        StreamElement se2 = new StreamElement(datafields, data2, System.currentTimeMillis());

        assertTrue(se1.equalsIgnoreTimedAndFields(se2, fieldNamesToBeIgnored));
		assertEquals(57, se1.getVolume());
		assertEquals(67, se2.getVolume());
		assertFalse(se1.equalsIgnoreTimedAndFields(se2, new String[]{}));
		se2.setData("COMMENT", "comment");
		assertTrue(se1.equalsIgnoreTimedAndFields(se2, new String[]{}));
		assertEquals("Integer , SmallInt , BigInt , BigInt , TinyInt , Double , Varchar , Varchar , Binary , ", se1.getFieldTypesInString().toString());
	}

	@Test
	public void testStreamElementJsonConversion(){

		DataField[] outputFormat = new DataField[]{
            new DataField("DEVICE_ID", "INTEGER"),
			new DataField("START", "BIGINT"),
            new DataField("VALUE_DB", "DOUBLE"),
			new DataField("BINARY", "BINARY"),
        };

		Serializable[] data = new Serializable[] {
			12345,        
			1234567L,        
			10.5,
			new byte[]{1, 2, 3},
		};

        StreamElement se = new StreamElement(outputFormat, data, System.currentTimeMillis());
		String json = se.toJSON("testvs");
		assertNotNull(json);
		
		StreamElement[] streamElements = se.fromJSON(json);
		assertEquals(1, streamElements.length);
		assertTrue(se.equalsIgnoreTimedAndFields(streamElements[0], new String[]{}));
		assertEquals(2, (int) se.getType("DEVICE_ID"));
	}

	@Test
    public void testStreamElementTreeMapConstructor() {

		TreeMap<String, Serializable> output = new TreeMap<>();
        output.put("DEVICE_ID", 12345);
        output.put("DEVICE_TYPE", (short) 1);
        output.put("START", 1234567L);

        DataField[] fields = new DataField[] {
            new DataField("DEVICE_ID", "INTEGER"),
            new DataField("DEVICE_TYPE", "SMALLINT"),
            new DataField("START", "BIGINT"),
        };

        StreamElement se = new StreamElement(output, fields);
        assertArrayEquals(new String[]{"DEVICE_ID", "DEVICE_TYPE", "START"}, se.getFieldNames());
        assertArrayEquals(new Byte[]{DataTypes.INTEGER, DataTypes.SMALLINT, DataTypes.BIGINT}, se.getFieldTypes());
		assertEquals(14, se.getVolume());
    }

	@Test
    public void testStreamElemenOtherConstructor() {
        DataField[] fields = new DataField[] {
            new DataField("DEVICE_ID", "INTEGER"),
            new DataField("DEVICE_TYPE", "SMALLINT"),
            new DataField("START", "BIGINT"),
        };

		Serializable[] data = new Serializable[] {
			12345,     
			(short) 1,         
			1234567L,
		};

        StreamElement se1 = new StreamElement(fields, data);
		StreamElement se2 = new StreamElement(se1);

        assertArrayEquals(new String[]{"device_id", "device_type", "start"}, se2.getFieldNames());
        assertArrayEquals(new Byte[]{DataTypes.INTEGER, DataTypes.SMALLINT, DataTypes.BIGINT}, se2.getFieldTypes());
		assertEquals(14, se2.getVolume());
		assertTrue(se1.equalsIgnoreTimedAndFields(se2, new String[]{}));

		DataField[] furhter_fields = new DataField[] {
            new DataField("END", "INTEGER"),
            new DataField("COMMENT", "VARCHAR(255)")
        };

		Serializable[] further_data = new Serializable[] {
			23456,     
			"comment",
		};

		StreamElement se3 = new StreamElement(se1, furhter_fields, further_data);
		assertArrayEquals(new String[]{"device_id", "device_type", "start", "end", "comment"}, se3.getFieldNames());
        assertArrayEquals(new Byte[]{DataTypes.INTEGER, DataTypes.SMALLINT, DataTypes.BIGINT, DataTypes.INTEGER, DataTypes.VARCHAR}, se3.getFieldTypes());
		assertEquals(25, se3.getVolume());
		assertFalse(se1.equalsIgnoreTimedAndFields(se3, new String[]{}));

		String[] fieldNames4 = new String[] {
			"END",
			"COMMENT"
		};

		Byte[] fieldTypes4 = {DataTypes.INTEGER, DataTypes.VARCHAR};

		StreamElement se4 = new StreamElement(se1, fieldNames4, fieldTypes4, further_data);
		assertTrue(se3.equalsIgnoreTimedAndFields(se4, new String[]{}));
    }

	@Test
	public void testStreamElementREST(){
		
		DataField[] outputFormat = new DataField[]{
            new DataField("DEVICE_ID", "INTEGER"),
            new DataField("DEVICE_TYPE", "TINYINT"),
			new DataField("START", "BIGINT"),
            new DataField("VALUE_DB", "DOUBLE"),
			new DataField("VALUE_FL", "FLOAT"),
        };

        String[] fieldNames = new String[]{
            "DEVICE_ID",
            "DEVICE_TYPE",
			"START",
            "VALUE_DB",
			"VALUE_FL",
        };

        Object[] fieldValues = new Object[]{
            "12345",
            "1",
			"123456789",
            "10.5",
			"10.5",
        };
		

		StreamElement se = StreamElement.createElementFromREST(outputFormat, fieldNames, fieldValues);
		assertArrayEquals(new String[]{"device_id", "device_type", "start", "value_db", "value_fl"}, se.getFieldNames());
        assertArrayEquals(new Byte[]{DataTypes.INTEGER, DataTypes.TINYINT, DataTypes.BIGINT, DataTypes.DOUBLE, DataTypes.FLOAT}, se.getFieldTypes());
	}

	@Test
    public void testFromREST() {
        DataField[] outputFormat = new DataField[]{
            new DataField("DEVICE_ID", "INTEGER"),
            new DataField("DEVICE_TYPE", "TINYINT"),
			new DataField("START", "BIGINT"),
            new DataField("VALUE_DB", "DOUBLE"),
			new DataField("VALUE_FL", "FLOAT"),
			new DataField("DESCRIPTION", "VARCHAR(255)"),
			new DataField("BINARY", "BINARY"),
        };

        String[] fieldNames = new String[]{
            "DEVICE_ID",
            "DEVICE_TYPE",
			"START",
            "VALUE_DB",
			"VALUE_FL",
			"DESCRIPTION",
			"BINARY"
        };

        String[] fieldValues = new String[]{
            "12345",
            "1",
			"123456789",
            "10.5",
			"10.5",
			"description",
			"1"
        };

		String timestamp = "1638345600000";

        StreamElement se = StreamElement.fromREST(outputFormat, fieldNames, fieldValues, timestamp);
		assertArrayEquals(new String[]{"device_id", "device_type", "start", "value_db", "value_fl", "description", "binary"}, se.getFieldNames());
        assertArrayEquals(new Byte[]{DataTypes.INTEGER, DataTypes.TINYINT, DataTypes.BIGINT, DataTypes.DOUBLE, DataTypes.FLOAT, DataTypes.VARCHAR, DataTypes.BINARY}, se.getFieldTypes());
    }

	 @Test
    public void testGetDataInRPCFriendly() {
       
        DataField[] fields = new DataField[] {
            new DataField("DEVICE_ID", "INTEGER"),
            new DataField("VALUE", "DOUBLE"),
			new DataField("COMMENT", "VARCHAR(255)")
        };

		Serializable[] data = new Serializable[] {
			12345,     
			10.5,
			"comment"
		};

        StreamElement se = new StreamElement(fields, data);
        Object[] rpcFriendlyData = se.getDataInRPCFriendly();

        assertNotNull(rpcFriendlyData);
        assertEquals(12345, rpcFriendlyData[0]); 
        assertEquals(10.5, rpcFriendlyData[1]);
		assertEquals("comment", rpcFriendlyData[2]);
    }

	@Test
    public void testVerifyTypeCompatibility() throws NoSuchMethodException {

		DataField[] fields = new DataField[] {
            new DataField("DEVICE_ID", "INTEGER"),
            new DataField("VALUE", "DOUBLE"),
			new DataField("COMMENT", "VARCHAR(255)")
        };

		Serializable[] data = new Serializable[] {
			12345,     
			10.5,
			"comment"
		};

        StreamElement se = new StreamElement(fields, data);

		// Get the private method using reflection
        Method method = StreamElement.class.getDeclaredMethod("verifyTypeCompatibility", Byte.class, Serializable.class);
        method.setAccessible(true);

		try {
			method.invoke(se, DataTypes.TINYINT, (byte) 1);
			method.invoke(se, DataTypes.SMALLINT, (short) 2);
			method.invoke(se, DataTypes.BIGINT, 123456789L);
			method.invoke(se, DataTypes.CHAR, "TestString");
            method.invoke(se, DataTypes.INTEGER, 42);
            method.invoke(se, DataTypes.DOUBLE, 3.14);
            method.invoke(se, DataTypes.FLOAT, 2.71f);
            method.invoke(se, DataTypes.BINARY, new byte[]{1, 2, 3});
        } catch (Exception e) {
            fail("testVerifyTypeCompatibility failed: " + e.getMessage());
        }

		assertThrows(Exception.class, () -> method.invoke(se, DataTypes.TINYINT, (short) 1));
		assertThrows(Exception.class, () -> method.invoke(se, DataTypes.SMALLINT, 123456789L));
		assertThrows(Exception.class, () -> method.invoke(se, DataTypes.BIGINT, "TestString"));
		assertThrows(Exception.class, () -> method.invoke(se, DataTypes.CHAR, 2.71f));
		assertThrows(Exception.class, () -> method.invoke(se, DataTypes.INTEGER, "Invalid"));
		assertThrows(Exception.class, () -> method.invoke(se, DataTypes.DOUBLE, "Invalid"));
		assertThrows(Exception.class, () -> method.invoke(se, DataTypes.FLOAT, new byte[]{1, 2, 3}));
		assertThrows(Exception.class, () -> method.invoke(se, DataTypes.BINARY, 1234));

    }



}