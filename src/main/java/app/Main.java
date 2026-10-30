package app;

import app.routes.Routes;
import io.javalin.Javalin;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(Routes::getRoutes).start(7070);
    }
}
