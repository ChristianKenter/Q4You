package app.routes;

import app.controllers.impl.VideoController;
import app.security.Roles;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.put;

public class VideoRoutes {

    private final VideoController videoController = new VideoController();

    protected EndpointGroup getRoutes() {
        return () -> {
            post("/generate-video", videoController::create, Roles.STANDARD);
            put("/update", videoController::update, Roles.STANDARD);
            post("/delete", videoController::delete, Roles.STANDARD);
        };
    }
}