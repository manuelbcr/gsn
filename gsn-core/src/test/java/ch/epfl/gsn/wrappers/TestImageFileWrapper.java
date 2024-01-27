package ch.epfl.gsn.wrappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.epfl.gsn.beans.AddressBean;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.utils.KeyValueImp;

public class TestImageFileWrapper {

    private ImageFileWrapper wrapper;
    private File directory;

    @Before
	public void setup() throws SQLException, IOException {


        // setup folder
        String currentWorkingDir = System.getProperty("user.dir");
        String directoryPath = currentWorkingDir + "../conf/imageFileWrapperTest";
        directory = new File(directoryPath);
        boolean isDirectoryCreated = directory.mkdir();

        if (isDirectoryCreated) {
            byte[] jpegImageBytes = generateRandomJpegImageBytes(1024, 1024);

            // Specify the path for the image file within the created directory
            String imagePath = directoryPath + "/ImageTest_20240101.jpg";

            // Writing the image bytes to the file
            try (FileOutputStream fos = new FileOutputStream(imagePath)) {
                fos.write(jpegImageBytes);
            } catch (IOException e) {
                System.out.println("Failed to write image to file: " + e.getMessage());
            }
        } else {
            System.out.println("Failed to create directory.");
        }

        wrapper = new ImageFileWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("directory", directoryPath));
        predicates.add( new KeyValueImp("file-mask", "ImageTest_(\\d{8})\\.jpg"));
        predicates.add( new KeyValueImp("time-format", "yyyyMMdd"));
        predicates.add( new KeyValueImp("extension", ".jpeg"));
        predicates.add( new KeyValueImp("rate", "1"));

        AddressBean ab = new AddressBean("imageFileWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("imageFileWrapper");

        wrapper.setActiveAddressBean(ab);

        assertTrue(wrapper.initialize());
        assertEquals("ImageFileWrapper", wrapper.getWrapperName());
    }

    @After
	public void teardown() throws SQLException {
        wrapper.dispose();
        deleteDirectory(directory);
	}

    @Test
    public void testInitializeNoExtension(){

        ImageFileWrapper wrapper = new ImageFileWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("directory", "directory"));
        predicates.add( new KeyValueImp("file-mask", "ImageTest_(\\d{8})\\.jpg"));
        predicates.add( new KeyValueImp("time-format", "yyyyMMdd"));
        predicates.add( new KeyValueImp("rate", "1"));
		
        AddressBean ab = new AddressBean("imageFileWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("imageFileWrapper");

        wrapper.setActiveAddressBean(ab);

        assertFalse(wrapper.initialize());

    }


    @Test
    public void testInitializeNoTimeFormat(){

        ImageFileWrapper wrapper = new ImageFileWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("directory", "directoryPath"));
        predicates.add( new KeyValueImp("file-mask", "ImageTest_(\\d{8})\\.jpg"));
        predicates.add( new KeyValueImp("extension", ".jpeg"));
        predicates.add( new KeyValueImp("rate", "1"));
		
        AddressBean ab = new AddressBean("imageFileWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("imageFileWrapper");

        wrapper.setActiveAddressBean(ab);

        assertFalse(wrapper.initialize());

    }

    @Test
    public void testInitializeNoFileMask(){

        ImageFileWrapper wrapper = new ImageFileWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("directory", "directoryPath"));
        predicates.add( new KeyValueImp("time-format", "yyyyMMdd"));
        predicates.add( new KeyValueImp("extension", ".jpeg"));
        predicates.add( new KeyValueImp("rate", "1"));
		
        AddressBean ab = new AddressBean("imageFileWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("imageFileWrapper");

        wrapper.setActiveAddressBean(ab);

        assertFalse(wrapper.initialize());

    }

    @Test
    public void testInitializeNoDirectory(){

        ImageFileWrapper wrapper = new ImageFileWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        predicates.add( new KeyValueImp("file-mask", "ImageTest_(\\d{8})\\.jpg"));
        predicates.add( new KeyValueImp("time-format", "yyyyMMdd"));
        predicates.add( new KeyValueImp("extension", ".jpeg"));
        predicates.add( new KeyValueImp("rate", "1"));
		
        AddressBean ab = new AddressBean("imageFileWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("imageFileWrapper");

        wrapper.setActiveAddressBean(ab);

        assertFalse(wrapper.initialize());

    }

    @Test
    public void testInitializeNoRate(){

        ImageFileWrapper wrapper = new ImageFileWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("directory", "directoryPath"));
        predicates.add( new KeyValueImp("file-mask", "ImageTest_(\\d{8})\\.jpg"));
        predicates.add( new KeyValueImp("time-format", "yyyyMMdd"));
        predicates.add( new KeyValueImp("extension", ".jpeg"));
		
        AddressBean ab = new AddressBean("imageFileWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("imageFileWrapper");

        wrapper.setActiveAddressBean(ab);

        assertFalse(wrapper.initialize());

    }

    @Test
    public void testInitializeWrongRate(){

        ImageFileWrapper wrapper = new ImageFileWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        predicates.add( new KeyValueImp("directory", "directoryPath"));
        predicates.add( new KeyValueImp("file-mask", "ImageTest_(\\d{8})\\.jpg"));
        predicates.add( new KeyValueImp("time-format", "yyyyMMdd"));
        predicates.add( new KeyValueImp("extension", ".jpeg"));
        predicates.add( new KeyValueImp("rate", "abc"));
		
        AddressBean ab = new AddressBean("imageFileWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("imageFileWrapper");

        wrapper.setActiveAddressBean(ab);

        assertFalse(wrapper.initialize());

    }

    @Test
    public void testRun(){

        wrapper.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        wrapper.stop();

    } 


    //Image generation
    public static byte[] generateRandomJpegImageBytes(int width, int height) {
        try {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            
            // Generate random pixel values
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = getRandomRgb();
                    image.setRGB(x, y, rgb);
                }
            }

            // Convert BufferedImage to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpeg", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int getRandomRgb() {
        int r = (int) (Math.random() * 256);
        int g = (int) (Math.random() * 256);
        int b = (int) (Math.random() * 256);
        return (r << 16) | (g << 8) | b;
    }

    private static boolean deleteDirectory(File directory) {
        if (!directory.exists()) {
            return false;
        }

        // List all files and subdirectories in the directory
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Recursive call to delete subdirectories
                    if (!deleteDirectory(file)) {
                        return false;
                    }
                } else {
                    // Delete the file
                    if (!file.delete()) {
                        return false;
                    }
                }
            }
        }

        // Finally, delete the empty directory itself
        return directory.delete();
    }
    
}
