package stream.meme.app.util;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.bluelinelabs.conductor.ChangeHandlerFrameLayout;
import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;

import stream.meme.app.R;

public abstract class RouterActivity extends AppCompatActivity {
    private Router router;

    abstract protected RouterTransaction onRouterTransaction();

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.controller_activity);

        ChangeHandlerFrameLayout container = findViewById(R.id.container);
        router = Conductor.attachRouter(this, container, state);
        if (!router.hasRootController())
            router.setRoot(onRouterTransaction());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != android.R.id.home)
            return super.onOptionsItemSelected(item);
        onBackPressed();
        return true;
    }


    @Override
    public void onBackPressed() {
        if (!router.handleBack())
            super.onBackPressed();
    }
}