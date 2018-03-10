package stream.meme.app.controller.alpha.app;

import stream.meme.app.R;
import stream.meme.app.controller.alpha.ListView;
import stream.meme.app.controller.alpha.comments.CommentView;
import stream.meme.app.controller.alpha.comments.CommentsView;
import stream.meme.app.controller.alpha.main.MainView;
import stream.meme.app.controller.alpha.posts.PostView;
import stream.meme.app.controller.alpha.posts.PostsView;
import stream.meme.app.databinding.MainActivityBinding;
import stream.meme.app.util.components.components.ComponentActivity;
import stream.meme.app.util.components.layertest.ViewFour;
import stream.meme.app.util.components.layertest.ViewOne;
import stream.meme.app.util.components.layertest.ViewThree;
import stream.meme.app.util.components.layertest.ViewTwo;

import static stream.meme.app.util.components.test.GlobalTagRegistry.getInstance;

public class MainActivity extends ComponentActivity<MainActivityBinding> {
    static {
        getInstance().register(
                ViewOne.class, ViewTwo.class,
                ViewThree.class, ViewFour.class,
                ListView.class, CommentView.class,
                CommentsView.class, PostsView.class,
                PostView.class, MainView.class
        );
    }

    public MainActivity() {
        super(R.layout.main_activity, getInstance());
        getComponents(view -> view.main.setActivity(this));
    }
}