package stream.meme.app.controller;

import com.bluelinelabs.conductor.RouterTransaction;

import stream.meme.app.util.RouterActivity;

/**
 * Created by Home on 11/22/2017.
 */

public class Test extends RouterActivity {
    @Override
    protected RouterTransaction onRouterTransaction() {
        return RouterTransaction.with(new StreamContainerController());
    }
}
