package stream.meme.backend;

import com.google.common.base.Optional;

import java.util.UUID;

import lombok.Data;
import lombok.Getter;

import static java.util.Objects.hash;

@Data
public class Post {
    public static final Boolean UP_VOTE = true;
    public static final Boolean NO_VOTE = null;
    public static final Boolean DOWN_VOTE = false;
    private final UUID id;
    private final String title, subtitle;
    private final Boolean rating;
    @Getter
    private int upvotes, downvotes = 0;

    protected int upVote() {
        return ++upvotes;
    }

    protected int downVote() {
        return ++downvotes;
    }

    public Optional<Boolean> getRating() {
        return Optional.fromNullable(rating);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Post && id.equals(((Post) obj).id);
    }

    @Override
    public int hashCode() {
        return hash(id);
    }
}
