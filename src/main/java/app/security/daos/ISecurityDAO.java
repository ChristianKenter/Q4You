package app.security.daos;

import app.entities.Client;
import app.security.exceptions.ValidationException;

public interface ISecurityDAO {
    Client getVerifiedUser(String username, String password) throws ValidationException;
    Client createUser(String username, String password);
}