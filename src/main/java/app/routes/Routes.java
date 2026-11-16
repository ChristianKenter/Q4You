
package app.routes;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private static final ClientRoutes clientRoutes = new ClientRoutes();
    private static final VideoRoutes videoRoutes = new VideoRoutes();
    private static final SecurityRoutes securityRoutes = new SecurityRoutes();

    public EndpointGroup getRoutes() {
        return () -> {
            path("/client", clientRoutes.getRoutes());
            path("/auth", securityRoutes.getRoutes());
            path("/video", videoRoutes.getRoutes());
        };
    }
}
