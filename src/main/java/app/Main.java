package app;

import app.config.ApplicationConfig;
import app.routes.Routes;
import io.javalin.Javalin;

public class Main {
    public static void main(String[] args) {
        ApplicationConfig.startServer(7070);
    }
}
