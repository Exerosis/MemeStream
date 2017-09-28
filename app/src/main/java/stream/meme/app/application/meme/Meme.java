package stream.meme.app.application.meme;

import java.util.UUID;

public class Meme {
    private UUID uuid;
    private String title;
    private String subtitle;
    private String image;

    public Meme(UUID uuid, String title, String subtitle, String image) {
        this.uuid = uuid;
        this.title = title;
        this.subtitle = subtitle;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public UUID getUuid() {
        return uuid;
    }
}
