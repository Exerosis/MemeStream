package stream.meme.app.application;

import java.util.List;
import java.util.UUID;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import stream.meme.app.application.login.ProviderType;

public interface Service {
    //--Auth--
    @POST("auth")
    Observable<String> login(@Query("provider") ProviderType type, @Body String accessToken);

    //--Comments--
    @GET("posts/{id}/comments")
    Observable<List<Comment>> comments(@Path("id") UUID post);

    @POST("posts/{id}/comment")
    Observable<List<Comment>> comment(@Path("id") UUID post, @Body String comment);

    //--Posts--
    @GET("posts")
    Observable<List<Post>> posts(@Query("last") UUID last);

    @POST("posts/{id}/rate")
    Observable<Post> rate(@Path("id") UUID post, @Query("rating") Boolean rating);
}
