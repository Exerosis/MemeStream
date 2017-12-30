package stream.meme.backend;

import java.util.HashMap;
import java.util.Map;

public class User {
    private final Map<Long, Boolean> ratings = new HashMap<>();


    public Boolean rating(Long post) {
        return ratings.get(post);
    }

    public Boolean rate(Long post, Boolean rating) {
        return ratings.put(post, rating);
    }

    public Map<Long, Boolean> getRatings() {
        return ratings;
    }
}