package stream.meme.app.application;

import android.graphics.Bitmap;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import stream.meme.app.application.login.ProviderType;

@Getter @Setter
public class Profile extends User {
    final String email;
    final UUID uuid;
    final ProviderType[] logins;

    public Profile(String name, Bitmap image, String email, UUID uuid, ProviderType... logins) {
        super(name, image);
        this.email = email;
        this.uuid = uuid;
        this.logins = logins;
    }
}