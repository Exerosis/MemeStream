package stream.meme.app.application.services;

import io.reactivex.Maybe;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MemeService {

    @POST("auth")
    Maybe<LoginResponse> login(@Body LoginRequest login);
}
