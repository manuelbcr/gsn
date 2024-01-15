package ch.epfl.gsn.wrappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertFalse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

import ch.epfl.gsn.Main;
import ch.epfl.gsn.Mappings;
import ch.epfl.gsn.VirtualSensor;
import ch.epfl.gsn.beans.AddressBean;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.StreamSource;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import org.apache.commons.collections.KeyValue;
import ch.epfl.gsn.utils.KeyValueImp;
import ch.epfl.gsn.beans.InputStream;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;


public class TestGridDataWrapper {

    private GridDataWrapper gridDataWrapper;

    @Before
	public void setup() throws SQLException, IOException {

        gridDataWrapper = new GridDataWrapper();

        String currentWorkingDir = System.getProperty("user.dir");

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("extension", "txt"));
        predicates.add( new KeyValueImp("time-format", "yyyyMMdd"));
        predicates.add( new KeyValueImp("file-mask", "GridDataTest_(\\d{8})\\.txt"));
        predicates.add( new KeyValueImp("directory", currentWorkingDir + "../conf/"));
        predicates.add( new KeyValueImp("rate", "1000"));
        
        AddressBean ab = new AddressBean("grid-data",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("grid-data");

        gridDataWrapper.setActiveAddressBean(ab);
        assertTrue(gridDataWrapper.initialize());
        assertEquals("GridDataWrapper", gridDataWrapper.getWrapperName());
    }

    @Test
    public void testInitializeMissingExtension() {

        GridDataWrapper gridDataWrapper = new GridDataWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        predicates.add( new KeyValueImp("time-format", "yyyy-MM-dd"));
        predicates.add( new KeyValueImp("file-mask", "*.txt"));
        predicates.add( new KeyValueImp("directory", "/path/to/files"));
        predicates.add( new KeyValueImp("rate", "1000"));
        
        AddressBean ab = new AddressBean("grid-data",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("grid-data");

        gridDataWrapper.setActiveAddressBean(ab);

        assertFalse(gridDataWrapper.initialize());
    }

    @Test
    public void testInitializeMissingTimeFormat() {

        GridDataWrapper gridDataWrapper = new GridDataWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("extension", "txt"));
        predicates.add( new KeyValueImp("file-mask", "*.txt"));
        predicates.add( new KeyValueImp("directory", "/path/to/files"));
        predicates.add( new KeyValueImp("rate", "1000"));
        
        AddressBean ab = new AddressBean("grid-data",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("grid-data");

        gridDataWrapper.setActiveAddressBean(ab);

        assertFalse(gridDataWrapper.initialize());
    }

    @Test
    public void testInitializeMissingFileMask() {

        GridDataWrapper gridDataWrapper = new GridDataWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("extension", "txt"));
        predicates.add( new KeyValueImp("time-format", "yyyy-MM-dd"));
        predicates.add( new KeyValueImp("directory", "/path/to/files"));
        predicates.add( new KeyValueImp("rate", "1000"));
        
        AddressBean ab = new AddressBean("grid-data",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("grid-data");

        gridDataWrapper.setActiveAddressBean(ab);

        assertFalse(gridDataWrapper.initialize());
    }

    @Test
    public void testInitializeMissingDirectory() {

        GridDataWrapper gridDataWrapper = new GridDataWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("extension", "txt"));
        predicates.add( new KeyValueImp("time-format", "yyyy-MM-dd"));
        predicates.add( new KeyValueImp("file-mask", "*.txt"));
        predicates.add( new KeyValueImp("rate", "1000"));
        
        AddressBean ab = new AddressBean("grid-data",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("grid-data");

        gridDataWrapper.setActiveAddressBean(ab);

        assertFalse(gridDataWrapper.initialize());
    }

    @Test
    public void testInitializeMissingRate() {

        GridDataWrapper gridDataWrapper = new GridDataWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("extension", "txt"));
        predicates.add( new KeyValueImp("time-format", "yyyy-MM-dd"));
        predicates.add( new KeyValueImp("file-mask", "*.txt"));
        predicates.add( new KeyValueImp("directory", "/path/to/files"));
        
        AddressBean ab = new AddressBean("grid-data",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("grid-data");

        gridDataWrapper.setActiveAddressBean(ab);

        assertFalse(gridDataWrapper.initialize());
    }

    @Test
    public void testInitializeMissingWrongRate() {

        GridDataWrapper gridDataWrapper = new GridDataWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("extension", "txt"));
        predicates.add( new KeyValueImp("time-format", "yyyy-MM-dd"));
        predicates.add( new KeyValueImp("file-mask", "*.txt"));
        predicates.add( new KeyValueImp("directory", "/path/to/files"));
        predicates.add( new KeyValueImp("rate", "rate"));
        
        AddressBean ab = new AddressBean("grid-data",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("grid-data");

        gridDataWrapper.setActiveAddressBean(ab);

        assertFalse(gridDataWrapper.initialize());
    }

    @Test
    public void testGetOutputFormat() {
        GridDataWrapper wrapper = new GridDataWrapper();
        DataField[] outputFormat = wrapper.getOutputFormat();
        
        assertNotNull(outputFormat);
        assertEquals(7, outputFormat.length);
        
        assertEquals("ncols", outputFormat[0].getName());
        assertEquals("int", outputFormat[0].getType());
        assertEquals("number of columns", outputFormat[0].getDescription());
        
        assertEquals("nrows", outputFormat[1].getName());
        assertEquals("int", outputFormat[1].getType());
        assertEquals("number of rows", outputFormat[1].getDescription());
        
        assertEquals("xllcorner", outputFormat[2].getName());
        assertEquals("double", outputFormat[2].getType());
        assertEquals("xll corner", outputFormat[2].getDescription());
        
        assertEquals("yllcorner", outputFormat[3].getName());
        assertEquals("double", outputFormat[3].getType());
        assertEquals("yll corner", outputFormat[3].getDescription());
        
        assertEquals("cellsize", outputFormat[4].getName());
        assertEquals("double", outputFormat[4].getType());
        assertEquals("cell size", outputFormat[4].getDescription());
        
        assertEquals("nodata_value", outputFormat[5].getName());
        assertEquals("double", outputFormat[5].getType());
        assertEquals("no data value", outputFormat[5].getDescription());
        
        assertEquals("grid", outputFormat[6].getName());
        assertEquals("binary:image/raw", outputFormat[6].getType());
        assertEquals("raw raster data", outputFormat[6].getDescription());
    }

    @Test
    public void testParseFile() {

        String currentWorkingDir = System.getProperty("user.dir");
        assertTrue(gridDataWrapper.parseFile(currentWorkingDir + "../conf/GridDataTest_20240101.txt"));
    }

    @Test
    public void testRun() {
        gridDataWrapper.start();

        try {
            // Sleep for 3 seconds (3000 milliseconds)
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // Handle the InterruptedException (if necessary)
            e.printStackTrace();
        }

        gridDataWrapper.stop();
        gridDataWrapper.dispose();
    }

    @Test
    public void testDeserialize() {
        Double[][] original = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(original);
            out.close();
            
            byte[] bytes = bos.toByteArray();
            GridDataWrapper.testDeserialize(bytes);

        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

}