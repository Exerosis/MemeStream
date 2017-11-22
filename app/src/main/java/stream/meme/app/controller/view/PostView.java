package stream.meme.app.controller.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.widget.FrameLayout;

import io.reactivex.Observable;
import stream.meme.app.R;
import stream.meme.app.application.Post;
import stream.meme.app.databinding.MemeViewBinding;

import static android.support.v4.util.Pair.create;
import static android.view.LayoutInflater.from;
import static com.jakewharton.rxbinding2.view.RxView.clicks;
import static stream.meme.app.application.Post.DOWN_VOTE;
import static stream.meme.app.application.Post.UP_VOTE;
import static stream.meme.app.util.Operators.always;
import static stream.meme.app.util.Optionals.ifPresent;

/**
 * Created by Exerosis on 11/21/2017.
 */
public class PostView extends FrameLayout {
    private final MemeViewBinding binding;
    private Post post;

    public PostView(@NonNull Context context) {
        super(context);
        binding = DataBindingUtil.inflate(from(context), R.layout.meme_view, this, true);
    }

    public void post(Observable<Post> post) {
        post.subscribe(this::post);
    }

    public void post(Post post) {
        this.post = post;
        binding.image.setImageBitmap(post.getThumbnail());
        binding.title.setText(post.getTitle());
        binding.subtitle.setText(post.getSubtitle());
        ifPresent(post.getRating(), rating -> {
            binding.upvote.setChecked(rating);
            binding.downvote.setChecked(!rating);
        });
    }

    public Observable<Pair<Post, Boolean>> rated() {
        return clicks(binding.upvote)
                .compose(always(() -> create(post, UP_VOTE)))
                .mergeWith(clicks(binding.downvote)
                        .compose(always(() -> create(post, DOWN_VOTE))));
    }

    public Observable<Post> shared() {
        return clicks(binding.share).compose(always(this::post));
    }

    public Observable<Post> reply() {
        return clicks(binding.comments).compose(always(this::post));
    }

    public Observable<Post> clicked() {
        return clicks(binding.getRoot()).compose(always(this::post));
    }

    public Post post() {
        return post;
    }
}