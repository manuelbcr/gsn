package ch.epfl.gsn.wrappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.epfl.gsn.Main;
import ch.epfl.gsn.beans.AddressBean;
import ch.epfl.gsn.beans.VSensorConfig;
import ch.epfl.gsn.storage.StorageManager;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.KeyValueImp;
import ch.epfl.gsn.wrappers.backlog.statistics.DeploymentStatistics;

public class TestBacklogStatsWrapper {

    private BackLogStatsWrapper wrapper;
    private static StorageManager sm;

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

    @AfterClass
	public static void teardownAfterClass() throws Exception {
        sm.shutdown();
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
   
    
}
