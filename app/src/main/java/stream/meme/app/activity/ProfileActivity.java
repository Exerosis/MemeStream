package stream.meme.app.activity;

import android.view.MenuItem;

import com.bluelinelabs.conductor.RouterTransaction;

import stream.meme.app.controller.ProfileController;
import stream.meme.app.util.RouterActivity;

public class ProfileActivity extends RouterActivity {
    @Override
    protected RouterTransaction onRouterTransaction() {
        return RouterTransaction.with(new ProfileController());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != android.R.id.home)
            return super.onOptionsItemSelected(item);
        onBackPressed();
        return true;
    }
}