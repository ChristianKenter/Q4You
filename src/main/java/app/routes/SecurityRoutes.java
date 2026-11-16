package app.routes;

import app.security.Roles;
import app.security.controllers.SecurityController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.post;

public class SecurityRoutes {
    private final SecurityController securityController = SecurityController.getInstance();

    protected EndpointGroup getRoutes() {
        return () -> {
            post("/login", securityController::login, Roles.ANYONE);
            post("/register", securityController::register, Roles.ANYONE);
        };
    }
}