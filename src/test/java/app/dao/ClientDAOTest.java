package app.dao;

import app.config.HibernateConfig;
import app.entities.Client;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClientDAOTest {

    private final ClientDAO clientDAO = new ClientDAO();
    private String clientName;
    private final String clientPassword = "TestPassword123";

    @BeforeEach
    void setUp() {
        clientName = "test" + UUID.randomUUID().toString().substring(0, 8);
    }

    @AfterEach
    void cleanUp() {
        EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Client c WHERE c.clientName = :name").setParameter("name", clientName).executeUpdate();
            em.getTransaction().commit();

        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Test
    void shouldCreateAndFindClient() {
        Client createdClient = clientDAO.createClient(clientName, clientPassword);
        assertNotNull(createdClient);
        assertEquals(clientName, createdClient.getClientName());
        assertNotEquals(clientPassword, createdClient.getClientPassword());

        Client foundClient = clientDAO.findClient(clientName, clientPassword);
        assertNotNull(foundClient);
        assertEquals(clientName, foundClient.getClientName());
    }

    @Test
    void shouldNotFindClientWithWrongPassword() {
        clientDAO.createClient(clientName, clientPassword);
        Client foundClient = clientDAO.findClient(clientName, "WrongPassword123");
        assertNull(foundClient);
    }

    @Test
    void shouldNotAllowDuplicateUsername() {
        clientDAO.createClient(clientName, clientPassword);
        assertThrows(IllegalArgumentException.class, () -> clientDAO.createClient(clientName, clientPassword));
    }

    @Test
    void shouldRejectUsernameLongerThan15Characters() {
        String longUsername = "thisusernameistoolong";
        assertThrows(IllegalArgumentException.class, () -> clientDAO.createClient(longUsername, clientPassword));
    }

    @Test
    void shouldRejectPasswordLongerThan20Characters() {
        String longPassword = "ThisPasswordIsDefinitelyTooLong";
        assertThrows(IllegalArgumentException.class, () -> clientDAO.createClient(clientName, longPassword));
    }
}
