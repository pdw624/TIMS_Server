package kr.tracom.platform.launcher.http;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import kr.tracom.platform.common.config.AppConfig;
import kr.tracom.platform.common.config.Constants;
import kr.tracom.platform.common.module.ModuleInterface;
import kr.tracom.platform.launcher.http.handler.StaticHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.ServerConfiguration;

public class THttpServer implements ModuleInterface {
    private HttpServer server;

    /* singleton */
    private static class SingletonHolder {
        public static final THttpServer INSTANCE = new THttpServer();
    }

    public static THttpServer getInstance() {
        return SingletonHolder.INSTANCE;
    }
	/* singleton */

    private THttpServer() {

    }

    @Override
    public void init(Object args) {

    }

    @Override
    public void startup() {
        String rootPath = AppConfig.getApplicationPath() + Constants.WEB_PATH;//AppConfig.getApplicationPath() + Constants.WEB_PATH;
        int webPort = Integer.parseInt(AppConfig.get("platform.web.port"));
        String webUrl = String.format("http://0.0.0.0:%d", webPort);
        boolean isView = Boolean.parseBoolean(AppConfig.get("platform.web.view.enable"));

        ResourceConfig rc = new PackagesResourceConfig("kr.tracom.platform.launcher.http");
        rc.getProperties().put(
                "com.sun.jersey.spi.container.ContainerRequestFilters",
                "com.sun.jersey.api.container.filter.LoggingFilter;kr.tracom.platform.launcher.http.security.AuthFilter"
        );

        try {
            server = GrizzlyServerFactory.createHttpServer(webUrl, rc);

            ServerConfiguration config = server.getServerConfiguration();
            
            if(isView) {
	            config.addHttpHandler(new StaticHandler(rootPath + "/"), "/asset");
	            config.addHttpHandler(new StaticHandler(rootPath + "/"), "/platform");
            }
            
            server.start();
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void shutdown() {
        if(server != null) {
            server.shutdown();
        }
    }
}
