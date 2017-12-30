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

import static java.lang.Long.parseLong;
import static java.util.stream.Collectors.toSet;
import static stream.meme.backend.ProviderType.valueOf;

public class API {
    private final RMap<Long, PostData> posts;
    private final RMap<Long, Integer> upvotes;
    private final RMap<Long, Integer> downvotes;
    private final RMultimap<Long, Comment> comments;
    private final RMap<String, User> users;
    private final Gson gson = new Gson();

    public API() {
        RedissonClient redis = Redisson.create();
        posts = redis.getMap("posts");
        users = redis.getMap("users");
        upvotes = redis.getMap("upvotes");
        downvotes = redis.getMap("downvotes");
        comments = redis.getListMultimap("comments");
    }

    public final Route comments() {
        return (request, response) -> comments.get(id(request));
    }

    public final Route comment() {
        return (request, response) -> {
            Long id = id(request);
            comments.put(id, gson.fromJson(request.body(), Comment.class));
            return request.body();
        };
    }

    public final Route rate() {
        return (request, response) -> {
            Boolean rating = gson.fromJson(request.body(), Boolean.class);
            Long id = parseLong(request.params("id"));
            Boolean oldRating = user(request).rate(id, rating);
            if (rating != oldRating) {
                if (oldRating != null)
                    (oldRating ? upvotes : downvotes).addAndGetAsync(id, -1);
                if (rating != null)
                    (rating ? upvotes : downvotes).addAndGetAsync(id, 1);
            }
            return post(request);
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

            return posts.getAll(ids).values();
        };
    }

    private Set<Long> postsForUser(String token) {
        //FIXME implement.
        return posts.keySet();
    }

    private String token(Request request) {
        return request.headers("auth");
    }

    private User user(Request request) {
        return users.get(token(request));
    }

    private Long id(Request request) {
        return parseLong(request.params("id"));
    }

    private Post post(Request request) {
        Long id = id(request);
        return new Post(user(request).rating(id), upvotes.get(id), downvotes.get(id), posts.get(id));
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
            return token;
        };
    }

    public boolean isAuthenticated(String token) {
        return users.containsKey(token);
    }
}
