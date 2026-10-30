package app.dao;

import app.config.HibernateConfig;
import app.entities.Client;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import org.mindrot.jbcrypt.BCrypt;

public class ClientDAO {

    private final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    public Client findClient(String clientName, String clientPassword) {
        EntityManager em = emf.createEntityManager();

        try {
            Client client = em.createQuery("SELECT c FROM Client c WHERE c.clientName = :name", Client.class)
                    .setParameter("name", clientName)
                    .getSingleResultOrNull();

            if (client != null && BCrypt.checkpw(clientPassword, client.getClientPassword())) {
                return client;
            }
            return null;

        } finally {
            em.close();
        }
    }

    public Client createClient(String clientName, String clientPassword) {
        clientName = clientName.trim().toLowerCase(java.util.Locale.ROOT);

        if (clientName.length() > 15) {
            throw new IllegalArgumentException("Username must be 15 characters or fewer");
        }

        if (clientPassword.length() > 20) {
            throw new IllegalArgumentException("Password must be 20 characters or fewer");
        }
        EntityManager em = emf.createEntityManager();

        try {
            long count = em.createQuery("SELECT COUNT(c) FROM Client c WHERE c.clientName = :name", Long.class)
                    .setParameter("name", clientName)
                    .getSingleResult();

            if (count > 0) {
                throw new IllegalArgumentException(
                        "That username is already taken"
                );
            }

            String hashedPassword = BCrypt.hashpw(
                    clientPassword,
                    BCrypt.gensalt()
            );

            Client client = new Client(clientName, hashedPassword);

            em.getTransaction().begin();
            em.persist(client);
            em.getTransaction().commit();

            return client;

        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

    public void saveVideo() {
    }
}