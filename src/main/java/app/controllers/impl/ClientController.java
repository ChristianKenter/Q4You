package app.controllers.impl;

import app.controllers.IController;
import app.dao.ClientDAO;
import app.entities.Client;
import app.security.controllers.SecurityController;
import io.javalin.http.Context;

public class ClientController implements IController {

    private final ClientDAO clientDAO = new ClientDAO();
    private final SecurityController securityController = new SecurityController();

    public void login(Context ctx) {
        String clientName = ctx.formParam("clientName");
        String clientPassword = ctx.formParam("clientPassword");

        if (clientName == null || clientName.isBlank()) {
            ctx.status(400);
            return;
        }

        if (clientPassword == null || clientPassword.isBlank()) {
            ctx.status(400);
            return;
        }

        clientName = clientName.toLowerCase();
        Client client = clientDAO.findClient(clientName, clientPassword);

        if (client == null) {
            ctx.status(401);
            return;
        }
        String token = securityController.createToken(client);
        ctx.header("Set-Cookie", "auth=" + token + "; Max-Age=3600" + "; Path=/" + "; HttpOnly" + "; SameSite=Strict");
        ctx.render("clientpage.html");
    }

    public void register(Context ctx) {
        String clientName = ctx.formParam("clientName");
        String clientPassword = ctx.formParam("clientPassword");

        if (clientName == null || clientName.isBlank()){
            ctx.status(400);
            return;
        }

        if (clientPassword == null || clientPassword.isBlank()){
            ctx.status(400);
            return;
        }

        try {
            clientName = clientName.toLowerCase();
            Client client = clientDAO.createClient(clientName, clientPassword);
            String token = securityController.createToken(client);

            ctx.header("Set-Cookie", "auth=" + token + "; Max-Age=3600" + "; Path=/" + "; HttpOnly" + "; SameSite=Strict");
            ctx.render("clientpage.html");

        } catch (IllegalArgumentException e) {
            ctx.status(400);
        }
    }

    public void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/auth/login");
    }

    public void saveVideo() {
    }

    @Override
    public void create(Context ctx) {
    }

    @Override
    public void read(Context ctx) {
    }

    @Override
    public void update(Context ctx) {
    }

    @Override
    public void delete(Context ctx) {
    }
}