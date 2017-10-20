package stream.meme.app.application.services;

import lombok.Data;
import stream.meme.app.application.login.LoginType;

@Data
public class LoginRequest {
    final LoginType providerName;
    final String accessToken;
}