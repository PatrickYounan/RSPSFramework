package rs2;

import rs2.cache.Cache;
import rs2.net.ClientServer;

import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * @author Patrick
 */
public final class Main {

    public static final Logger LOGGER = Logger.getLogger(Main.class.getSimpleName());

    public static void main(String[] args) throws Throwable {
        Cache.init("./cache/");
        LOGGER.info("Initialized cache!");

        ClientServer server = new ClientServer();
        server.bind();

        Executors.newSingleThreadExecutor().execute(server);
        LOGGER.info("Initialized server!");
    }

}
