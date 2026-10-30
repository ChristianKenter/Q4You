package app.routes;

import app.controllers.impl.ClientController;
import app.security.Roles;
import io.javalin.config.JavalinConfig;

public class ClientRoutes {

    private final ClientController clientController = new ClientController();

    public void getRoutes(JavalinConfig config) {

        config.routes.get("/client/login", ctx -> ctx.render("login.html"), Roles.ANYONE);
        config.routes.post("/client/login", clientController::login, Roles.ANYONE);
        config.routes.get("/client/register", ctx -> ctx.render("register.html"), Roles.ANYONE);
        config.routes.post("/client/register", clientController::register, Roles.ANYONE);
        config.routes.post("/client/logout", clientController::logout, Roles.ANYONE);

    }
}