package ch.epfl.gsn.vsensor;

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


    @Before
	public void setup() throws SQLException, IOException {
        DataField[] fields = new DataField[]{
            new DataField("xy", DataTypes.VARCHAR),
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

}
