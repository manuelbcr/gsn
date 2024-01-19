package ch.epfl.gsn.vsensor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.imageio.ImageIO;

import org.apache.commons.collections.KeyValue;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.epfl.gsn.Main;
import ch.epfl.gsn.Mappings;
import ch.epfl.gsn.VirtualSensor;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class DemoVSensorTest {

    private static StorageManager sm;
    private VSensorConfig testVsensorConfig;

    @BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		// Setup current working directory
        String currentWorkingDir = System.getProperty("user.dir");
		if (!currentWorkingDir.endsWith("/gsn-core/")) {
			String newDirectory = currentWorkingDir + "/gsn-core/";
        	System.setProperty("user.dir", newDirectory);
		}

		DriverManager.registerDriver( new org.h2.Driver( ) );
		sm = StorageManagerFactory.getInstance( "org.h2.Driver","sa","" ,"jdbc:h2:mem:coreTest", Main.DEFAULT_MAX_DB_CONNECTIONS);

		Main.setDefaultGsnConf("/gsn_test.xml");
		Main.getInstance();
	}

    @Before
	public void setup() throws SQLException, IOException {
        DataField[] fields = new DataField[]{
            new DataField("STATUS", DataTypes.VARCHAR),
            new DataField("ID", DataTypes.VARCHAR)
        };
        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("testdemoVS");
		File someFile = File.createTempFile("testdemoVS", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.DemoVSensor");
		testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);
        KeyValue[] emptyAddressingArray = new KeyValue[0];
        testVsensorConfig.setAddressing(emptyAddressingArray);
        VirtualSensor pool = new VirtualSensor(testVsensorConfig);
        Mappings.addVSensorInstance(pool);

        
        sm.executeCreateTable("testdemoVS", fields, true);
    }

	@After
	public void teardown() throws SQLException {
		sm.executeDropTable("testdemoVS");
	}
    @Test
    public void testInitialize() throws IOException {
        DemoVSensor vs = new DemoVSensor();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
        File tempImageFile = createTemporaryImage();
        byte[] imageData = Files.readAllBytes(tempImageFile.toPath());
        vs.dataAvailable("CSTREAM", new StreamElement(
                new String[]{"IMAGE"},
                new Byte[]{DataTypes.BINARY},
                new Serializable[]{imageData},
                System.currentTimeMillis()
        ));
        vs.dataAvailable("SSTREAM", new StreamElement(new String[]{"STATUS", "ID"},new Byte[]{DataTypes.VARCHAR, DataTypes.VARCHAR},new Serializable[]{"add", "mica"},System.currentTimeMillis()));
        vs.dataAvailable("SSTREAM", new StreamElement(new String[]{"STATUS", "ID"},new Byte[]{DataTypes.VARCHAR, DataTypes.VARCHAR},new Serializable[]{"add", "add"},System.currentTimeMillis()));
        vs.dataAvailable("SSTREAM", new StreamElement(new String[]{"STATUS", "ID"},new Byte[]{DataTypes.VARCHAR, DataTypes.VARCHAR},new Serializable[]{"add", "remove"},System.currentTimeMillis()));


        vs.dataAvailable("SSTREAM", new StreamElement(new String[]{"STATUS", "ID"},new Byte[]{DataTypes.VARCHAR, DataTypes.VARCHAR},new Serializable[]{"add", "add"},System.currentTimeMillis()));
        vs.dataAvailable("SSTREAM", new StreamElement(new String[]{"STATUS", "ID"},new Byte[]{DataTypes.VARCHAR, DataTypes.VARCHAR},new Serializable[]{"add", "add"},System.currentTimeMillis()));
        vs.dataAvailable("SSTREAM", new StreamElement(new String[]{"STATUS", "ID"},new Byte[]{DataTypes.VARCHAR, DataTypes.VARCHAR},new Serializable[]{"add", "add"},System.currentTimeMillis()));
        tempImageFile = createTemporaryImage();
        imageData = Files.readAllBytes(tempImageFile.toPath());
        vs.dataAvailable("CSTREAM", new StreamElement(
                new String[]{"IMAGE"},
                new Byte[]{DataTypes.BINARY},
                new Serializable[]{imageData},
                System.currentTimeMillis()
        ));
        vs.dispose();
    }


    private File createTemporaryImage() throws IOException {
        File tempFile = File.createTempFile("tempImage", ".jpg");

        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        assertNotNull(image);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        Files.write(tempFile.toPath(), baos.toByteArray());

        return tempFile;
    }
}
