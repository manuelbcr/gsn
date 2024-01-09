package ch.epfl.gsn.vsensor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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

public class SensorscopeVSTest {

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
            new DataField("field1", DataTypes.VARCHAR),
        };

        testVsensorConfig = new VSensorConfig();
		testVsensorConfig.setName("testsensorscopevs");
        File someFile = File.createTempFile("testsensorscopevs", ".xml");
		testVsensorConfig.setMainClass("ch.epfl.gsn.vsensor.SensorscopeVS");
        testVsensorConfig.setFileName(someFile.getAbsolutePath());
        testVsensorConfig.setOutputStructure(fields);
        ArrayList < KeyValue > params = new ArrayList < KeyValue >( );
        params.add(new KeyValueImp("ntwsenderid", "NTWSENDERID"));
        params.add(new KeyValueImp("ntwdisttobs", "NTWDISTTOBS"));
        params.add(new KeyValueImp("tsphopcount", "TSPHOPCOUNT"));
        params.add(new KeyValueImp("tsppacketsn", "TSPPACKETSN"));
        params.add(new KeyValueImp("reporterid", "REPORTERID"));
        params.add(new KeyValueImp("timestamp", "TIMESTAMP"));
        params.add(new KeyValueImp("rainmeter", "RAINMETER"));
        params.add(new KeyValueImp("windspeed", "WINDSPEED"));
        params.add(new KeyValueImp("watermark", "WATERMARK"));
        params.add(new KeyValueImp("solarradiation", "SOLARRADIATION"));
        params.add(new KeyValueImp("airtemperature", "AIRTEMPERATURE"));
        params.add(new KeyValueImp("airhumidity", "AIRHUMIDITY"));
        params.add(new KeyValueImp("skintemperature", "SKINTEMPERATURE"));
        params.add(new KeyValueImp("soilmoisture", "SOILMOISTURE"));
        params.add(new KeyValueImp("winddirection", "WINDDIRECTION"));
        params.add(new KeyValueImp("winddirection2", "WINDDIRECTION2"));
        params.add(new KeyValueImp("soilconductivity1", "SOILCONDUCTIVITY1"));
        params.add(new KeyValueImp("soilconductivity2", "SOILCONDUCTIVITY2"));
        params.add(new KeyValueImp("soilconductivity3", "SOILCONDUCTIVITY3"));
        params.add(new KeyValueImp("soilmoisture1", "SOILMOISTURE1"));
        params.add(new KeyValueImp("soilmoisture2", "SOILMOISTURE2"));
        params.add(new KeyValueImp("soilmoisture3", "SOILMOISTURE3"));
        params.add(new KeyValueImp("soiltemperature1", "SOILTEMPERATURE1"));
        params.add(new KeyValueImp("soiltemperature2", "SOILTEMPERATURE2"));
        params.add(new KeyValueImp("soiltemperature3", "SOILTEMPERATURE3"));
        params.add(new KeyValueImp("foo", "FOO"));
        params.add(new KeyValueImp("sampling", "SAMPLING"));

        testVsensorConfig.setMainClassInitialParams( params );

        
        sm.executeCreateTable("testsensorscopevs", fields, true);
    }

    @After
	public void teardown() throws SQLException {
		sm.executeDropTable("testsensorscopevs");
	}
    @Test
    public void testInitialize() {
        SensorscopeVS vs= new SensorscopeVS();
        vs.setVirtualSensorConfiguration(testVsensorConfig);

        short myshort= 1;

        String[] fieldnames = {
            "airtemperature","ntwsenderid", "ntwdisttobs", "tsphopcount", "tsppacketsn",
            "reporterid", "timestamp", "rainmeter", "windspeed", "watermark",
            "solarradiation", "airhumidity", "skintemperature",
            "soilmoisture", "winddirection", "winddirection2", "soilconductivity1",
            "soilconductivity2", "soilconductivity3", "soilmoisture1", "soilmoisture2",
            "soilmoisture3", "soiltemperature1", "soiltemperature2", "soiltemperature3",
            "foo", "sampling"
         };
         StreamElement streamElement = new StreamElement(
            fieldnames,
            new Byte[]{DataTypes.INTEGER,DataTypes.SMALLINT, DataTypes.SMALLINT, DataTypes.SMALLINT, DataTypes.SMALLINT,
                       DataTypes.SMALLINT, DataTypes.BIGINT, DataTypes.SMALLINT, DataTypes.INTEGER,
                       DataTypes.INTEGER, DataTypes.INTEGER, DataTypes.DOUBLE,
                       DataTypes.DOUBLE, DataTypes.DOUBLE, DataTypes.DOUBLE, DataTypes.DOUBLE,
                       DataTypes.SMALLINT, DataTypes.SMALLINT, DataTypes.SMALLINT, DataTypes.SMALLINT,
                       DataTypes.SMALLINT, DataTypes.SMALLINT, DataTypes.SMALLINT, DataTypes.SMALLINT,
                       DataTypes.SMALLINT, DataTypes.SMALLINT, DataTypes.INTEGER},
            new Serializable[]{30,myshort, myshort, myshort, myshort, myshort, System.currentTimeMillis(), myshort, 201, 900, 1,
                                1.0, 1.0, 1.0, 1.0, 1.0, myshort, myshort, myshort, myshort, myshort,
                                myshort, myshort,myshort,myshort, myshort, 200}
        );

        
        assertTrue(vs.initialize());
        vs.dataAvailable("input", streamElement);
        assertNotNull(vs.getWatermark(1000, 27.78));
        vs.dispose();
        assertNotNull(vs.getThreads());
        vs.initialize_wrapper();
        Map<Long, String> threads = new HashMap<>();
        threads.put(1L, "Thread1");
        threads.put(2L, "Thread2");
        vs.setThreads(threads);
        assertNotNull(vs.getThreads());
        assertNotNull(vs.getStatistics());
        vs.dispose_decorated();

    }
}
