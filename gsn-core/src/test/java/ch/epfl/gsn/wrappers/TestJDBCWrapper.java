package ch.epfl.gsn.wrappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.KeyValueImp;

public class TestJDBCWrapper  {
   
    private JDBCWrapper wrapper;
    private static StorageManager sm;

    @BeforeClass
	public static void setUpBeforeClass() throws Exception {
        sm = StorageManagerFactory.getInstance( "org.h2.Driver","sa","" ,"jdbc:h2:mem:coreTest", Main.DEFAULT_MAX_DB_CONNECTIONS);
	}

    @Before
	public void setup() throws SQLException, IOException {

        wrapper = new JDBCWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("table-name", "jdbcWrapper"));
        predicates.add( new KeyValueImp("jdbc-url", "jdbc:h2:mem:coreTest"));
        predicates.add( new KeyValueImp("username", "sa"));
        predicates.add( new KeyValueImp("password", ""));
        predicates.add( new KeyValueImp("driver", "org.h2.Driver"));
        predicates.add( new KeyValueImp("start-time", "continue"));
        
        AddressBean ab = new AddressBean("jdbcWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("jdbcWrapper");

        wrapper.setActiveAddressBean(ab);

        DataField[] positionStructure = new DataField[] {
            new DataField("timed", DataTypes.BIGINT),
            new DataField("DEVICE_ID", DataTypes.INTEGER),
            new DataField("DEVICE_TYPE", DataTypes.SMALLINT),
            new DataField("BEGIN", DataTypes.BIGINT),
            new DataField("END", DataTypes.BIGINT),
            new DataField("POSITION", DataTypes.INTEGER),
            new DataField("COMMENT", DataTypes.VARCHAR)
        };


        sm.executeCreateTable("jdbcWrapper", positionStructure, true);
    
        StreamElement streamElement = new StreamElement(
                    new String[]{"timed", "DEVICE_ID", "DEVICE_TYPE", "BEGIN", "END", "POSITION", "COMMENT"},
                    new Byte[]{DataTypes.BIGINT, DataTypes.INTEGER, DataTypes.SMALLINT, DataTypes.BIGINT, DataTypes.BIGINT, DataTypes.INTEGER, DataTypes.VARCHAR},
                    new Serializable[]{1257946505000L, 1, (short) 1, 123456L, 234567L, 1, "postion 1"},
                    System.currentTimeMillis());
            
        sm.executeInsert("jdbcWrapper", positionStructure, streamElement);

        assertTrue(wrapper.initialize());
        assertEquals("JDBCWrapper", wrapper.getWrapperName());

    }

    @After
	public void teardown() throws SQLException {
        wrapper.dispose();
        sm.executeDropTable("jdbcWrapper");	
	}

    @Test
    public void testInitializeIsoTime(){

        JDBCWrapper wrapper = new JDBCWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("table-name", "jdbcWrapper"));
        predicates.add( new KeyValueImp("jdbc-url", "jdbc:h2:mem:coreTest"));
        predicates.add( new KeyValueImp("username", "sa"));
        predicates.add( new KeyValueImp("password", ""));
        predicates.add( new KeyValueImp("driver", "org.h2.Driver"));
        predicates.add( new KeyValueImp("start-time", "2009-11-02T00:00:00.000+00:00"));
        
        AddressBean ab = new AddressBean("jdbc-wrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("jdbc-wrapper");

        wrapper.setActiveAddressBean(ab);

        assertTrue(wrapper.initialize());

    }

    @Test
    public void testInitializeEpochTime(){

        JDBCWrapper wrapper = new JDBCWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("table-name", "jdbcWrapper"));
        predicates.add( new KeyValueImp("jdbc-url", "jdbc:h2:mem:coreTest"));
        predicates.add( new KeyValueImp("username", "sa"));
        predicates.add( new KeyValueImp("password", ""));
        predicates.add( new KeyValueImp("driver", "org.h2.Driver"));
        predicates.add( new KeyValueImp("start-time", "1257946505000"));

        AddressBean ab = new AddressBean("jdbc-wrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("jdbc-wrapper");

        wrapper.setActiveAddressBean(ab);

        assertTrue(wrapper.initialize());

    }

