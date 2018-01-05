package stream.meme.app.util;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Exerosis on 1/1/2018.
 */

public class Test extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle in) {
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new LayoutInflater.Factory2() {
            @Override
            public View onCreateView(View view, String s, Context context, AttributeSet attributeSet) {
                return null;
            }

            @Override
            public View onCreateView(String s, Context context, AttributeSet attributeSet) {
                return null;
            }
        });
        super.onCreate(in);
    }
}
