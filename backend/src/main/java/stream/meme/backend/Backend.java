package stream.meme.backend;

import static spark.Spark.*;

public class Backend {

    public static void main(String[] args) {
        path("/api", () -> {
            before("/*", (request, response) -> {
                if (!API.isAuthenticated(request.headers("auth")))
                    halt(401, "You are not welcome here");
                else
                    System.out.println("Received api call");
            });
            path("/posts", () -> {
                path("/:id", () -> {
                    get("/comments", API.comments);
                    post("/comment", API.comment);
                    post("/rate", API.rate);
                });
                get("/", API.posts);
            });
            post("/auth", API.auth);
        });
    }
}
