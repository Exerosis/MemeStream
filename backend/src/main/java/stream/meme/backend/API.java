package stream.meme.backend;

import com.google.gson.Gson;

import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RMultimap;
import org.redisson.api.RedissonClient;

import java.util.Set;
import java.util.UUID;

import spark.Request;
import spark.Route;

import static java.util.stream.Collectors.toSet;
import static stream.meme.backend.ProviderType.valueOf;

public class API {
    private final RMap<Long, Post> posts;
    private final RMultimap<Long, Comment> comments;
    private final RMap<String, User> users;
    private final Gson gson = new Gson();

    public API() {
        RedissonClient redis = Redisson.create();
        posts = redis.getMap("posts");
        users = redis.getMap("users");
        comments = redis.getListMultimap("comments");
    }

    public final Route comments() {
        return (request, response) -> {
            Long id = gson.fromJson(request.queryParams("id"), Long.class);
            response.body(gson.toJson(comments.get(id)));
            return response;
        };
    }

    public final Route comment() {
        return (request, response) -> {
            Long id = gson.fromJson(request.queryParams("id"), Long.class);
            comments.put(id, gson.fromJson(request.body(), Comment.class));
            response.body(gson.toJson(comments.get(id)));
            return response;
        };
    }

    public final Route rate() {
        return (request, response) -> {
            Long id = gson.fromJson(request.queryParams("id"), Long.class);
            Post post = posts.get(id);
            if (gson.fromJson(request.body(), Boolean.class)) {
                post.upVote();
            } else {
                post.downVote();
            }
            return response;
        };
    }

    public final Route posts() {
        return (request, response) -> {
            Long last = gson.fromJson(request.queryParams("last"), Long.class);

            Set<Long> ids = postsForUser(token(request));
            if (last != null)
                ids = ids.parallelStream()
                        .filter(id -> id > last)
                        .collect(toSet());

            response.body(gson.toJson(posts.getAll(ids).values()));
            return response;
        };
    }

    private Set<Long> postsForUser(String token) {
        //FIXME implement.
        return posts.keySet();
    }


    //--Auth--
    private String token(Request request) {
        return request.headers("auth");
    }

    public final Route auth() {
        return (request, response) -> {
            //FIXME meh.
            if (isAuthenticated(token(request))) {
                response.body(token(request));
                return response;
            }
            ProviderType provider = valueOf(request.queryParams("provider"));
            String providedToken = request.body();
            //TODO auth here.
            String token = UUID.randomUUID().toString();
            User user = new User();
            //TODO user details here.

            users.put(token, user);
            response.body(token);
            return response;
        };
    }

    public boolean isAuthenticated(String token) {
        return users.containsKey(token);
    }
}
