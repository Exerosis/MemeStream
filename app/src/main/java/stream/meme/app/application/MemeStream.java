package stream.meme.app.application;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import stream.meme.app.application.login.FacebookProvider;
import stream.meme.app.application.login.GoogleProvider;
import stream.meme.app.application.login.Provider;
import stream.meme.app.application.login.ProviderType;
import stream.meme.app.application.login.TwitterProvider;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;
import static com.squareup.picasso.Picasso.with;
import static io.reactivex.Observable.empty;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.UUID.randomUUID;
import static java.util.concurrent.ThreadLocalRandom.current;
import static java.util.concurrent.TimeUnit.SECONDS;
import static stream.meme.app.application.Comment.ERROR;
import static stream.meme.app.application.Comment.SENDING;
import static stream.meme.app.application.Comment.SUCCESS;
import static stream.meme.app.application.login.ProviderType.FACEBOOK;
import static stream.meme.app.application.login.ProviderType.GOOGLE;
import static stream.meme.app.application.login.ProviderType.TWITTER;

public class MemeStream extends Application {
    @Deprecated
    private Observable<List<Comment>> comments;
    @Deprecated
    private Observable<List<Post>> posts;
    @Deprecated
    private Observable<Profile> profile;

    public static final String KEY_TOKEN = "token";
    private Map<ProviderType, Provider> providers;
    private SharedPreferences prefs;
    private Service service;

    public MemeStream() throws IOException {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        comments = getProfile().map(profile -> asList(
                new Comment(profile,
                        "1d",
                        "Ut commodo elit nisi, non luctus metus gravida non. Donec at est vel libero pretium sollicitudin. Maecenas a ultric "),
                new Comment(profile,
                        "1d",
                        "Ut commodo elit nisi, non luctus metus gravida non. Donec at est vel libero pretium sollicitudin. Maecenas a ultric "),
                new Comment(profile,
                        "1d",
                        "Ut commodo elit nisi, non luctus metus gravida non. Donec at est vel libero pretium sollicitudin. Maecenas a ultric ")));

        posts = getComments(null).map(comments -> asList(
                new Post(randomUUID(),
                        "test0",
                        "page " + 0,
                        "https://i.vimeocdn.com/portrait/58832_300x300",
                        with(this).load("https://i.vimeocdn.com/portrait/58832_300x300").get(),
                        comments),
                new Post(randomUUID(),
                        "test1",
                        "page " + 0,
                        "https://i.vimeocdn.com/portrait/58832_300x300",
                        with(this).load("https://i.vimeocdn.com/portrait/58832_300x300").get(),
                        comments),
                new Post(randomUUID(),
                        "test2",
                        "page " + 0,
                        "https://i.vimeocdn.com/portrait/58832_300x300",
                        with(this).load("https://i.vimeocdn.com/portrait/58832_300x300").get(),
                        comments),
                new Post(randomUUID(),
                        "test3",
                        "page " + 0,
                        "https://i.vimeocdn.com/portrait/58832_300x300",
                        with(this).load("https://i.vimeocdn.com/portrait/58832_300x300").get(),
                        comments),
                new Post(randomUUID(),
                        "test4",
                        "page " + 0,
                        "https://i.vimeocdn.com/portrait/58832_300x300",
                        with(this).load("https://i.vimeocdn.com/portrait/58832_300x300").get(),
                        comments),
                new Post(randomUUID(),
                        "test5",
                        "page " + 0,
                        "https://i.vimeocdn.com/portrait/58832_300x300",
                        with(this).load("https://i.vimeocdn.com/portrait/58832_300x300").get(),
                        comments)));

        prefs = getSharedPreferences(getPackageName() + "authentication", MODE_PRIVATE);

        providers = new HashMap<>();
        providers.put(FACEBOOK, new FacebookProvider());
        providers.put(TWITTER, new TwitterProvider(this));
        providers.put(GOOGLE, new GoogleProvider(this));
    }

    //--Posts--
    public Observable<List<Post>> loadPosts() {
        return loadPosts(null);
    }

    public Observable<List<Post>> loadPosts(UUID last) {
//      return service.posts(last);
        return posts.delay(1, SECONDS);
    }


    //--Authentication--
    public boolean isAuthenticated() {
//        return getSharedPreferences().contains(KEY_TOKEN);
        return true;
    }

    public Observable<Boolean> authenticate(ProviderType type, Activity activity) {
        if (providers.containsKey(type))
            return providers.get(type)
                    .login(activity)
                    .toObservable()
                    .flatMap(providerToken -> getService().login(type, providerToken))
                    .map(token -> token != null &&
                            prefs.edit().putString(KEY_TOKEN, token).commit());
        else
            return empty();
    }


    //--Profile--
    public Observable<Profile> getProfile() {
        return !isAuthenticated() ? empty() : Observable.fromCallable(() -> new Profile(
                "Exerosis",
                with(this).load("http://gameplaying.info/wp-content/uploads/2017/05/nier-automata-2b-type-art-by-GoddessMechanic.jpg").get(),
                "exerosis@gmail.com",
                randomUUID(),
                FACEBOOK,
                TWITTER)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    //--Comments--
    public Observable<List<Comment>> getComments(UUID post) {
//      return service.comments(post).toObservable();
        return comments.delay(1, SECONDS);
    }

    public Observable<List<Comment>> addComment(UUID post, String comment) {
        return Observable.<List<Comment>>empty()
                .onErrorResumeNext(newComment(comment, ERROR))
                .mergeWith(newComment(comment, SENDING))
                .mergeWith(newComment(comment, current().nextBoolean() ? ERROR : SUCCESS).delay(1, SECONDS));
    }

    private Observable<List<Comment>> newComment(String comment, Boolean status) {
        return getProfile().map(user -> singletonList(new Comment(user, status == null ? "now" : status ? "sending" : "error", comment, status)));
    }


    public Observable<Boolean> rate(UUID post, Boolean vote){
        return service.rate(post, vote).startWith(vote);
    }


    //--Getters--
    private Service getService() {
        if (service == null) {
            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES)
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                    .baseUrl("http://192.168.1.3:5000/").build();
            service = retrofit.create(Service.class);
        }
        return service;
    }

    public Map<ProviderType, Provider> getProviders() {
        return providers;
    }

    public SharedPreferences getSharedPreferences() {
        return prefs;
    }
}