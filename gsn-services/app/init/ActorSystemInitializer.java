package init;

import akka.actor.ActorSystem;
import akka.actor.Props;
import play.inject.ApplicationLifecycle;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;

import ch.epfl.gsn.data.DataStore;
import ch.epfl.gsn.data.SensorStore;
import ch.epfl.gsn.config.GsnConf;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ActorSystemInitializer {

    private final Logger logger = LoggerFactory.getLogger(ActorSystemInitializer.class);

    @Inject
    public ActorSystemInitializer(ApplicationLifecycle lifecycle,ActorSystem actorSystem) {

        logger.info("Application has started");

        Config config = ConfigFactory.load();
		GsnConf gsnConf = GsnConf.load(config.getString("gsn.config"));
		DataStore dataStore = new DataStore(gsnConf);

        actorSystem.actorOf(Props.create(SensorStore.class, dataStore), "gsnSensorStore");

        logger.debug("Config content: {}", config.toString());

        // Shut down the actor system when the application exits
        lifecycle.addStopHook(() -> {

            logger.info("Application terminated");

            actorSystem.terminate();
            return CompletableFuture.completedFuture(null);
        });
    }
}
