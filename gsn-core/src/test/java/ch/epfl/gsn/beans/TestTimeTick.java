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

public class TestTimeTick {

    TimeTick timeTick1;
    TimeTick timeTick2;

    @Before
	public void setUp() {

        timeTick1 = new TimeTick();
        assertNotNull(timeTick1);
        timeTick2 = new TimeTick(12345678L);
        assertNotNull(timeTick2);

	}


    @Test
	public void testBeansInitializerContructor() {

        assertEquals(12345678L, timeTick2.getTick(), 0.0);
        timeTick1.setTick(87654321L);
        assertEquals(87654321L, timeTick1.getTick(), 0.0);

	}
	
}