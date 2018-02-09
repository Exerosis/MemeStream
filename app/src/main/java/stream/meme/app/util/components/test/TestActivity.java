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
import stream.meme.app.util.components.superalpha.TestInflater;
import stream.meme.app.util.components.superalpha.TestInjectedView;

import static stream.meme.app.util.components.test.GlobalTagRegistry.getInstance;

public class TestActivity extends AppCompatActivity {

    static {
//        GlobalTagRegistry.getInstance().register(ThirdLayout.class, FourthLayerView.class, ListView.class, TestViewComponent.class, TestListViewComponent.class);
        getInstance().register(TestListViewComponent.class, ListView.class, ViewOne.class, ViewTwo.class, ViewThree.class, ViewFour.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle in) {
        View view = getLayoutInflater().inflate(R.layout.test_activty, null, false);
        TestActivtyBinding inflate = DataBindingUtil.bind(view);
        //Ok so at this point we have a fully injected view, but if our attempt to trick Android works out then we can still get
        //The fake injected class hehe.
        view.findViewById(R.id.test_injected_view);
        TestInjectedView injectedView = inflate.testInjectedView;

        //Depending on the result here we can figure out how databinding really works >:)

        setContentView(inflate.getRoot());
        super.onCreate(in);
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(TestInflater.inject(context));
    }
}
