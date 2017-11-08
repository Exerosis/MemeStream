package stream.meme.app.application.login;

import android.app.Activity;
import android.content.Intent;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;

import io.reactivex.Maybe;

import static com.facebook.CallbackManager.Factory.create;
import static com.facebook.login.LoginManager.getInstance;
import static java.util.Collections.singletonList;

public class FacebookProvider implements Provider {
    private final CallbackManager callbackManager;

    public FacebookProvider() {
        callbackManager = create();
    }

    @Override
    public Maybe<String> login(Activity activity) {
        return Maybe.create(subscriber -> {
            getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    subscriber.onSuccess(loginResult.getAccessToken().getToken());
                }

                @Override
                public void onCancel() {
                    subscriber.onError(new FacebookException("Provider attempt was canceled."));
                }

                @Override
                public void onError(FacebookException error) {
                    subscriber.onError(error);
                }
            });
            getInstance().logInWithReadPermissions(activity, singletonList("email"));
        });
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        callbackManager.onActivityResult(requestCode, responseCode, data);
    }
}
