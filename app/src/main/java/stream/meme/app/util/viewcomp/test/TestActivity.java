package stream.meme.app.util.viewcomp.test;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import stream.meme.app.R;
import stream.meme.app.util.viewcomp.alpha.ViewDelegateTwo;


public class TestActivity extends AppCompatActivity {
    static {
        GlobalTagRegistry.getInstance().register(TestComponent.class, SecondComponent.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle in) {
        setContentView(R.layout.test_activty);
        ViewDelegateTwo delegateTwo = findViewById(R.id.view_delegate);
        String rotationsSoFar = delegateTwo.getRots();
        System.out.println(rotationsSoFar);
        super.onCreate(in);
    }

    //This can be done application wide from android.app.Application.
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(GlobalTagRegistry.injectContext(context));
    }
}
