package stream.meme.app.controller.alpha.app;

import java.util.UUID;

import stream.meme.app.R;
import stream.meme.app.databinding.CommentsActivtyBinding;
import stream.meme.app.util.components.components.ComponentActivity;
import stream.meme.app.util.components.test.GlobalTagRegistry;

import static stream.meme.app.controller.alpha.main.MainView.EXTRA_POST;

public class CommentsActivity extends ComponentActivity<CommentsActivtyBinding> {

    public CommentsActivity() {
        super(R.layout.comments_activty, GlobalTagRegistry.getInstance());
        getComponents(components -> components.comments.setPost((UUID) getIntent().getSerializableExtra(EXTRA_POST)));
    }
}