    @Test
    public void testInitializeWithoutTable(){

        JDBCWrapper wrapper = new JDBCWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        predicates.add( new KeyValueImp("jdbc-url", "jdbc:h2:mem:coreTest"));
        predicates.add( new KeyValueImp("username", "sa"));
        predicates.add( new KeyValueImp("password", ""));
        predicates.add( new KeyValueImp("driver", "org.h2.Driver"));
        predicates.add( new KeyValueImp("start-time", "continue"));

        AddressBean ab = new AddressBean("jdbc-wrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("jdbc-wrapper");

        wrapper.setActiveAddressBean(ab);

        assertFalse(wrapper.initialize());

    }

    @Test
    public void testInitializeWithoutStartTime(){

        JDBCWrapper wrapper = new JDBCWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        predicates.add( new KeyValueImp("table-name", "jdbcWrapper"));
        predicates.add( new KeyValueImp("jdbc-url", "jdbc:h2:mem:coreTest"));
        predicates.add( new KeyValueImp("username", "sa"));
        predicates.add( new KeyValueImp("password", ""));
        predicates.add( new KeyValueImp("driver", "org.h2.Driver"));

        AddressBean ab = new AddressBean("jdbc-wrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("jdbc-wrapper");

        wrapper.setActiveAddressBean(ab);

        assertFalse(wrapper.initialize());

    }

    @Test
    public void testInitializeWithoutWrongIso(){

        JDBCWrapper wrapper = new JDBCWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        predicates.add( new KeyValueImp("table-name", "jdbcWrapper"));
        predicates.add( new KeyValueImp("jdbc-url", "jdbc:h2:mem:coreTest"));
        predicates.add( new KeyValueImp("username", "sa"));
        predicates.add( new KeyValueImp("password", ""));
        predicates.add( new KeyValueImp("driver", "org.h2.Driver"));
        predicates.add( new KeyValueImp("start-time", "2009-11-02T00:00:00.000"));

        AddressBean ab = new AddressBean("jdbc-wrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("jdbc-wrapper");

        wrapper.setActiveAddressBean(ab);

        assertFalse(wrapper.initialize());

    }

    @Test
    public void testInitializeWithoutWrongEpoch(){

        JDBCWrapper wrapper = new JDBCWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        predicates.add( new KeyValueImp("table-name", "jdbcWrapper"));
        predicates.add( new KeyValueImp("jdbc-url", "jdbc:h2:mem:coreTest"));
        predicates.add( new KeyValueImp("username", "sa"));
        predicates.add( new KeyValueImp("password", ""));
        predicates.add( new KeyValueImp("driver", "org.h2.Driver"));
        predicates.add( new KeyValueImp("start-time", "-1"));

        AddressBean ab = new AddressBean("jdbc-wrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("jdbc-wrapper");

        wrapper.setActiveAddressBean(ab);

        assertFalse(wrapper.initialize());

    }

    @Test
    public void testRun() throws SQLException {
        
        DataField[] positionStructure = new DataField[] {
            new DataField("timed", DataTypes.BIGINT),
            new DataField("DEVICE_ID", DataTypes.INTEGER),
            new DataField("DEVICE_TYPE", DataTypes.SMALLINT),
            new DataField("BEGIN", DataTypes.BIGINT),
            new DataField("END", DataTypes.BIGINT),
            new DataField("POSITION", DataTypes.INTEGER),
            new DataField("COMMENT", DataTypes.VARCHAR)
        };

        StreamElement streamElement = new StreamElement(
                    new String[]{"timed", "DEVICE_ID", "DEVICE_TYPE", "BEGIN", "END", "POSITION", "COMMENT"},
                    new Byte[]{DataTypes.BIGINT, DataTypes.INTEGER, DataTypes.SMALLINT, DataTypes.BIGINT, DataTypes.BIGINT, DataTypes.INTEGER, DataTypes.VARCHAR},
                    new Serializable[]{1357946505000L, 2, (short) 2, 123456L, 234567L, 2, "postion 1"},
                    System.currentTimeMillis());
            
        sm.executeInsert("jdbcWrapper", positionStructure, streamElement);

        wrapper.start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        wrapper.stop();
    
    }




}