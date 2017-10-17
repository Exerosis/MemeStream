package stream.meme.app.application;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import stream.meme.app.application.login.FacebookLogin;
import stream.meme.app.application.login.GoogleLogin;
import stream.meme.app.application.login.Login;
import stream.meme.app.application.login.LoginType;
import stream.meme.app.application.login.TwitterLogin;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;
import static io.reactivex.Observable.empty;
import static io.reactivex.Observable.fromCallable;
import static io.reactivex.Observable.just;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static java.util.concurrent.TimeUnit.SECONDS;
import static stream.meme.app.application.login.LoginType.FACEBOOK;
import static stream.meme.app.application.login.LoginType.GOOGLE;
import static stream.meme.app.application.login.LoginType.TWITTER;

public class MemeStream extends Application {
    public static final String KEY_TOKEN = "token";
    private Map<LoginType, Login> logins;
    private SharedPreferences sharedPreferences;
    private Profile profile;
    private MemeService memeService;
    private final SparseArray<Observable<List<Meme>>> streams = new SparseArray<>();

    private MemeService getService() {
        if (memeService == null) {
            Gson gson = new GsonBuilder().setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES).create();

            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                    .baseUrl("url").build();
            memeService = retrofit.create(MemeService.class);
        }
        return memeService;
    }

    public Observable<List<Meme>> loadMemes(int page) {
        Observable<List<Meme>> memes;
        if ((memes = streams.get(page)) == null) {
            memes = just(asList(
                    new Meme(randomUUID(), "test0", "page " + page, "https://i.vimeocdn.com/portrait/58832_300x300"),
                    new Meme(randomUUID(), "test1", "page " + page, "https://i.vimeocdn.com/portrait/58832_300x300"),
                    new Meme(randomUUID(), "test2", "page " + page, "https://i.vimeocdn.com/portrait/58832_300x300"),
                    new Meme(randomUUID(), "test3", "page " + page, "https://i.vimeocdn.com/portrait/58832_300x300"),
                    new Meme(randomUUID(), "test4", "page " + page, "https://i.vimeocdn.com/portrait/58832_300x300"),
                    new Meme(randomUUID(), "test5", "page " + page, "https://i.vimeocdn.com/portrait/58832_300x300")))
                    .delay(1, SECONDS).replay(1).autoConnect();
            streams.put(page, memes);
        }
        return memes;

    }

    public boolean isAuthenticated() {
        return getSharedPreferences().contains(KEY_TOKEN);
    }

    public Observable<Boolean> login(LoginType type, Activity activity) {
        if (getLogins().containsKey(type))
            return getLogins().get(type)
                    .login(activity)
                    .flatMap(token -> getService().login(new MemeService.LoginRequest(type, token)))
                    .map(response -> response != null &&
                            getSharedPreferences().edit().putString(KEY_TOKEN, response.accessToken).commit())
                    .toObservable()
                    .onErrorReturn(error -> false);
        else
            return just(false);
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

    public Observable<Profile> getProfile() {
        return !isAuthenticated() ? empty() : fromCallable(() -> {
            if (profile == null) {
                profile = new Profile(
                        this,
                        "Exerosis",
                        "exerosis@gmail.com",
                        randomUUID(),
                        LoginType.FACEBOOK,
                        LoginType.TWITTER);
            }
            return profile;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
