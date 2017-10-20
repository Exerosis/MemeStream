package stream.meme.app.application;

import android.graphics.Bitmap;

import lombok.Data;

/**
 * Created by Exerosis on 10/19/2017.
 */
@Data
public class User {
    private final String name;
    private final Bitmap image;
}