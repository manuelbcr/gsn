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
import java.util.ArrayList;



public class TestDeviceMappings {

	@Test
	public void testDeviceMappings() {

        GeoMapping geoMapping = new GeoMapping(1, 10.5, 11.5, 12.5, "comment");
        GeoMapping geoMapping_tmp = new GeoMapping();
        assertNotNull(geoMapping);

        SensorMap sensorMap1 = new SensorMap(123L, null, "sensortype", 456L, "comment");
        SensorMap sensorMap2 = new SensorMap();
        SensorMap sensorMap3 = new SensorMap(123L, 234L, "sensortype_new", 456L, "comment_new");
        ArrayList<SensorMap> sMappings = new ArrayList<>();
        
        sMappings.add(sensorMap1);
        SensorMappings sensorMapping = new SensorMappings(1, sMappings);
        SensorMappings sensorMapping_tmp = new SensorMappings();
        sensorMapping.add(sensorMap3);
        assertNotNull(sensorMapping);

        PositionMap positionMap1 = new PositionMap(1, (short) 1, 123L, null, "comment");
        PositionMap positionMap2 = new PositionMap();
        PositionMap positionMap3 = new PositionMap(1, (short) 1, 123L, 234L, "comment_new");
        ArrayList<PositionMap> pMappings = new ArrayList<>();
        
        pMappings.add(positionMap1);
        PositionMappings positionMapping = new PositionMappings(1, pMappings);
        PositionMappings positionMapping_tmp = new PositionMappings();
        positionMapping.add(positionMap3);
        assertNotNull(positionMapping);

        ArrayList<PositionMappings> positionMappings = new ArrayList<>();
        positionMappings.add(positionMapping);
        ArrayList<SensorMappings> sensorMappings = new ArrayList<>();
        sensorMappings.add(sensorMapping);
        ArrayList<GeoMapping> geoMappings = new ArrayList<>();
        geoMappings.add(geoMapping);

        DeviceMappings deviceMappings = new DeviceMappings(positionMappings, sensorMappings, geoMappings);
        assertNotNull(deviceMappings);
        
	}
	
}