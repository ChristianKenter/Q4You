package app.routes;

import app.controllers.impl.ClientController;
import app.security.Roles;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.post;

public class ClientRoutes {
    private final ClientController clientController = new ClientController();

    protected EndpointGroup getRoutes() {
        return () -> {
            post("/login", clientController::login, Roles.ANYONE);
            post("/register", clientController::register, Roles.ANYONE);
            post("/logout", clientController::logout, Roles.ANYONE);
        };
    }
}