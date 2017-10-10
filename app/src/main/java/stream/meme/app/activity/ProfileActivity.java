package stream.meme.app.activity;

import com.bluelinelabs.conductor.RouterTransaction;

import stream.meme.app.controller.ProfileController;
import stream.meme.app.util.RouterActivity;

public class ProfileActivity extends RouterActivity {
    @Override
    protected RouterTransaction onRouterTransaction() {
        return RouterTransaction.with(new ProfileController());
    }
}