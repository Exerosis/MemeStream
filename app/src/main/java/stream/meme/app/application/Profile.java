package stream.meme.app.application;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.common.base.Optional;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

import stream.meme.app.application.login.LoginType;

public class Profile {
    private final String name;
    private final String email;
    private final UUID uuid;
    private final LoginType[] logins;
    private final Optional<Bitmap> profilePicture;

    public Profile(Context context, String name, String email, UUID uuid, LoginType... logins) {
        this.name = name;
        this.email = email;
        this.uuid = uuid;
        this.logins = logins;
        Bitmap picture = null;
        try {
            picture = Picasso.with(context).load("https://cdna.artstation.com/p/assets/images/images/004/421/584/large/gimins-draws-117-2b.jpg?1483624834").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        profilePicture = Optional.fromNullable(picture);
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Optional<Bitmap> getProfilePicture() {
        return profilePicture;
    }

    public UUID getUuid() {
        return uuid;
    }

    public LoginType[] getLogins() {
        return logins;
    }
}