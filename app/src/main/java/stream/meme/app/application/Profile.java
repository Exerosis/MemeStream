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
            picture = Picasso.with(context).load("https://img00.deviantart.net/23aa/i/2017/060/5/3/2b_by_johnsonting-db0takw.jpg").get();
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

    public Optional<Bitmap> getImage() {
        return profilePicture;
    }

    public UUID getUuid() {
        return uuid;
    }

    public LoginType[] getLogins() {
        return logins;
    }
}