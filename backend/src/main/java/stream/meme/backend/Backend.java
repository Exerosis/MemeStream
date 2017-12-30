package stream.meme.backend;

import static spark.Spark.*;

public class Backend {

    public static void main(String[] args) {
        API api = new API();
        path("/api", () -> {
            before("/*", (request, response) -> {
                if (!api.isAuthenticated(request.headers("auth")))
                    throw halt(401, "You are not welcome here");
                else
                    System.out.println("Received api call");
            });
            path("/posts", () -> {
                path("/:id", () -> {
                    get("/comments", api.comments());
                    post("/comment", api.comment());
                    post("/rate", api.rate());
                });
                get("/", api.posts());
            });
            post("/auth", api.auth());
        });
    }
}
