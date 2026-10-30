package app.security;

import io.javalin.security.RouteRole;

public enum Roles implements RouteRole {
    ANYONE,
    STANDARD,
    PREMIUM
}