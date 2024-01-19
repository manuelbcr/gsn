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
import ch.epfl.gsn.utils.GSNRuntimeException;


public class TestDataField {

    DataField testDataField;

        @Before
        public void setUp() throws GSNRuntimeException {
                testDataField = new DataField("DESCRIPTION", "VARCHAR", 255, "Example description");
                assertNotNull(testDataField);
	}

	@Test
	public void testDataFieldEquals() throws GSNRuntimeException {
        
                DataField testField = new DataField("DESCRIPTION", "VARCHAR", 255, "Example description");
                assertTrue(testDataField.equals(testField));

                DataField testField1 = new DataField();
                DataField testField2 = new DataField();
                assertTrue(testField1.equals(testField2));
                assertFalse(testDataField.equals(testField1));

                testField2.setName("VALUE");
                testField2.setType("FLOAT");
                testField2.setDescription("some value");
                testField2.setUnit("some unit");
                testField2.setIndex("true");
                assertTrue(testField2.getIndex());
                testField2.setIndex(false);
                assertFalse(testField2.getIndex());
                testField2.setIndex(true);
                testField2.setIndex("false");
                assertFalse(testField2.getIndex());
                assertEquals(testField2.getDescription(), "some value");
                assertEquals(testField2.getUnit(), "some unit");
                assertFalse(testField1.equals(testField2));
	}

        @Test
	public void testDataFieldToString() throws GSNRuntimeException {
                assertNotNull(testDataField.toString());
	}

        @Test
	public void testDataFieldHashCode() throws GSNRuntimeException {
                assertNotNull(testDataField.hashCode());
	}
}