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
* File: src/ch/epfl/gsn/utils/TestValidityTools.java
*
* @author Ali Salehi
* @author Timotee Maret
*
*/

package ch.epfl.gsn.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.UnknownHostException;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.epfl.gsn.Main;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.GSNRuntimeException;
import ch.epfl.gsn.utils.ValidityTools;

import org.junit.Ignore;


public class TestValidityTools {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Setup current working directory
        String currentWorkingDir = System.getProperty("user.dir");
		if (!currentWorkingDir.endsWith("/gsn-core/")) {
			String newDirectory = currentWorkingDir + "/gsn-core/";
        	System.setProperty("user.dir", newDirectory);
		}
	}


	@Test
	public void testFileAccess() {
		String updatedWorkingDir = System.getProperty("user.dir");
		ValidityTools.checkAccessibilityOfFiles ( "../conf/wrappers.properties", "../conf/gsn.xml");
	}

	@Test
    public void testIsNotAccessibleSocket() throws UnknownHostException {
        assertFalse(ValidityTools.isAccessibleSocket("localhost", 9999, 3000));
		assertFalse(ValidityTools.isAccessibleSocket("localhost", 9999));
    }

	@Test
	public void testInvalidParameters() throws UnknownHostException, RuntimeException {
        ValidityTools.isAccessibleSocket(null, -1, 3000);
    }

    @Test
    public void testGetHostName() {
        assertEquals("example.com", ValidityTools.getHostName("example.com:8080"));
    }

    @Test
    public void testGetPortNumber() {
        assertEquals(8080, ValidityTools.getPortNumber("example.com:8080"));
    }

	@Test
    public void testIsLocalhost() {
        assertTrue(ValidityTools.isLocalhost("localhost"));
        assertTrue(ValidityTools.isLocalhost("127.0.0.1"));
        assertFalse(ValidityTools.isLocalhost("example.com"));
    }

	@Test
    public void testIsValidJavaVariable() {
        assertTrue(ValidityTools.isValidJavaVariable("validIdentifier"));
        assertFalse(ValidityTools.isValidJavaVariable("123invalid"));
        assertFalse(ValidityTools.isValidJavaVariable(null));
    }

	@Test(expected = ClassNotFoundException.class)
    public void testInvaliIsDBAccessible() throws ClassNotFoundException, SQLException {
        ValidityTools.isDBAccessible("invalidDriverClass", "invalid", "invalid", "invalid");
    }

	@Test
    public void testIsInt() {
        assertTrue(ValidityTools.isInt("123"));
        assertTrue(ValidityTools.isInt("-456"));
        assertTrue(ValidityTools.isInt("0"));
		assertFalse(ValidityTools.isInt(null));
		assertFalse(ValidityTools.isInt("abc"));
        assertFalse(ValidityTools.isInt("12.34"));
        assertFalse(ValidityTools.isInt("   "));
    }
}
