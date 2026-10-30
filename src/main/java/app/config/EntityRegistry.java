package app.config;

import app.entities.Client;
import app.entities.Video;

import org.hibernate.cfg.Configuration;

final class EntityRegistry {

    private EntityRegistry() {}

    static void registerEntities(Configuration configuration) {
        configuration.addAnnotatedClass(Client.class);
        configuration.addAnnotatedClass(Video.class);
        // TODO: Add more entities here...
    }
}
