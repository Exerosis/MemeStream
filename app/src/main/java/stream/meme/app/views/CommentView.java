package stream.meme.app.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import stream.meme.app.R;
import stream.meme.app.databinding.CommentViewBinding;
import stream.meme.app.util.components.components.ViewComponent;

/**
 * Created by Exerosis on 1/14/2018.
 */

public class CommentView extends ViewComponent<CommentViewBinding> {
    public CommentView(@NonNull Context context) {
        super(context);

    }

    @Override
    protected int inflate(@NonNull AttributeSet attributes) {
        return R.layout.comment_view;
    }
}
