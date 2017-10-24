package stream.meme.app.application;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Maybe;
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
import stream.meme.app.application.services.LoginRequest;
import stream.meme.app.application.services.MemeService;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;
import static io.reactivex.Observable.empty;
import static io.reactivex.Observable.fromCallable;
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

    public MemeService getService() {
        if (memeService == null) {
            Gson gson = new GsonBuilder().setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES).create();

            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                    .baseUrl("http://192.168.1.3:5000/").build();
            memeService = retrofit.create(MemeService.class);
        }
        return memeService;
    }

    public Observable<List<Meme>> loadMemes(int page) {
        Observable<List<Meme>> memes;
        if ((memes = streams.get(page)) == null) {
            memes = fromCallable(() -> asList(
                    new Meme(randomUUID(),
                            "test0",
                            "page " + page,
                            "https://i.vimeocdn.com/portrait/58832_300x300",
                            Picasso.with(this).load("https://i.vimeocdn.com/portrait/58832_300x300").get(),
                            Arrays.asList(new Comment(getProfile().blockingFirst(),
                                            "1d",
                                            "Ut commodo elit nisi, non luctus metus gravida non. Donec at est vel libero pretium sollicitudin. Maecenas a ultric "),
                                    new Comment(getProfile().blockingFirst(),
                                            "1d",
                                            "Ut commodo elit nisi, non luctus metus gravida non. Donec at est vel libero pretium sollicitudin. Maecenas a ultric "),
                                    new Comment(getProfile().blockingFirst(),
                                            "1d",
                                            "Ut commodo elit nisi, non luctus metus gravida non. Donec at est vel libero pretium sollicitudin. Maecenas a ultric "))),
                    new Meme(randomUUID(),
                            "test1",
                            "page " + page,
                            "https://i.vimeocdn.com/portrait/58832_300x300",
                            Picasso.with(this).load("https://i.vimeocdn.com/portrait/58832_300x300").get(),
                            Arrays.asList(new Comment(getProfile().blockingFirst(),
                                            "1d",
                                            "Ut commodo elit nisi, non luctus metus gravida non. Donec at est vel libero pretium sollicitudin. Maecenas a ultric "),
                                    new Comment(getProfile().blockingFirst(),
                                            "1d",
                                            "Ut commodo elit nisi, non luctus metus gravida non. Donec at est vel libero pretium sollicitudin. Maecenas a ultric "),
                                    new Comment(getProfile().blockingFirst(),
                                            "1d",
                                            "Ut commodo elit nisi, non luctus metus gravida non. Donec at est vel libero pretium sollicitudin. Maecenas a ultric "))),
                    new Meme(randomUUID(),
                            "test2",
                            "page " + page,
                            "https://i.vimeocdn.com/portrait/58832_300x300",
                            Picasso.with(this).load("https://i.vimeocdn.com/portrait/58832_300x300").get(),
                            Arrays.asList(new Comment(getProfile().blockingFirst(),
                                            "1d",
                                            "Ut commodo elit nisi, non luctus metus gravida non. Donec at est vel libero pretium sollicitudin. Maecenas a ultric "),
                                    new Comment(getProfile().blockingFirst(),
                                            "1d",
                                            "Ut commodo elit nisi, non luctus metus gravida non. Donec at est vel libero pretium sollicitudin. Maecenas a ultric "),
                                    new Comment(getProfile().blockingFirst(),
                                            "1d",
                                            "Ut commodo elit nisi, non luctus metus gravida non. Donec at est vel libero pretium sollicitudin. Maecenas a ultric "))),
                    new Meme(randomUUID(),
                            "test3",
                            "page " + page,
                            "https://i.vimeocdn.com/portrait/58832_300x300",
                            Picasso.with(this).load("https://i.vimeocdn.com/portrait/58832_300x300").get(),
                            Arrays.asList(new Comment(getProfile().blockingFirst(),
                                            "1d",
                                            "Ut commodo elit nisi, non luctus metus gravida non. Donec at est vel libero pretium sollicitudin. Maecenas a ultric "),
                                    new Comment(getProfile().blockingFirst(),
                                            "1d",
                                            "Ut commodo elit nisi, non luctus metus gravida non. Donec at est vel libero pretium sollicitudin. Maecenas a ultric "),
                                    new Comment(getProfile().blockingFirst(),
                                            "1d",
                                            "Ut commodo elit nisi, non luctus metus gravida non. Donec at est vel libero pretium sollicitudin. Maecenas a ultric "))),
                    new Meme(randomUUID(),
                            "test4",
                            "page " + page,
                            "https://i.vimeocdn.com/portrait/58832_300x300",
                            Picasso.with(this).load("https://i.vimeocdn.com/portrait/58832_300x300").get(),
                            Arrays.asList(new Comment(getProfile().blockingFirst(),
                                            "1d",
                                            "Ut commodo elit nisi, non luctus metus gravida non. Donec at est vel libero pretium sollicitudin. Maecenas a ultric "),
                                    new Comment(getProfile().blockingFirst(),
                                            "1d",
                                            "Ut commodo elit nisi, non luctus metus gravida non. Donec at est vel libero pretium sollicitudin. Maecenas a ultric "),
                                    new Comment(getProfile().blockingFirst(),
                                            "1d",
                                            "Ut commodo elit nisi, non luctus metus gravida non. Donec at est vel libero pretium sollicitudin. Maecenas a ultric "))),
                    new Meme(randomUUID(),
                            "test5",
                            "page " + page,
                            "https://i.vimeocdn.com/portrait/58832_300x300",
                            Picasso.with(this).load("https://i.vimeocdn.com/portrait/58832_300x300").get(),
                            Arrays.asList(new Comment(getProfile().blockingFirst(),
                                            "1d",
                                            "Ut commodo elit nisi, non luctus metus gravida non. Donec at est vel libero pretium sollicitudin. Maecenas a ultric "),
                                    new Comment(getProfile().blockingFirst(),
                                            "1d",
                                            "Ut commodo elit nisi, non luctus metus gravida non. Donec at est vel libero pretium sollicitudin. Maecenas a ultric "),
                                    new Comment(getProfile().blockingFirst(),
                                            "1d",
                                            "Ut commodo elit nisi, non luctus metus gravida non. Donec at est vel libero pretium sollicitudin. Maecenas a ultric ")))))
                    .delay(1, SECONDS).replay(1).autoConnect().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            streams.put(page, memes);
        }
        return memes;

    }

    public boolean isAuthenticated() {
//        return getSharedPreferences().contains(KEY_TOKEN);
        return true;
    }

    public Maybe<Boolean> login(LoginType type, Activity activity) {
        if (getLogins().containsKey(type))
            return getLogins().get(type)
                    .login(activity)
                    .flatMap(token -> getService().login(new LoginRequest(type, token)))
                    .map(response -> response != null &&
                            getSharedPreferences().edit().putString(KEY_TOKEN, response.getAccessToken()).commit());
        else
            return Maybe.empty();
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
                        "Exerosis",
                        Picasso.with(this).load("http://gameplaying.info/wp-content/uploads/2017/05/nier-automata-2b-type-art-by-GoddessMechanic.jpg").get(),
                        "exerosis@gmail.com",
                        randomUUID(),
                        LoginType.FACEBOOK,
                        LoginType.TWITTER);
            }
            return profile;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
