package stream.meme.backend;

import java.util.UUID;

import lombok.Data;
import lombok.Getter;

import static java.util.Objects.hash;

@Data
public class Post {
    private final UUID id;
    private final String title, subtitle;
    @Getter
    private int upvotes, downvotes = 0;

    protected int upVote() {
        return ++upvotes;
    }

    protected int downVote() {
        return ++downvotes;
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
