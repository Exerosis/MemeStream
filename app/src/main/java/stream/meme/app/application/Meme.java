package stream.meme.app.application;

import android.graphics.Bitmap;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class Meme {
    private final UUID uuid;
    private final String title;
    private final String subtitle;
    private final String image;
    private final Bitmap thumbnail;
    private final List<Comment> comments
}
