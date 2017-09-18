package stream.meme.app.application.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import io.reactivex.Maybe;

public class TwitterLogin implements Login {
    private final TwitterAuthClient twitterAuthClient;

    public TwitterLogin(Context context) {
        Twitter.initialize(context);
        twitterAuthClient = new TwitterAuthClient();
    }

    @Override
    public Maybe<String> login(Activity activity) {
        return Maybe.create(subscriber -> {
            twitterAuthClient.authorize(activity, new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    subscriber.onSuccess(result.data.getAuthToken().token);
                }

                @Override
                public void failure(TwitterException exception) {
                    subscriber.onError(exception);
                }
            });
        });
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        twitterAuthClient.onActivityResult(requestCode, responseCode, data);
    }
}