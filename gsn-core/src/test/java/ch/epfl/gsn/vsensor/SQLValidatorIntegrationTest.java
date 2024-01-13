package ch.epfl.gsn.vsensor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.validation.Validator;

import org.apache.commons.collections.KeyValue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.epfl.gsn.Main;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.storage.SQLValidator;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.KeyValueImp;

public class SQLValidatorIntegrationTest {
    private static StorageManager sm;
    private VSensorConfig testVsensorConfig;
    ArrayList < KeyValue > params;


    @BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		// Setup current working directory
        String currentWorkingDir = System.getProperty("user.dir");
		if (!currentWorkingDir.endsWith("/gsn-core/")) {
			String newDirectory = currentWorkingDir + "/gsn-core/";
        	System.setProperty("user.dir", newDirectory);
		}

		DriverManager.registerDriver( new org.h2.Driver( ) );
		sm = StorageManagerFactory.getInstance( "org.h2.Driver","sa","" ,"jdbc:h2:mem:test", Main.DEFAULT_MAX_DB_CONNECTIONS);

		Main.setDefaultGsnConf("/gsn_test.xml");
		Main.getInstance();
	}


    @Before
	public void setup() throws SQLException, IOException {
        DataField[] fields = new DataField[]{
            new DataField("xy", DataTypes.DOUBLE),
            new DataField("z", DataTypes.INTEGER)
        };
        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("testconfig");
        File someFile = File.createTempFile("testconfig", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.BridgeVirtualSensor");
        testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);
        params = new ArrayList < KeyValue >( );
        params.add( new KeyValueImp( "rate" , "10" ) );
    
        testVsensorConfig.setMainClassInitialParams( params );
        
    }   

    @Test
    public void testVsLoading() throws Exception {
        SQLValidatorIntegration validator = new SQLValidatorIntegration(SQLValidator.getInstance());
        boolean result = validator.vsLoading(testVsensorConfig);
        assertTrue(result);
        validator.release();
    }
    @Test
    public void testVsUnloading() throws Exception {
        SQLValidatorIntegration validator = new SQLValidatorIntegration(SQLValidator.getInstance());
        boolean result = validator.vsUnLoading(testVsensorConfig);
        assertTrue(result);
        validator.release();
    }

    @Test
    public void testVsUnloadingFailure() throws SQLException {
        SQLValidatorIntegration validator = new SQLValidatorIntegration(SQLValidator.getInstance());
        VSensorConfig config = null;
        validator.vsLoading(null);
        boolean result = validator.vsUnLoading(config);
        assertFalse(result);
    }

    @Test
    public void testSQLValidator() throws SQLException{
        DataField[] fields = new DataField[]{
            new DataField("xy", DataTypes.DOUBLE),
            new DataField("z", DataTypes.INTEGER)
        };
        sm.executeCreateTable("testconfig", fields, true);
        StreamElement streamElement1 = new StreamElement(
            new String[]{"xy", "z"},
            new Byte[]{DataTypes.DOUBLE, DataTypes.INTEGER},
            new Serializable[]{22.21465,123},
            System.currentTimeMillis()+200);
        sm.executeInsert("testconfig", fields, streamElement1);
        SQLValidator validator=null;
        try {
            
            validator = SQLValidator.getInstance();
            assertNotNull(validator);
        } catch (SQLException e) {
            fail("SQLValidator not created");
        }
        
        assertEquals("select*from ",SQLValidator.removeQuotes("select*from \"testconfig\""));
        assertEquals("select* testconfig",SQLValidator.removeSingleQuotes("select*'from' testconfig"));
        assertEquals("TESTCONFIG", validator.validateQuery("SELECT * FROM testconfig"));
        assertFalse(validator.vsUnLoading(testVsensorConfig));
        assertFalse(validator.vsLoading(testVsensorConfig));
        assertEquals(fields[0].getName(),validator.extractSelectColumns("SELECT * FROM testconfig", testVsensorConfig)[0].getName().toLowerCase());
        assertEquals("select pk, xy from testconfig",SQLValidator.addPkField("select xy from testconfig"));
        assertEquals("select * from testconfig order by TIMED desc limit 1",SQLValidator.addTopFirst("select * from testconfig"));
        sm.executeDropTable("testconfig");
    }

}
