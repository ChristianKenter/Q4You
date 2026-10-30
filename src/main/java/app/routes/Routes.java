
package app.routes;

import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.security.Roles;
import app.security.controllers.SecurityController;
import io.javalin.config.JavalinConfig;
import io.javalin.rendering.template.JavalinThymeleaf;

public class Routes {

    private static final ClientRoutes clientRoutes = new ClientRoutes();
    private static final VideoRoutes videoRoutes = new VideoRoutes();
    private static final SecurityRoutes securityRoutes = new SecurityRoutes();

    public static void getRoutes(JavalinConfig config) {
        config.staticFiles.add("/public");
        config.jetty.modifyServletContextHandler(handler -> handler.setSessionHandler(SessionConfig.sessionConfig()));
        config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));

        SecurityController securityController = SecurityController.getInstance();
        config.routes.beforeMatched(securityController::authenticate);
        config.routes.beforeMatched(securityController::authorize);
        config.routes.get("/", ctx -> ctx.render("index.html"), Roles.ANYONE);

        clientRoutes.getRoutes(config);
        securityRoutes.getRoutes(config);
        videoRoutes.getRoutes(config);
    }
}
