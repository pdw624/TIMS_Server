package kr.tracom.platform.api;

import kr.tracom.platform.api.jwt.filter.JWTTokenNeededFilter;
import kr.tracom.platform.common.config.AppConfig;
import kr.tracom.platform.common.config.Constants;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;


public class TOpenApi {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /* singleton */
    private static class SingletonHolder {
        public static final TOpenApi INSTANCE = new TOpenApi();
    }

    public static TOpenApi getInstance() {
        return SingletonHolder.INSTANCE;
    }
	/* singleton */

    public static void main(String[] args) {
        AppConfig.read(Constants.PROPERTIES_PATH);
        TOpenApi.getInstance().startup();
    }

    private static final String BASE_URI = "http://localhost:8080/";
    private HttpServer httpServer = null;

    public void startup() {
        String rootPath = AppConfig.getApplicationPath() + Constants.WEB_PATH;

        final ResourceConfig rc = new ResourceConfig();
        rc.packages("kr.tracom.platform.api");
        rc.register(JWTTokenNeededFilter.class);

        httpServer = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
        ServerConfiguration config = httpServer.getServerConfiguration();
        config.addHttpHandler(new StaticHttpHandler(rootPath + "/static"), "/static");

        /*
        httpServer.getListener("grizzly").registerAddOn(new WebSocketAddOn());
        WebSocketApplication chatApplication = new ChatApplication();
        // register the application
        WebSocketEngine.getEngine().register("/w-chat", "/chat", chatApplication);
        */

        logger.info(String.format("Jersey app started with WADL available at %sapplication.wadl\nHit enter to stop it...", BASE_URI));

        try {
            httpServer.start();
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            if(httpServer != null) {
                httpServer.shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
