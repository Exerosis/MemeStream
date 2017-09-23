package stream.meme.app.application;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import stream.meme.app.application.login.FacebookLogin;
import stream.meme.app.application.login.GoogleLogin;
import stream.meme.app.application.login.Login;
import stream.meme.app.application.login.TwitterLogin;
import stream.meme.app.login.LoginType;
import stream.meme.app.stream.Meme;

import static stream.meme.app.login.LoginType.FACEBOOK;
import static stream.meme.app.login.LoginType.GOOGLE;
import static stream.meme.app.login.LoginType.TWITTER;

public class MemeStream extends Application {
    public static final String KEY_TOKEN = "token";
    private Map<LoginType, Login> logins;
    private SharedPreferences sharedPreferences;

    public Observable<List<Meme>> loadMemes(int page) {
        return Observable.just(Arrays.asList(
                new Meme(UUID.randomUUID(), "test0", "page " + page, "https://i.vimeocdn.com/portrait/58832_300x300"),
                new Meme(UUID.randomUUID(), "test1", "page " + page, "https://i.vimeocdn.com/portrait/58832_300x300"),
                new Meme(UUID.randomUUID(), "test2", "page " + page, "https://i.vimeocdn.com/portrait/58832_300x300"),
                new Meme(UUID.randomUUID(), "test3", "page " + page, "https://i.vimeocdn.com/portrait/58832_300x300"),
                new Meme(UUID.randomUUID(), "test4", "page " + page, "https://i.vimeocdn.com/portrait/58832_300x300"),
                new Meme(UUID.randomUUID(), "test5", "page " + page, "https://i.vimeocdn.com/portrait/58832_300x300")))
                .delay(5, TimeUnit.SECONDS);
    }

    public boolean isAuthenticated() {
        return getSharedPreferences().contains(KEY_TOKEN);
    }

    public Observable<Boolean> login(LoginType type, Activity activity) {
        if (getLogins().containsKey(type))
            return getLogins().get(type).login(activity)
                    .toObservable()
                    .map(token -> token != null && getSharedPreferences().edit().putString(KEY_TOKEN, token).commit())
                    .onErrorReturn(error -> false);
        else
            return Observable.just(false);
    }

    public Map<LoginType, Login> getLogins() {
        if (logins == null) {
            logins = new HashMap<>();
            logins.put(FACEBOOK, new FacebookLogin());
            logins.put(TWITTER, new TwitterLogin(this));
            logins.put(GOOGLE, new GoogleLogin(this));
        }
        return logins;
    }

    private SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null)
            sharedPreferences = getSharedPreferences(getPackageName() + "authentication", MODE_PRIVATE);
        return sharedPreferences;
    }
}
