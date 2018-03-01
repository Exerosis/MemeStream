package stream.meme.app.util.components.test;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import stream.meme.app.R;
import stream.meme.app.controller.alpha.ListView;
import stream.meme.app.databinding.TestActivtyBinding;
import stream.meme.app.util.components.layertest.ViewFour;
import stream.meme.app.util.components.layertest.ViewOne;
import stream.meme.app.util.components.layertest.ViewThree;
import stream.meme.app.util.components.layertest.ViewTwo;

import static stream.meme.app.util.components.test.GlobalTagRegistry.getInstance;
import static stream.meme.app.util.components.test.GlobalTagRegistry.injectContext;

public class TestActivity extends AppCompatActivity {
    static {
        getInstance().register(ViewOne.class, ViewTwo.class, ViewThree.class, ViewFour.class, ListView.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle in) {
        View view = getLayoutInflater().inflate(R.layout.test_activty, null, false);
        TestActivtyBinding inflate = DataBindingUtil.bind(view);

        setContentView(inflate.getRoot());
        super.onCreate(in);
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(injectContext(context));
    }
}
