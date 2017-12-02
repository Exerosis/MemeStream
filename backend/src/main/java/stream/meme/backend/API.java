package stream.meme.backend;

import com.google.gson.Gson;

import org.redisson.Redisson;
import org.redisson.api.RListMultimap;
import org.redisson.api.RedissonClient;

import spark.Request;
import spark.Route;

public class API {
    private final RListMultimap<String, String> posts;
    private final Gson gson = new Gson();

    public API() {
        RedissonClient redis = Redisson.create();
        posts = redis.getListMultimap("posts");
    }

    public final Route comments() {
        return (request, response) -> {
            return response;
        };
    }

    public final Route comment() {
        return (request, response) -> {
            return response;
        };
    }

    public final Route rate() {
        return (request, response) -> {
            return response;
        };
    }

    public final Route posts() {
        return (request, response) -> {
            response.body(gson.toJson(posts.get(token(request))));
            return response;
        };
    }

    public final Route auth() {
        return (request, response) -> {
            return response;
        };
    }

    private String token(Request request) {
        return request.headers("auth");
    }

    public boolean isAuthenticated(String token) {
        return true;
    }
}
