package stream.meme.app.application;

import android.graphics.Bitmap;

import com.google.common.base.Optional;

import java.util.UUID;

import lombok.Data;

import static java.util.Objects.hash;

@Data
public class Post {
    public static final Boolean UP_VOTE = true;
    public static final Boolean NO_VOTE = null;
    public static final Boolean DOWN_VOTE = false;
    private final UUID id;
    private final String title;
    private final String subtitle;
    private final String image;
    private final Bitmap thumbnail;
    private final Boolean rating;

    public Optional<Boolean> getRating() {
        return Optional.fromNullable(rating);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Post && id.equals(((Post) obj).id) && rating == ((Post) obj).rating;
    }

    @Override
    public int hashCode() {
        return hash(id, rating);
    }
}
