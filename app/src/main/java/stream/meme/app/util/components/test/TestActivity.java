package stream.meme.app.util.components.test;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import stream.meme.app.R;
import stream.meme.app.controller.alpha.ListView;
import stream.meme.app.databinding.TestActivtyBinding;

import static android.view.LayoutInflater.from;

public class TestActivity extends AppCompatActivity {

    static {
        GlobalTagRegistry.getInstance().register(ThirdLayout.class, FourthLayerView.class, ListView.class, TestViewComponent.class, TestListViewComponent.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle in) {
        TestActivtyBinding inflate = DataBindingUtil.inflate(from(this), R.layout.test_activty, null, false);
        setContentView(inflate.getRoot());
        super.onCreate(in);
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(GlobalTagRegistry.injectContext(context));
    }
}
