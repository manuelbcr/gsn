package ch.epfl.gsn.wrappers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.Serializable;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.epfl.gsn.Main;
import ch.epfl.gsn.beans.AddressBean;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.KeyValueImp;

public class TestReplayWrapper {

    private static StorageManager sm;
    private ReplayWrapper wrapper;

    @BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		// Setup current working directory
        String currentWorkingDir = System.getProperty("user.dir");
		if (!currentWorkingDir.endsWith("/gsn-core/")) {
			String newDirectory = currentWorkingDir + "/gsn-core/";
        	System.setProperty("user.dir", newDirectory);
		}

		DriverManager.registerDriver( new org.h2.Driver( ) );
		//sm = StorageManagerFactory.getInstance( "org.h2.Driver","sa","" ,"jdbc:h2:mem:coreTest", Main.DEFAULT_MAX_DB_CONNECTIONS);
        
		Main.setDefaultGsnConf("/gsn_test.xml");
		Main.getInstance();
        sm = StorageManagerFactory.getInstance( "org.h2.Driver","sa","" ,"jdbc:h2:mem:coreTest", Main.DEFAULT_MAX_DB_CONNECTIONS);
	}

    @Before
	public void setup() throws SQLException, IOException {

        DataField[] positionStructure = new DataField[] {
            new DataField("timed", DataTypes.BIGINT),
            new DataField("DEVICE_ID", DataTypes.INTEGER),
            new DataField("DEVICE_TYPE", DataTypes.SMALLINT),
            new DataField("BEGIN", DataTypes.BIGINT),
            new DataField("END", DataTypes.BIGINT),
            new DataField("POSITION", DataTypes.INTEGER),
            new DataField("COMMENT", DataTypes.VARCHAR)
        };


        sm.executeCreateTable("replayWrapper", positionStructure, true);
    
        StreamElement streamElement = new StreamElement(
                    new String[]{"timed", "DEVICE_ID", "DEVICE_TYPE", "BEGIN", "END", "POSITION", "COMMENT"},
                    new Byte[]{DataTypes.BIGINT, DataTypes.INTEGER, DataTypes.SMALLINT, DataTypes.BIGINT, DataTypes.BIGINT, DataTypes.INTEGER, DataTypes.VARCHAR},
                    new Serializable[]{1257946505000L, 1, (short) 1, 123456L, 234567L, 1, "postion 1"},
                    System.currentTimeMillis());
            
        sm.executeInsert("replayWrapper", positionStructure, streamElement);

        wrapper = new ReplayWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("dbname", "replayWrapper"));
        predicates.add( new KeyValueImp("speed", "1"));

        AddressBean ab = new AddressBean("replayWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("replayWrapper");

        wrapper.setActiveAddressBean(ab);

        assertTrue(wrapper.initialize());
        assertEquals("ReplayWrapper", wrapper.getWrapperName());
    }

    @After
	public void teardown() throws SQLException {
        wrapper.dispose();
        sm.executeDropTable("replayWrapper");
	}

    @Test
    public void testInitializeDefaultSpeed(){

        ReplayWrapper wrapper = new ReplayWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("dbname", "replayWrapper"));
        predicates.add( new KeyValueImp("speed", "-1"));
		
        AddressBean ab = new AddressBean("replayWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("replayWrapper");

        wrapper.setActiveAddressBean(ab);

        assertTrue(wrapper.initialize());

    }

    @Test
    public void testGetOutputFormat() {

        DataField[] expected = new DataField[] {
            new DataField("TIMED", DataTypes.BIGINT),
            new DataField("DEVICE_ID", DataTypes.INTEGER),
            new DataField("DEVICE_TYPE", DataTypes.SMALLINT),
            new DataField("BEGIN", DataTypes.BIGINT),
            new DataField("END", DataTypes.BIGINT),
            new DataField("POSITION", DataTypes.INTEGER),
            new DataField("COMMENT", DataTypes.VARCHAR)
        };
        
        DataField[] actualOutput = wrapper.getOutputFormat();
        assertArrayEquals(expected, actualOutput);
    }
    
}
