package stream.meme.backend;

import com.google.common.base.Optional;

import lombok.Getter;

public class Post extends PostData {
    public static final Boolean UP_VOTE = true;
    public static final Boolean NO_VOTE = null;
    public static final Boolean DOWN_VOTE = false;
    private final Boolean rating;
    @Getter
    private final int upvotes, downvotes;

    public Post(Boolean rating, int upvotes, int downvotes, PostData postData) {
        super(postData.getId(), postData.getTitle(), postData.getSubtitle());
        this.rating = rating;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
    }

    public Optional<Boolean> getRating() {
        return Optional.fromNullable(rating);
    }

}
