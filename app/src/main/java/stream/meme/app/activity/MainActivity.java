package stream.meme.app.activity;

import android.content.Intent;

import com.bluelinelabs.conductor.RouterTransaction;

import stream.meme.app.application.MemeStream;
import stream.meme.app.application.login.Login;
import stream.meme.app.util.RouterActivity;
import stream.meme.app.controller.LoginController;
import stream.meme.app.controller.StreamContainerController;

public class MainActivity extends RouterActivity {
    @Override
    protected RouterTransaction onRouterTransaction() {
        return RouterTransaction.with(!((MemeStream) getApplicationContext()).isAuthenticated() ? new StreamContainerController() : new LoginController());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (Login login : ((MemeStream) getApplicationContext()).getLogins().values())
            login.onActivityResult(requestCode, resultCode, data);
    }
}