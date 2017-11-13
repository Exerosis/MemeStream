package stream.meme.app.application;

import android.graphics.Bitmap;

import lombok.Data;

import static java.util.Objects.hash;

/**
 * Created by Exerosis on 10/19/2017.
 */
@Data
public class User {
    private final String name;
    private final Bitmap image;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof User && ((User) obj).name.equals(name);
    }

    @Override
    public int hashCode() {
        return hash(name);
    }
}