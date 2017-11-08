package stream.meme.app.application.login;

import android.app.Activity;
import android.content.Intent;

import io.reactivex.Maybe;

public interface Provider {
    Maybe<String> login(Activity activity);

    void onActivityResult(int requestCode, int responseCode, Intent data);
}
