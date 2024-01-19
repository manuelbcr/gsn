package ch.epfl.gsn.storage;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.collections.KeyValue;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.epfl.gsn.Main;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.VSensorConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StorageManagerTest {
    private static StorageManager sm;
    private VSensorConfig testVsensorConfig;
    private ArrayList < KeyValue > params;

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
            new DataField("field1", DataTypes.FLOAT),
            new DataField("field2", DataTypes.INTEGER),
            new DataField("field3", DataTypes.INTEGER),
            new DataField("field4", DataTypes.DOUBLE),
        };
        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("teststoragemanager");
		File someFile = File.createTempFile("teststoragemanager", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.DemoVSensor");
		testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);
        KeyValue[] emptyAddressingArray = new KeyValue[0];
        testVsensorConfig.setAddressing(emptyAddressingArray);

        sm.executeCreateTable("teststoragemanager", fields, true);
    }

    @After
    public void teardown() throws SQLException {
        sm.executeDropTable("teststoragemanager");
    }

    @Test
    public void testfunctions() throws SQLException{
        DataField[] fields = new DataField[]{
            new DataField("field1", DataTypes.FLOAT),
            new DataField("field2", DataTypes.INTEGER),
            new DataField("field3", DataTypes.INTEGER),
            new DataField("field4", DataTypes.DOUBLE),
        };
        StreamElement streamElement1 = new StreamElement(
            new String[]{"field1", "field2","field3","field4"},
            new Byte[]{DataTypes.FLOAT, DataTypes.INTEGER,DataTypes.INTEGER,DataTypes.DOUBLE},
            new Serializable[]{6.321f, 2,123,23.03},
            System.currentTimeMillis()+2500); 

        sm.executeInsert("teststoragemanager", fields, streamElement1);

        assertFalse(sm.isOracle());
        assertFalse(sm.isSqlServer());
        assertEquals("org.h2.Driver", sm.getJDBCDriverClass());
        Connection con = sm.getConnection();
        assertTrue(sm.tableExists("teststoragemanager"));
        assertTrue(sm.tableExists("teststoragemanager", con));
        assertEquals(fields[0].getName(),sm.tableToStructure("teststoragemanager", con)[1].getName().toLowerCase());
        assertEquals(sm.tableToStructure("teststoragemanager", con)[1],sm.tableToStructureByString("teststoragemanager", con)[0]);
        assertEquals("alter table teststoragemanager rename to teststoragemanager2",sm.getStatementRenameTable("teststoragemanager", "teststoragemanager2").toString());
        assertEquals("DROP INDEX field1",sm.getStatementDropIndex("field1","teststoragemanager",con).toString());
        sm.executeCommand("SELECT * FROM teststoragemanager", con);
        assertTrue(sm.getStatementCreateIndexOnField("teststoragemanager","field1",true).toString().contains("CREATE  UNIQUE  INDEX"));
        sm.executeRenameTable("teststoragemanager", "teststoragemanager2");
        assertTrue(sm.tableExists("teststoragemanager2"));
        sm.executeRenameTable("teststoragemanager2", "teststoragemanager");
        assertTrue(sm.tableExists("teststoragemanager"));
        assertEquals("delete from teststoragemanager where teststoragemanager.timed < -1 - 1000",sm.getStatementRemoveUselessDataTimeBased("teststoragemanager", 1000).toString());
    }
}
