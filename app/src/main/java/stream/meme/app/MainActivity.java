package stream.meme.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bluelinelabs.conductor.ChangeHandlerFrameLayout;
import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;

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
            try {
                router.setRoot(RouterTransaction.with(new StreamController()));
            } catch (Exception eggsAgainstTheWall) {
                throw new RuntimeException(eggsAgainstTheWall);
            }
    }

    @Override
    public void onBackPressed() {
        if (!router.handleBack())
            super.onBackPressed();
    }
}