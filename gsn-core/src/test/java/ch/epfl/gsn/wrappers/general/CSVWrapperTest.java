package ch.epfl.gsn.wrappers.general;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.io.FileWriter;  

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.epfl.gsn.beans.AddressBean;
import ch.epfl.gsn.utils.KeyValueImp;

public class CSVWrapperTest {

	private final String CSV_FILE_NAME =  "testWrapper.csv"; 
	private final String CHECK_POINT_DIR = "csv-check-points";
    
    private String filePath;
    private String directoryPath;
    private String checkpointsDir;
    private CSVWrapper wrapper;
    private File directory;
    private File checkPointsDirectory;

    @Before
	public void setup(){

        String currentWorkingDir = System.getProperty("user.dir");

        //prepare directory
        directoryPath = currentWorkingDir + "../conf/csvWrapperTest";
        checkpointsDir = currentWorkingDir + "../" + CHECK_POINT_DIR;
        directory = new File(directoryPath);
        checkPointsDirectory = new File(checkpointsDir);
        boolean isDirectoryCreated = directory.mkdir();

        String fields = "TIMED, air_temp , TIMEd , AiR_TeMp2, comments";
        String formats = "Timestamp(d.M.y ) , Numeric , timestamp(k:m) , numeric ,String   ";
        String data = "01.01.2009,1,10:10,10,\"Ali Salehi\"\n" +
                "01.01.2009,2,10:11,11,\"Ali Salehi\"\n" +
                 "01.01.2009,3,10:12,12,\"Ali Salehi\"\n";

        //prepare file
        if (isDirectoryCreated) {

            // Specify the path for the image file within the created directory
            filePath = directoryPath + "/csvWrapperTest.csv";

            generateCsvFile(filePath, fields, formats, data);

        } else {
            System.out.println("Failed to create directory.");
        }
    
        wrapper = new CSVWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        predicates.add( new KeyValueImp("file", filePath));
        predicates.add( new KeyValueImp("fields", fields));
        predicates.add( new KeyValueImp("formats", formats));
        predicates.add( new KeyValueImp("separator", ","));
        predicates.add( new KeyValueImp("check-point-directory", CHECK_POINT_DIR));
        predicates.add( new KeyValueImp("quote", "\""));
        predicates.add( new KeyValueImp("skip-first-lines", "1"));
        predicates.add( new KeyValueImp("timezone", "UTC"));
        predicates.add( new KeyValueImp("bad-values", "0"));
        predicates.add( new KeyValueImp("use-counter-for-check-point", "false"));
        predicates.add( new KeyValueImp("sampling", "1000"));
        
        AddressBean ab = new AddressBean("csvWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("csvWrapper");

        wrapper.setActiveAddressBean(ab);
        
        assertTrue(wrapper.initialize());
        assertEquals("ch.epfl.gsn.wrappers.general.CSVWrapper", wrapper.getWrapperName());
        
    }

    @After
	public void tearDown() throws Exception {
        wrapper.dispose();
	    deleteDirectory(directory);
        deleteDirectory(checkPointsDirectory);
	}

    @Test
    public void testInitialize(){

        CSVWrapper wrapper = new CSVWrapper();

        String fields = "TIMED, air_temp , TIMEd , AiR_TeMp2, comments";
        String formats = "Timestamp(d.M.y ) , Numeric , timestamp(k:m) , numeric ,String   ";

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        predicates.add( new KeyValueImp("file", filePath));
        predicates.add( new KeyValueImp("fields", fields));
        predicates.add( new KeyValueImp("formats", formats));
        predicates.add( new KeyValueImp("separator", ";;"));
        predicates.add( new KeyValueImp("check-point-directory", CHECK_POINT_DIR));
        predicates.add( new KeyValueImp("quote", "''"));
        predicates.add( new KeyValueImp("skip-first-lines", "1"));
        predicates.add( new KeyValueImp("timezone", "UTC"));
        predicates.add( new KeyValueImp("bad-values", "0"));
        predicates.add( new KeyValueImp("use-counter-for-check-point", "true"));
        predicates.add( new KeyValueImp("sampling", "1000"));
        
        AddressBean ab = new AddressBean("csvWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("csvWrapper");

        wrapper.setActiveAddressBean(ab);
        
        assertTrue(wrapper.initialize());

    }


    @Test
    public void testList(){
        assertEquals("name = 1706013249000 (01/23/2024 12:34:09,000)", wrapper.list("name", 1706013249000L));
    }

    @Test
    public void testDebugInfo(){
        wrapper.DEBUG_INFO("name");
        String currentWorkingDir = System.getProperty("user.dir");
        File debugFile = new File(currentWorkingDir + "../DEBUG_INFO_-3.txt");
        assertTrue(debugFile.exists());
        debugFile.delete();
    }

    @Test
    public void testRun(){

        wrapper.start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        wrapper.stop();
        
    }

    private static void generateCsvFile(String fileName, String fields, String formats, String data) {
        try (FileWriter writer = new FileWriter(fileName)) {
            // Write CSV header
            String[] fieldArray = fields.split("\\s*,\\s*");
            writer.append(String.join(",", fieldArray)).append("\n");

            // Write data rows
            String[] dataArray = data.split("\n");
            writer.append(String.join("\n", dataArray));

            System.out.println("CSV file '" + fileName + "' has been generated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
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