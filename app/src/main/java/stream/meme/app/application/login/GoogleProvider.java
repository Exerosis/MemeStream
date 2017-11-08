package stream.meme.app.application.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;

import io.reactivex.Maybe;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API;
import static com.google.android.gms.auth.api.Auth.GoogleSignInApi;
import static com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN;

public class GoogleProvider implements Provider {
    private static final int REQUEST_SIGN_IN = 1;
    private final GoogleApiClient googleApiClient;
    private Subject<String> loginResults = PublishSubject.create();

    public GoogleProvider(Context context) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(GOOGLE_SIGN_IN_API, new GoogleSignInOptions.Builder(DEFAULT_SIGN_IN).requestProfile().build())
                .build();
    }

    @Override
    public Maybe<String> login(Activity activity) {
        return Maybe.create(subscriber -> {
            activity.startActivityForResult(GoogleSignInApi.getSignInIntent(googleApiClient), REQUEST_SIGN_IN);
            loginResults.subscribe(subscriber::onSuccess, subscriber::onError);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        if (requestCode == REQUEST_SIGN_IN) {
            GoogleSignInResult result = GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess())
                loginResults.onNext(result.getSignInAccount().getServerAuthCode());
            else if (loginResults.getThrowable() != null)
                loginResults.onError(loginResults.getThrowable());
        }
    }
}
