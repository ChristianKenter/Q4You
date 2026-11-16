package app.config;

import app.routes.Routes;
import app.security.controllers.SecurityController;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;

public class ApplicationConfig {
    private static final SecurityController securityController = SecurityController.getInstance();
    private static final Routes routes = new Routes();

    public static void configuration(JavalinConfig config) {
        config.routes.beforeMatched(securityController::authenticate);
        config.routes.beforeMatched(securityController::authorize);
        config.routes.apiBuilder(routes.getRoutes());
    }

    public static Javalin startServer(int port) {
        Javalin app = Javalin.create(ApplicationConfig::configuration);
        app.start(port);
        return app;
    }

}


