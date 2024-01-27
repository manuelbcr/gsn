package ch.epfl.gsn.wrappers.backlog.statistics;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.epfl.gsn.Main;
import ch.epfl.gsn.beans.AddressBean;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.utils.KeyValueImp;
import ch.epfl.gsn.wrappers.BackLogStatsWrapper;

public class TestBacklogStatsWrapper {

    private BackLogStatsWrapper wrapper;

    @BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		// Setup current working directory
        String currentWorkingDir = System.getProperty("user.dir");
		if (!currentWorkingDir.endsWith("/gsn-core/")) {
			String newDirectory = currentWorkingDir + "/gsn-core/";
        	System.setProperty("user.dir", newDirectory);
		}

		Main.setDefaultGsnConf("/gsn_test.xml");
		Main.getInstance();

	}

    @Before
	public void setup() throws SQLException, IOException {

        wrapper = new BackLogStatsWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("sampling-rate", "1"));

        AddressBean ab = new AddressBean("backlogStatsWrapper",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("backlogStatsWrapper");

        wrapper.setActiveAddressBean(ab);

        assertTrue(wrapper.initialize());
        assertEquals("StatisticsWrapper", wrapper.getWrapperName());

    }

    @Test
    public void testGetOutputFormat() {

        DataField[] expected = new DataField[] {
			new DataField("generation_time", DataTypes.BIGINT),
			new DataField("device_id", DataTypes.INTEGER),

			new DataField("connected", DataTypes.TINYINT),

			new DataField("in_total_counter", DataTypes.BIGINT),
			new DataField("in_total_stuffed", DataTypes.BIGINT),
			new DataField("in_total_unstuffed", DataTypes.BIGINT),
			new DataField("out_total_counter", DataTypes.BIGINT),
			new DataField("out_total_stuffed", DataTypes.BIGINT),
			new DataField("out_total_unstuffed", DataTypes.BIGINT),

			new DataField("in_ack_counter", DataTypes.BIGINT),
			new DataField("in_ack_volume", DataTypes.BIGINT),
			new DataField("in_ping_counter", DataTypes.BIGINT),
			new DataField("in_ping_volume", DataTypes.BIGINT),
			new DataField("in_ping_ack_counter", DataTypes.BIGINT),
			new DataField("in_ping_ack_volume", DataTypes.BIGINT),

			new DataField("out_ack_counter", DataTypes.BIGINT),
			new DataField("out_ack_volume", DataTypes.BIGINT),
			new DataField("out_ping_counter", DataTypes.BIGINT),
			new DataField("out_ping_volume", DataTypes.BIGINT),
			new DataField("out_ping_ack_counter", DataTypes.BIGINT),
			new DataField("out_ping_ack_volume", DataTypes.BIGINT),

			new DataField("out_queue_limit_counter", DataTypes.BIGINT),
			new DataField("out_queue_limit_volume", DataTypes.BIGINT),
			new DataField("out_queue_ready_counter", DataTypes.BIGINT),
			new DataField("out_queue_ready_volume", DataTypes.BIGINT)
	    };
        
        wrapper.getOutputFormat();
        
        DataField[] actualOutput = wrapper.getOutputFormat();
        assertArrayEquals(expected, actualOutput);
    }


    @Test
    public void testRun() throws IOException{

        wrapper.start();

        DeploymentStatistics stats = StatisticsMain.getDeploymentStatsInstance("backlogstatswrapper", wrapper);
        stats.newStatisticsClass("127.0.0.1");
        Map<String, CoreStationStatistics> coreStationToCoreStationStatsList = stats.getCoreStationToCoreStationStatsList();
        CoreStationStatistics csstat = coreStationToCoreStationStatsList.get("127.0.0.1");
        csstat.setConnected(true);
        csstat.setDeviceId(1);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        wrapper.stop();
        wrapper.dispose();

    }
    
}
