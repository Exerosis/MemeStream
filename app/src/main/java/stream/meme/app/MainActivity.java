package stream.meme.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bluelinelabs.conductor.ChangeHandlerFrameLayout;
import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;

import stream.meme.app.application.MemeStream;
import stream.meme.app.login.LoginController;
import stream.meme.app.stream.StreamController;

public class MainActivity extends AppCompatActivity {
    private Router router;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        ChangeHandlerFrameLayout container = findViewById(R.id.container);
        router = Conductor.attachRouter(this, container, savedInstanceState);
        if (!router.hasRootController())
            ((MemeStream) getApplicationContext()).isAuthenticated().subscribe(authenticated ->
                    router.setRoot(RouterTransaction.with(authenticated ? new StreamController() : new LoginController())));
    }

    @Override
    public void onBackPressed() {
        if (!router.handleBack())
            super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ((MemeStream) getApplicationContext()).getLoginManager().onActivityResult(requestCode, resultCode, data);
    }
}