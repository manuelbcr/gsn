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

public class TestInputInfo {

    InputInfo inputInfo1;
    InputInfo inputInfo2;
    InputInfo inputInfo3;

    @Before
	public void setUp() {

        inputInfo1 = new InputInfo();
        assertNotNull(inputInfo1);
        inputInfo2 = new InputInfo("producer", "info", true);
        assertNotNull(inputInfo2);
        inputInfo3 = new InputInfo("producer3", "info3", true);
        assertNotNull(inputInfo2);
	}


    @Test
	public void testInputInfo() {

        inputInfo2.addInfo("producer1", "info1", false);
        inputInfo2.addInfo(inputInfo1);
        assertTrue(inputInfo2.hasAtLeastOneSuccess());
        assertFalse(inputInfo1.hasAtLeastOneSuccess());
        assertEquals(2, inputInfo2.getInfoList().size());
        assertEquals(0, inputInfo1.getInfoList().size());
        assertEquals("info (producer)\ninfo1 (producer1)\n", inputInfo2.toString());
        inputInfo3.addInfo(inputInfo2);
        assertEquals(3, inputInfo3.getInfoList().size());
	}

	
}