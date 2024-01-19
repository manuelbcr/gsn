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

package ch.epfl.gsn.beans.json;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;

import java.io.IOException;

public class TestGeoJsonStats {

    GeoJsonStats geoJsonStats;

    @Before
	public void setUp() {

        geoJsonStats = new GeoJsonStats();
        geoJsonStats.setStartDatetime(123456L);
        geoJsonStats.setEndDatetime(234567L);

	}

    @Test
	public void TestGeoJsonStats() {

        assertEquals(123456L, geoJsonStats.getStartDatetime(), 0.0);
        assertEquals(234567L, geoJsonStats.getEndDatetime(), 0.0);

	}
	
}