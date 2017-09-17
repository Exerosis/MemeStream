package stream.meme.app.application;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import stream.meme.app.login.LoginType;
import stream.meme.app.stream.Meme;

public class MemeStream extends Application {
    public static final String KEY_TOKEN = "token";
    private LoginManager loginManager;
    private SharedPreferences sharedPreferences;

    public Observable<List<Meme>> loadMemes(int page) {
        return Observable.just(Arrays.asList(
                new Meme(UUID.randomUUID(), "test0", "page " + page, "https://i.vimeocdn.com/portrait/58832_300x300"),
                new Meme(UUID.randomUUID(), "test1", "page " + page, "https://i.vimeocdn.com/portrait/58832_300x300"),
                new Meme(UUID.randomUUID(), "test2", "page " + page, "https://i.vimeocdn.com/portrait/58832_300x300"),
                new Meme(UUID.randomUUID(), "test3", "page " + page, "https://i.vimeocdn.com/portrait/58832_300x300"),
                new Meme(UUID.randomUUID(), "test4", "page " + page, "https://i.vimeocdn.com/portrait/58832_300x300"),
                new Meme(UUID.randomUUID(), "test5", "page " + page, "https://i.vimeocdn.com/portrait/58832_300x300")))
                .delay(2, TimeUnit.SECONDS);
    }

    public Observable<Boolean> isAuthenticated() {
        return Observable.just(getSharedPreferences().contains(KEY_TOKEN));
    }

    public Observable<Boolean> login(LoginType type, Activity activity) {
        return getLoginManager().startLogin(type, activity).flatMap(result -> loginInternal(type, result)).onErrorReturn(error -> false);
    }

    private Observable<Boolean> loginInternal(LoginType type, String token) {
        return Observable.create(subscriber -> {
            if (getSharedPreferences().contains(KEY_TOKEN))
                subscriber.onNext(true);
            else if (token == null)
                subscriber.onNext(false);
            else
                subscriber.onNext(getSharedPreferences().edit().putString(KEY_TOKEN, token).commit());
            subscriber.onComplete();
        });
    }

    public LoginManager getLoginManager() {
        if (loginManager == null)
            loginManager = new LoginManager(this);
        return loginManager;
    }

    private SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null)
            sharedPreferences = getSharedPreferences(getPackageName() + "authentication", MODE_PRIVATE);
        return sharedPreferences;
    }
}
