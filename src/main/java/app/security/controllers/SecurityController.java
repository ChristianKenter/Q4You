package app.security.controllers;

import app.dto.ClientDTO;
import app.entities.Client;
import app.security.Roles;
import app.security.daos.ISecurityDAO;
import app.security.daos.SecurityDAO;
import app.security.exceptions.ApiException;
import app.security.exceptions.ValidationException;
import app.utils.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dk.bugelhartmann.ITokenSecurity;
import dk.bugelhartmann.TokenSecurity;
import dk.bugelhartmann.UserDTO;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.UnauthorizedResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class SecurityController implements ISecurityController {

    private static final Logger logger = LoggerFactory.getLogger(SecurityController.class);
    private static final SecurityController INSTANCE = new SecurityController();

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ITokenSecurity tokenSecurity = new TokenSecurity();
    private final ISecurityDAO securityDAO = new SecurityDAO();

    public static SecurityController getInstance() {
        return INSTANCE;
    }

    @Override
    public void register(Context ctx) {
        ObjectNode response = objectMapper.createObjectNode();

        try {
            ClientDTO body = ctx.bodyAsClass(ClientDTO.class);
            Client client = securityDAO.createUser(
                    body.getClientName(),
                    body.getClientPassword()
            );

            String token = createToken(client);
            ctx.status(HttpStatus.CREATED).json(response
                    .put("token", token)
                    .put("clientName", client.getClientName())
                    .put("role", client.getClientRole())
            );

        } catch (ValidationException | IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).json(response.put("msg", e.getMessage()));

        } catch (Exception e) {
            logger.error("Registration failed", e);
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response.put("msg", "Registration failed"));
        }
    }

    @Override
    public void login(Context ctx) {
        ObjectNode response = objectMapper.createObjectNode();

        try {
            ClientDTO body = ctx.bodyAsClass(ClientDTO.class);
            Client client = securityDAO.getVerifiedUser(
                    body.getClientName(),
                    body.getClientPassword()
            );

            if (client == null) {
                ctx.status(HttpStatus.UNAUTHORIZED).json(response.put("msg", "Invalid username or password"));
                return;
            }

            String token = createToken(client);
            ctx.status(HttpStatus.OK).json(response
                    .put("token", token)
                    .put("clientName", client.getClientName())
                    .put("role", client.getClientRole())
            );

        } catch (ValidationException | IllegalArgumentException e) {
            ctx.status(HttpStatus.UNAUTHORIZED).json(response.put("msg", "Invalid username or password"));

        } catch (Exception e) {
            logger.error("Login failed", e);
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response.put("msg", "Login failed"));
        }
    }

    private UserDTO toTokenClient(Client client) {
        String role = client.getClientRole().toUpperCase(Locale.ROOT);
        return new UserDTO(client.getClientName(), Set.of(role));
    }

    @Override
    public void authenticate(Context ctx) {

        if (ctx.method().toString().equalsIgnoreCase("OPTIONS")) {
            return;
        }

        Set<String> allowed = getRouteRoles(ctx);

        if (allowed.isEmpty() || isOpenEndpoint(allowed)) {
            return;
        }

        String token = null;

        String header = ctx.header("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7).trim();
        }

        if (token == null || token.isBlank()) {
            token = ctx.cookie("auth");
        }

        if (token == null || token.isBlank()) {
            throw new UnauthorizedResponse("Authentication required");
        }

        UserDTO clientToken = verifyToken(token);

        ctx.attribute("client", clientToken);
    }

    @Override
    public void authorize(Context ctx) {

        if (ctx.method().toString().equalsIgnoreCase("OPTIONS")) {
            return;
        }

        Set<String> allowed = getRouteRoles(ctx);
        if (allowed.isEmpty() || isOpenEndpoint(allowed)) {
            return;
        }

        UserDTO clientToken = ctx.attribute("client");
        if (clientToken == null) {
            throw new UnauthorizedResponse("No authenticated client found");
        }

        Set<String> clientRoles = clientToken.getRoles() == null
                ? Set.of()
                : clientToken.getRoles().stream()
                .filter(role -> role != null)
                .map(role -> role.toUpperCase(Locale.ROOT))
                .collect(Collectors.toSet());
        boolean permitted;

        if (allowed.contains(Roles.PREMIUM.name())) {
            permitted = clientRoles.contains(Roles.PREMIUM.name());

        } else if (allowed.contains(Roles.STANDARD.name())) {
            permitted = clientRoles.contains(Roles.STANDARD.name()) || clientRoles.contains(Roles.PREMIUM.name());

        } else {
            permitted = false;
        }

        if (!permitted) {
            throw new io.javalin.http.ForbiddenResponse("Your client role does not permit access to this route");
        }
    }

    private Set<String> getRouteRoles(Context ctx) {
        return ctx.routeRoles()
                .stream()
                .map(Object::toString)
                .map(role -> role.toUpperCase(Locale.ROOT))
                .collect(Collectors.toSet());
    }

    private boolean isOpenEndpoint(Set<String> roles) {
        return roles.contains(Roles.ANYONE.name());
    }

    public String createToken(Client client) {
        return createToken(toTokenClient(client));
    }

    private String createToken(UserDTO clientToken) {
        try {
            String issuer = property("ISSUER");
            String expireTime = property("TOKEN_EXPIRE_TIME");
            String secret = property("SECRET_KEY");

            return tokenSecurity.createToken(clientToken, issuer, expireTime, secret
            );

        } catch (Exception e) {
            logger.error("Could not create token", e);
            throw new ApiException(500, "Could not create token");
        }
    }

    private UserDTO verifyToken(String token) {
        try {
            String secret = property("SECRET_KEY");

            if (tokenSecurity.tokenIsValid(token, secret) && tokenSecurity.tokenNotExpired(token)) {
                return tokenSecurity.getUserWithRolesFromToken(token);
            }

        } catch (Exception e) {
            logger.warn("Token verification failed: {}", e.getMessage());
        }

        throw new UnauthorizedResponse("Invalid or expired token");
    }

    private String property(String key) {
        if (System.getenv("DEPLOYED") != null) {
            String value = System.getenv(key);

            if (value == null || value.isBlank()) {
                throw new ApiException(
                        500,
                        "Missing environment variable: " + key
                );
            }

            return value;
        }

        return Utils.getPropertyValue(key, "config.properties");
    }
}