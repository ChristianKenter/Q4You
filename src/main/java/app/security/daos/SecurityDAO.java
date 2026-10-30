package app.security.daos;

import app.config.HibernateConfig;
import app.entities.Client;
import app.security.exceptions.ValidationException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Locale;

public class SecurityDAO implements ISecurityDAO {

    private final EntityManagerFactory emf =
            HibernateConfig.getEntityManagerFactory();

    @Override
    public Client getVerifiedUser(String username, String password) {
        if (username == null || password == null) {
            throw new ValidationException("Invalid username or password");
        }

        String normalized = username.trim().toLowerCase(Locale.ROOT);
        try (EntityManager em = emf.createEntityManager()) {
            Client client = em.createQuery("SELECT c FROM Client c WHERE c.clientName = :username", Client.class)
                    .setParameter("username", normalized)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (client == null || !BCrypt.checkpw(password, client.getClientPassword())) {
                throw new ValidationException("Invalid username or password");
            }

            return client;
        }
    }

    @Override
    public Client createUser(String username, String password) {
        if (username == null || !username.trim().matches("[A-Za-z0-9_.-]{3,50}")) {
            throw new ValidationException("Username must be 3-50 characters and contain only " + "letters, numbers, dots, underscores or hyphens");
        }

        if (password == null || password.length() < 8) {
            throw new ValidationException("Password must contain at least 8 characters");
        }

        String normalized = username.trim().toLowerCase(Locale.ROOT);
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();

            try {
                tx.begin();

                Long count = em.createQuery("SELECT COUNT(c) FROM Client c WHERE c.clientName = :username", Long.class)
                        .setParameter("username", normalized)
                        .getSingleResult();

                if (count > 0) {
                    throw new ValidationException("Username is already registered");
                }

                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
                Client client = new Client(normalized, hashedPassword);
                client.setClientRole("standard");

                em.persist(client);
                tx.commit();

                return client;

            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }
}