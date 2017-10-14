package stream.meme.app.application;

import io.reactivex.Maybe;
import retrofit2.http.Body;
import retrofit2.http.POST;
import stream.meme.app.application.login.LoginType;

public interface MemeService {

    @POST("auth")
    Maybe<LoginResponse> login(@Body LoginRequest login);

    class LoginResponse {
        String accessToken;
    }

    class LoginRequest {
        LoginType providerName;
        String accessToken;

        public LoginRequest(LoginType providerName, String accessToken) {
            this.providerName = providerName;
            this.accessToken = accessToken;
        }
    }
}
