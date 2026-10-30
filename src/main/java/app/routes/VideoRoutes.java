package app.routes;

import app.controllers.impl.VideoController;
import app.security.Roles;
import io.javalin.config.JavalinConfig;

public class VideoRoutes {

    private final VideoController videoController = new VideoController();

    public void getRoutes(JavalinConfig config) {
        config.routes.post("/video/generate-video", videoController::create, Roles.STANDARD);
        config.routes.get("/video/read", videoController::read, Roles.STANDARD);
        config.routes.put("/video/update", videoController::update, Roles.STANDARD);
        config.routes.delete("/video/delete", videoController::delete, Roles.STANDARD);
    }
}