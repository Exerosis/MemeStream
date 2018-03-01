package stream.meme.app.controller.alpha.posts;

import android.content.Context;
import android.support.annotation.NonNull;

import io.reactivex.Observable;
import stream.meme.app.R;
import stream.meme.app.application.Post;
import stream.meme.app.databinding.MemeViewBinding;
import stream.meme.app.util.Nothing;
import stream.meme.app.util.components.components.StatefulViewComponent;

import static com.jakewharton.rxbinding2.view.RxView.clicks;
import static stream.meme.app.application.Post.DOWN_VOTE;
import static stream.meme.app.application.Post.UP_VOTE;
import static stream.meme.app.util.Operators.always;
import static stream.meme.app.util.Operators.ignored;
import static stream.meme.app.util.Optionals.ifPresent;

public class PostView extends StatefulViewComponent<Post, MemeViewBinding> {
    private final Observable<Nothing> shared;
    private final Observable<Boolean> rated;
    private final Observable<Nothing> clicked;

    public PostView(@NonNull Context context) {
        super(context, R.layout.meme_view);

        shared = ignored(viewSwitchMap(view -> clicks(view.share)));

        clicked = ignored(viewSwitchMap(view -> clicks(view.getRoot())));

        rated = viewStateSwitchMap((view, post) ->
                clicks(view.upvote).compose(always(UP_VOTE)).mergeWith(
                        clicks(view.downvote).compose(always(DOWN_VOTE))
                ));


        getViews().subscribe(view -> getStates().subscribe(post -> {
            view.image.setImageBitmap(post.getThumbnail());
            view.title.setText(post.getTitle());
            view.subtitle.setText(post.getSubtitle());
            ifPresent(post.getRating(), rating -> {
                view.upvote.setChecked(rating);
                view.downvote.setChecked(!rating);
            });
        }));
    }

    public Observable<Boolean> onRated() {
        return rated;
    }

    public Observable<Nothing> onClicked() {
        return clicked;
    }

    public Observable<Nothing> onShared() {
        return shared;
    }
}