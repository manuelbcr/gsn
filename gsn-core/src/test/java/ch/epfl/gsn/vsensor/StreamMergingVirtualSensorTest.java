package ch.epfl.gsn.vsensor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

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
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.KeyValueImp;
import ch.epfl.gsn.vsensor.StreamMergingVirtualSensor.StreamElementContainer;
import ch.epfl.gsn.vsensor.StreamMergingVirtualSensor.StreamElementInputStreamNameTuple;

public class StreamMergingVirtualSensorTest {
     private static StorageManager sm;
    private VSensorConfig testVsensorConfig;
    private ArrayList < KeyValue > params;
    private StreamElementContainer container;
    private StreamElementInputStreamNameTuple tuple;

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
            new DataField("field1", DataTypes.INTEGER),
            new DataField("field2", DataTypes.INTEGER),
            new DataField("field3", DataTypes.INTEGER),
            new DataField("field4", DataTypes.BIGINT),
            new DataField("foo",DataTypes.VARCHAR),
            new DataField("foo1",DataTypes.INTEGER),
            new DataField("foo2",DataTypes.DOUBLE),
            new DataField("foo3",DataTypes.SMALLINT),
            new DataField("foo4",DataTypes.BIGINT),
            new DataField("foo5",DataTypes.TINYINT),
            new DataField("foo6",DataTypes.BINARY),

        };

        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("teststreammerging");
        File someFile = File.createTempFile("teststreammerging", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.StreamMergingVirtualSensor");
        testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);
        params = new ArrayList < KeyValue >( );
        params.add(new KeyValueImp("maximum_buffered_stream_age_in_days", "10"));
        params.add(new KeyValueImp("streams_needed_to_merge", "5"));
        params.add(new KeyValueImp("timeline", "field4"));
        params.add(new KeyValueImp("merge_bucket_size_in_minutes", "120"));
        params.add(new KeyValueImp("merge_bucket_edge_type", "dynamic"));
        params.add(new KeyValueImp("matching_field1", "field1"));
        params.add(new KeyValueImp("matching_field2", "field2"));
        params.add(new KeyValueImp("default_merge_operator", "new"));
        params.add(new KeyValueImp("filter_duplicates", "false"));
        params.add(new KeyValueImp("filter_data_points_from_same_source", "false"));
        params.add(new KeyValueImp("duplicates_ignore_field", "field3"));
        params.add(new KeyValueImp("foo", "add"));
        params.add(new KeyValueImp("foo1", "add"));
        params.add(new KeyValueImp("foo2", "add"));
        params.add(new KeyValueImp("foo3", "add"));
        params.add(new KeyValueImp("foo4", "add"));
        params.add(new KeyValueImp("foo5", "add"));
        params.add(new KeyValueImp("foo6", "add"));


        testVsensorConfig.setMainClassInitialParams( params );

        
        sm.executeCreateTable("teststreammerging", fields, true);
        String[] fieldnames = {"field1","field2","field3","field4"};
        StreamElement streamElement1 = new StreamElement(
            fieldnames,
            new Byte[]{DataTypes.INTEGER,DataTypes.INTEGER,DataTypes.INTEGER,DataTypes.BIGINT},
            new Serializable[]{1,2,3,1657752584L},
            System.currentTimeMillis()+10
        );
        StreamElement streamElement2 = new StreamElement(
            fieldnames,
            new Byte[]{DataTypes.INTEGER,DataTypes.INTEGER,DataTypes.INTEGER,DataTypes.BIGINT},
            new Serializable[]{4,5,6,1657852584L},
            System.currentTimeMillis()+20
        );
        StreamElement streamElement3 = new StreamElement(
            fieldnames,
            new Byte[]{DataTypes.INTEGER,DataTypes.INTEGER,DataTypes.INTEGER,DataTypes.BIGINT},
            new Serializable[]{7,8,9,1657952584L},
            System.currentTimeMillis()+30
        );
        StreamElement streamElement4 = new StreamElement(
            fieldnames,
            new Byte[]{DataTypes.INTEGER,DataTypes.INTEGER,DataTypes.INTEGER,DataTypes.BIGINT},
            new Serializable[]{10,11,12,1658752584L},
            System.currentTimeMillis()+40
        );
        StreamElement streamElement5 = new StreamElement(
            fieldnames,
            new Byte[]{DataTypes.INTEGER,DataTypes.INTEGER,DataTypes.INTEGER,DataTypes.BIGINT},
            new Serializable[]{8,5,8,1658852584L},
            System.currentTimeMillis()+50
        );

        sm.executeInsert("teststreammerging", fields, streamElement1);
        sm.executeInsert("teststreammerging", fields, streamElement2);
        sm.executeInsert("teststreammerging", fields, streamElement3);
        sm.executeInsert("teststreammerging", fields, streamElement4);
        sm.executeInsert("teststreammerging", fields, streamElement5);
    }

    @After
	public void teardown() throws SQLException {
		sm.executeDropTable("teststreammerging");
	}

    @Test
    public void testInitialize() {
        StreamMergingVirtualSensor vs = new StreamMergingVirtualSensor();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
        String[] fieldnames = {"field1","field2","field3","field4","foo","foo1","foo2","foo3","foo6"};
        byte[] exampleBinaryData = { 0x48, 0x65, 0x6C, 0x6C, 0x6F };
         StreamElement streamElement = new StreamElement(
            fieldnames,
            new Byte[]{DataTypes.INTEGER,DataTypes.INTEGER,DataTypes.INTEGER,DataTypes.BIGINT,DataTypes.VARCHAR,DataTypes.INTEGER,DataTypes.DOUBLE,DataTypes.SMALLINT,DataTypes.BINARY},
            new Serializable[]{1,2,3,1657752585L,"foo",1,1.3213,(short)2,exampleBinaryData}
        );
         StreamElement streamElement1 = new StreamElement(
            fieldnames,
            new Byte[]{DataTypes.INTEGER,DataTypes.INTEGER,DataTypes.INTEGER,DataTypes.BIGINT,DataTypes.VARCHAR,DataTypes.INTEGER,DataTypes.DOUBLE,DataTypes.SMALLINT,DataTypes.BINARY},
            new Serializable[]{23,321,41,1657752585L,"foo2",2,45.3213,(short)2,exampleBinaryData}
        );

        vs.dataAvailable("input", streamElement);
        vs.dataAvailable("input2", streamElement1);
        vs.dispose();

    }   

    @Test
    public void testInitialize1() {
        StreamMergingVirtualSensor vs = new StreamMergingVirtualSensor();
        String[] fieldnames = {"field1","field2","field3","field4","foo","foo1","foo2","foo3"};
         StreamElement streamElement = new StreamElement(
            fieldnames,
            new Byte[]{DataTypes.INTEGER,DataTypes.INTEGER,DataTypes.INTEGER,DataTypes.BIGINT,DataTypes.VARCHAR,DataTypes.INTEGER,DataTypes.DOUBLE,DataTypes.SMALLINT},
            new Serializable[]{1,2,3,1657752585L,"foo",1,1.3213,(short)2}
        );
        params.set(4, new KeyValueImp("merge_bucket_edge_type", "static"));
        testVsensorConfig.setMainClassInitialParams(params);
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
        vs.dataAvailable("input", streamElement);
        vs.dispose();
    } 

    @Test
    public void testInitialize2(){
        StreamMergingVirtualSensor vs = new StreamMergingVirtualSensor();
        params = new ArrayList < KeyValue >( );
        params.add(new KeyValueImp("maximum_buffered_stream_age_in_days", "10"));
        params.add(new KeyValueImp("streams_needed_to_merge", "5"));
        params.add(new KeyValueImp("timeline", "field4"));
        params.add(new KeyValueImp("merge_bucket_size_in_minutes", "120"));
        params.add(new KeyValueImp("merge_bucket_edge_type", "dynamic"));
        params.add(new KeyValueImp("matching_field1", "field1"));
        params.add(new KeyValueImp("matching_field2", "field2"));
        params.add(new KeyValueImp("default_merge_operator", "new"));
        params.add(new KeyValueImp("filter_duplicates", "false"));
        params.add(new KeyValueImp("filter_data_points_from_same_source", "false"));
        params.add(new KeyValueImp("duplicates_ignore_field", "field3"));
        params.add(new KeyValueImp("foo", "add"));
        params.add(new KeyValueImp("foo1", "avg"));
        params.add(new KeyValueImp("foo2", "avg"));
        params.add(new KeyValueImp("foo3", "avg"));
        params.add(new KeyValueImp("foo4", "avg"));
        params.add(new KeyValueImp("foo5", "avg"));
        params.add(new KeyValueImp("foo6", "add"));


        testVsensorConfig.setMainClassInitialParams( params );
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
        String[] fieldnames = {"field1","field2","field3","field4","foo","foo1","foo2","foo3"};
         StreamElement streamElement = new StreamElement(
            fieldnames,
            new Byte[]{DataTypes.INTEGER,DataTypes.INTEGER,DataTypes.INTEGER,DataTypes.BIGINT,DataTypes.VARCHAR,DataTypes.INTEGER,DataTypes.DOUBLE,DataTypes.SMALLINT},
            new Serializable[]{1,2,3,1657752585L,"foo",1,1.3213,(short)2}
        );
        vs.dataAvailable("input", streamElement);
        vs.dispose();
    }
    @Test
    public void testInitialize3(){
        StreamMergingVirtualSensor vs = new StreamMergingVirtualSensor();
        params.set(12, new KeyValueImp("foo1", "min"));
        params.set(13, new KeyValueImp("foo2", "min"));
        params.set(14, new KeyValueImp("foo3", "min"));
        params.set(15, new KeyValueImp("foo4", "min"));
        params.set(16, new KeyValueImp("foo5", "min"));
        testVsensorConfig.setMainClassInitialParams(params);
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
        String[] fieldnames = {"field1","field2","field3","field4","foo","foo1","foo2","foo3"};
         StreamElement streamElement = new StreamElement(
            fieldnames,
            new Byte[]{DataTypes.INTEGER,DataTypes.INTEGER,DataTypes.INTEGER,DataTypes.BIGINT,DataTypes.VARCHAR,DataTypes.INTEGER,DataTypes.DOUBLE,DataTypes.SMALLINT},
            new Serializable[]{1,2,3,1657752585L,"foo",1,1.3213,(short)2}
        );
        vs.dataAvailable("input", streamElement);
        vs.dispose();
    }

    @Test
    public void testInitialize4(){
        StreamMergingVirtualSensor vs = new StreamMergingVirtualSensor();
        params.set(12, new KeyValueImp("foo1", "max"));
        params.set(13, new KeyValueImp("foo2", "max"));
        params.set(14, new KeyValueImp("foo3", "max"));
        params.set(15, new KeyValueImp("foo4", "max"));
        params.set(16, new KeyValueImp("foo5", "max"));
        testVsensorConfig.setMainClassInitialParams(params);
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());
        String[] fieldnames = {"field1","field2","field3","field4","foo","foo1","foo2","foo3"};
         StreamElement streamElement = new StreamElement(
            fieldnames,
            new Byte[]{DataTypes.INTEGER,DataTypes.INTEGER,DataTypes.INTEGER,DataTypes.BIGINT,DataTypes.VARCHAR,DataTypes.INTEGER,DataTypes.DOUBLE,DataTypes.SMALLINT},
            new Serializable[]{1,2,3,1657752585L,"foo",1,1.3213,(short)2}
        );
        vs.dataAvailable("input", streamElement);
        vs.dispose();
    }
     
    @Test
    public void testInitializeFalse() {
        StreamMergingVirtualSensor vs = new StreamMergingVirtualSensor();
        params.set(0, new KeyValueImp("maximum_buffered_stream_age_in_days", "invalid"));
        testVsensorConfig.setMainClassInitialParams(params);
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
        vs.dispose();
    }

    @Test
    public void testInitializeFalse1() {
        StreamMergingVirtualSensor vs = new StreamMergingVirtualSensor();
        params.set(3, new KeyValueImp("merge_bucket_size_in_minutes", "invalid"));
        testVsensorConfig.setMainClassInitialParams(params);
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
        vs.dispose();
    }

    @Test
    public void testInitializeFalse2() {
        StreamMergingVirtualSensor vs = new StreamMergingVirtualSensor();
        params.set(1, new KeyValueImp("streams_needed_to_merge", "invalid"));
        testVsensorConfig.setMainClassInitialParams(params);
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
        vs.dispose();
    }
    @Test
    public void testInitializeFalse3(){
        StreamMergingVirtualSensor vs = new StreamMergingVirtualSensor();
        params.set(4, new KeyValueImp("merge_bucket_edge_type", "invalid"));
        testVsensorConfig.setMainClassInitialParams(params);
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
        vs.dispose();
    }

    @Test
    public void testInitializeFalse4(){
        StreamMergingVirtualSensor vs = new StreamMergingVirtualSensor();
        params.set(2, new KeyValueImp("timeline", "invalidfield"));
        testVsensorConfig.setMainClassInitialParams(params);
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
        vs.dispose();
    }
    @Test
    public void testInitializeFalse5(){
        StreamMergingVirtualSensor vs = new StreamMergingVirtualSensor();
        params.set(5, new KeyValueImp("matching_field1", "invalidfield1"));
        testVsensorConfig.setMainClassInitialParams(params);
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
        vs.dispose();
    }
    @Test
    public void testInitializeFalse6(){
        StreamMergingVirtualSensor vs = new StreamMergingVirtualSensor();
        params.set(6, new KeyValueImp("matching_field2", "invalidfield2"));
        testVsensorConfig.setMainClassInitialParams(params);
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
        vs.dispose();
    }
    @Test
    public void testInitializeFalse7(){
        StreamMergingVirtualSensor vs = new StreamMergingVirtualSensor();
        params.set(10, new KeyValueImp("duplicates_ignore_field", "invalidfield2"));
        testVsensorConfig.setMainClassInitialParams(params);
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
        vs.dispose();
    }
    @Test
    public void testInitializeFalse8(){
        StreamMergingVirtualSensor vs = new StreamMergingVirtualSensor();
        params.set(7, new KeyValueImp("default_merge_operator", "invalidoperator"));
        testVsensorConfig.setMainClassInitialParams(params);
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
        vs.dispose();
    }
    @Test
    public void testInitializeFalse9(){
        StreamMergingVirtualSensor vs = new StreamMergingVirtualSensor();
        params.set(7, new KeyValueImp("notaparamname", "invalidoperator"));
        testVsensorConfig.setMainClassInitialParams(params);
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
        vs.dispose();
    }

    @Test
    public void testInitializeFalse10(){
        StreamMergingVirtualSensor vs = new StreamMergingVirtualSensor();
        params.set(0, new KeyValueImp("foo", "add"));
        testVsensorConfig.setMainClassInitialParams(params);
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
        vs.dispose();
    }

    @Test
    public void testInitializeFalse11(){
        StreamMergingVirtualSensor vs = new StreamMergingVirtualSensor();
        params.set(0, new KeyValueImp("foo", "add"));
        testVsensorConfig.setMainClassInitialParams(params);
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
        vs.dispose();
    }

    @Test
    public void testInitializeFalse12(){
        StreamMergingVirtualSensor vs = new StreamMergingVirtualSensor();
        params.set(4, new KeyValueImp("foo", "add"));
        testVsensorConfig.setMainClassInitialParams(params);
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
        vs.dispose();
    }

    @Test
    public void testInitialize13(){
        DataField[] fields = new DataField[]{
            new DataField("field1", DataTypes.INTEGER),
            new DataField("field2", DataTypes.INTEGER),
            new DataField("field3", DataTypes.INTEGER),
            new DataField("field4", DataTypes.INTEGER),
            new DataField("foo",DataTypes.VARCHAR)
        };
        StreamMergingVirtualSensor vs = new StreamMergingVirtualSensor();
        testVsensorConfig.setOutputStructure(fields);
        testVsensorConfig.setMainClassInitialParams(params);
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
        vs.dispose();
    }

    @Test
    public void testInitialize14(){
        StreamMergingVirtualSensor vs = new StreamMergingVirtualSensor();
        params.set(11, new KeyValueImp("maximum_buffered_stream_age_in_days", "10"));
        params.set(7, new KeyValueImp("maximum_buffered_stream_age_in_days", "10"));
        testVsensorConfig.setMainClassInitialParams(params);
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
        vs.dispose();
    }

    @Test
    public void testInitialize15(){
        StreamMergingVirtualSensor vs = new StreamMergingVirtualSensor();
        params = new ArrayList < KeyValue >( );
        params.add(new KeyValueImp("maximum_buffered_stream_age_in_days", "10"));
        params.add(new KeyValueImp("timeline", "field4"));
        params.add(new KeyValueImp("merge_bucket_size_in_minutes", "120"));
        params.add(new KeyValueImp("merge_bucket_edge_type", "dynamic"));
        params.add(new KeyValueImp("matching_field1", "field1"));
        params.add(new KeyValueImp("matching_field2", "field2"));
        params.add(new KeyValueImp("default_merge_operator", "new"));
        params.add(new KeyValueImp("filter_duplicates", "false"));
        params.add(new KeyValueImp("filter_data_points_from_same_source", "false"));
        params.add(new KeyValueImp("duplicates_ignore_field", "field3"));
        params.add(new KeyValueImp("foo", "add"));
        params.add(new KeyValueImp("foo1", "add"));
        params.add(new KeyValueImp("foo2", "add"));
        params.add(new KeyValueImp("foo3", "add"));
        params.add(new KeyValueImp("foo4", "add"));
        params.add(new KeyValueImp("foo5", "add"));
        params.add(new KeyValueImp("foo6", "add"));
        params.set(11, new KeyValueImp("foo", "max"));
        testVsensorConfig.setMainClassInitialParams(params);
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
        vs.dispose();
    }

    @Test
    public void testInitialize16(){
        StreamMergingVirtualSensor vs = new StreamMergingVirtualSensor();
        params = new ArrayList < KeyValue >( );
        params.add(new KeyValueImp("maximum_buffered_stream_age_in_days", "10"));
        params.add(new KeyValueImp("streams_needed_to_merge", "5"));
        params.add(new KeyValueImp("merge_bucket_size_in_minutes", "120"));
        params.add(new KeyValueImp("merge_bucket_edge_type", "dynamic"));
        params.add(new KeyValueImp("matching_field1", "field1"));
        params.add(new KeyValueImp("matching_field2", "field2"));
        params.add(new KeyValueImp("default_merge_operator", "new"));
        params.add(new KeyValueImp("filter_duplicates", "false"));
        params.add(new KeyValueImp("filter_data_points_from_same_source", "false"));
        params.add(new KeyValueImp("duplicates_ignore_field", "field3"));
        params.add(new KeyValueImp("foo", "add"));
        params.add(new KeyValueImp("foo1", "avg"));
        params.add(new KeyValueImp("foo2", "avg"));
        params.add(new KeyValueImp("foo3", "avg"));
        params.add(new KeyValueImp("foo4", "avg"));
        params.add(new KeyValueImp("foo5", "avg"));
        params.add(new KeyValueImp("foo6", "add"));
        testVsensorConfig.setMainClassInitialParams(params);
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertFalse(vs.initialize());
        vs.dispose();
    }

   @Test
    public void testStreamElementContainer() throws Exception {
        params = new ArrayList < KeyValue >( );
        params.add(new KeyValueImp("maximum_buffered_stream_age_in_days", "10"));
        params.add(new KeyValueImp("streams_needed_to_merge", "5"));
        params.add(new KeyValueImp("timeline", "field4"));
        params.add(new KeyValueImp("merge_bucket_size_in_minutes", "120"));
        params.add(new KeyValueImp("merge_bucket_edge_type", "dynamic"));
        params.add(new KeyValueImp("matching_field1", "field1"));
        params.add(new KeyValueImp("matching_field2", "field2"));
        params.add(new KeyValueImp("default_merge_operator", "new"));
        params.add(new KeyValueImp("filter_duplicates", "true"));
        params.add(new KeyValueImp("filter_data_points_from_same_source", "true"));
        params.add(new KeyValueImp("duplicates_ignore_field", "field3"));
        params.add(new KeyValueImp("foo", "add"));
        params.add(new KeyValueImp("foo1", "add"));
        params.add(new KeyValueImp("foo2", "add"));
        params.add(new KeyValueImp("foo3", "add"));
        params.add(new KeyValueImp("foo4", "add"));
        params.add(new KeyValueImp("foo5", "add"));
        params.add(new KeyValueImp("foo6", "add"));
        testVsensorConfig.setMainClassInitialParams( params );

        String[] fieldnames = { "field1", "field2", "field3", "field4" };
        StreamElement streamElement1 = new StreamElement(
                fieldnames,
                new Byte[] { DataTypes.INTEGER, DataTypes.INTEGER, DataTypes.INTEGER, DataTypes.BIGINT },
                new Serializable[] { 1, 2, 3, 1657752584L },
                System.currentTimeMillis() + 10);
        StreamElement streamElement2 = new StreamElement(
                fieldnames,
                new Byte[] { DataTypes.INTEGER, DataTypes.INTEGER, DataTypes.INTEGER, DataTypes.BIGINT },
                new Serializable[] { 4, 5, 6, 1657852584L },
                System.currentTimeMillis() + 20);
        StreamMergingVirtualSensor vs = new StreamMergingVirtualSensor();
        vs.setVirtualSensorConfiguration(testVsensorConfig);
        assertTrue(vs.initialize());

        container = vs.new StreamElementContainer("testStream", streamElement1);
        container.putStreamElement("testStream2", streamElement2);
        assertEquals(2, container.getNumberOfStreams());
        long expected = 1657852584L;
        assertEquals(expected, container.getNewestTimestamp().longValue());
        StreamElement merged = container.getMergedStreamElement();
        assertEquals(4, merged.getData()[0]);
        assertEquals(5, merged.getData()[1]); 
        assertEquals(6, merged.getData()[2]);
        assertEquals(1657852584L, merged.getData()[3]);

        
    }



}