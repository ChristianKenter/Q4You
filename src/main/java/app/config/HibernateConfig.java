package app.config;

import app.utils.Utils;
import jakarta.persistence.EntityManagerFactory;

import java.util.Properties;

public final class HibernateConfig {

    private static volatile EntityManagerFactory emf;

    private HibernateConfig() {}

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            synchronized (HibernateConfig.class) {
                if (emf == null) {
                    emf = HibernateEmfBuilder.build(buildProps());
                }
            }
        }
        return emf;
    }

    private static Properties buildProps() {
        Properties props = HibernateBaseProperties.createBase();

        // Teaching-friendly default
        props.put("hibernate.hbm2ddl.auto", "update");

        if (System.getenv("DEPLOYED") != null) {
            setDeployedProperties(props);
        } else {
            setDevProperties(props);
        }

        return props;
    }

    private static void setDeployedProperties(Properties props) {
        String dbName = System.getenv("JDBC_DB");
        String connectionString = System.getenv("JDBC_CONNECTION_STRING");
        String username = System.getenv("JDBC_USER");
        String password = System.getenv("JDBC_PASSWORD");

        if (dbName == null || connectionString == null ||
                username == null || password == null) {
            throw new IllegalStateException(
                    "Missing required JDBC environment variables"
            );
        }

        String jdbcUrl = connectionString.replace("%s", dbName);

        props.setProperty("hibernate.connection.url", jdbcUrl);
        props.setProperty("hibernate.connection.username", username);
        props.setProperty("hibernate.connection.password", password);
    }

    private static void setDevProperties(Properties props) {
        String dbName = Utils.getPropertyValue("DB_NAME", "config.properties");
        String username = Utils.getPropertyValue("DB_USERNAME", "config.properties");
        String password = Utils.getPropertyValue("DB_PASSWORD", "config.properties");

        props.put("hibernate.connection.url",
                "jdbc:postgresql://localhost:5432/" + dbName);
        props.put("hibernate.connection.username", username);
        props.put("hibernate.connection.password", password);
    }
}