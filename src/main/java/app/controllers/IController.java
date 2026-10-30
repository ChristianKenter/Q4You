package app.controllers;
import io.javalin.http.Context;

public interface IController {

    void create(Context ctx) throws Exception;
    void read(Context ctx);
    void update(Context ctx);
    void delete(Context ctx);

}
