package stream.meme.app.util.components.test;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import stream.meme.app.R;
import stream.meme.app.controller.alpha.ListView;
import stream.meme.app.controller.alpha.comments.CommentView;
import stream.meme.app.controller.alpha.comments.CommentsView;
import stream.meme.app.controller.alpha.posts.PostView;
import stream.meme.app.controller.alpha.posts.PostsView;
import stream.meme.app.util.components.layertest.ViewFour;
import stream.meme.app.util.components.layertest.ViewOne;
import stream.meme.app.util.components.layertest.ViewThree;
import stream.meme.app.util.components.layertest.ViewTwo;

import static stream.meme.app.util.components.test.GlobalTagRegistry.getInstance;

public class TestActivity extends AppCompatActivity {
    static {
        getInstance().register(
                ViewOne.class, ViewTwo.class,
                ViewThree.class, ViewFour.class,
                ListView.class, CommentView.class,
                CommentsView.class, PostsView.class,
                PostView.class
        );
    }

    @Override
    protected void onCreate(@Nullable Bundle in) {
        final View view = LayoutInflater.from(this).inflate(R.layout.test_activty, null);
        view.invalidate();
        setContentView(view);
        super.onCreate(in);
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(GlobalTagRegistry.injectContext(context));
    }
}
