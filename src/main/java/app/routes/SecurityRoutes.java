package app.routes;

import app.security.Roles;
import app.security.controllers.SecurityController;
import io.javalin.config.JavalinConfig;

public class SecurityRoutes {

    private final SecurityController securityController = SecurityController.getInstance();

    public void getRoutes(JavalinConfig config) {
        config.routes.get("/auth/login", ctx -> ctx.render("login.html"), Roles.ANYONE);
        config.routes.post("/auth/login", securityController::login, Roles.ANYONE);
        config.routes.get("/auth/register", ctx -> ctx.render("register.html"), Roles.ANYONE);
        config.routes.post("/auth/register", securityController::register, Roles.ANYONE);
    }
}