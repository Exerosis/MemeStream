package stream.meme.app.application;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import stream.meme.app.login.LoginType;

import static com.facebook.CallbackManager.Factory.create;
import static com.facebook.login.LoginManager.getInstance;
import static com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API;
import static com.google.android.gms.auth.api.Auth.GoogleSignInApi;
import static com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN;
import static java.util.Collections.singletonList;

public class LoginManager {
    private static final int REQUEST_SIGN_IN = 1;
    private final TwitterAuthClient twitterAuthClient;
    private final GoogleApiClient googleApiClient;
    private final CallbackManager callbackManager;
    private Subject<String> loginResults = PublishSubject.create();

    public LoginManager(Context context) {
        getInstance().registerCallback(callbackManager = create(), new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginResults.onNext(loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                loginResults.onError(new FacebookException("Login attempt was canceled."));
            }

            @Override
            public void onError(FacebookException error) {
                loginResults.onError(error);
            }
        });

        //--Twitter--
        Twitter.initialize(context);
        twitterAuthClient = new TwitterAuthClient();

        //--Google--
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(GOOGLE_SIGN_IN_API, new GoogleSignInOptions.Builder(DEFAULT_SIGN_IN).requestProfile().build())
                .build();
    }

    public Observable<String> startLogin(LoginType type, Activity activity) {
        switch (type) {
            case FACEBOOK: {
                getInstance().logInWithReadPermissions(activity, singletonList("email"));
                break;
            }
            case TWITTER: {
                twitterAuthClient.authorize(activity, new Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> result) {
                        loginResults.onNext(result.data.getAuthToken().token);
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        loginResults.onError(exception);
                    }
                });
                break;
            }
            case GOOGLE: {
                activity.startActivityForResult(GoogleSignInApi.getSignInIntent(googleApiClient), REQUEST_SIGN_IN);
                break;
            }
            default: {
                loginResults.onError(new UnsupportedOperationException(type.toString() + " sign in not yet supported!"));
            }
        }
        return loginResults;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        twitterAuthClient.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGN_IN) {
            GoogleSignInResult result = GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess())
                loginResults.onNext(result.getSignInAccount().getServerAuthCode());
            else
                loginResults.onError(loginResults.getThrowable());
        }
    }
}
