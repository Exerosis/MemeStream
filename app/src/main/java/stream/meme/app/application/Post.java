package stream.meme.app.application;

import android.graphics.Bitmap;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class Post {
    private final UUID id;
    private final String title;
    private final String subtitle;
    private final String image;
    private final Bitmap thumbnail;
    private final List<Comment> previewComments;
}
