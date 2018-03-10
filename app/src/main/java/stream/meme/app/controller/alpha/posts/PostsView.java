package stream.meme.app.controller.alpha.posts;

import android.content.Context;
import android.support.annotation.NonNull;

import io.reactivex.Observable;
import stream.meme.app.R;
import stream.meme.app.application.MemeStream;
import stream.meme.app.application.Post;
import stream.meme.app.controller.alpha.ListView;
import stream.meme.app.databinding.PostsViewBinding;
import stream.meme.app.util.components.adapters.ListAdapter;
import stream.meme.app.util.components.components.StatefulViewComponent;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static stream.meme.app.util.Operators.always;
import static stream.meme.app.util.components.adapters.ListAdapter.NOTHING;

public class PostsView extends StatefulViewComponent<PostsView.State, PostsViewBinding> {
    private final Observable<Post> clicked;

    public PostsView(@NonNull Context context) {
        super(context, R.layout.posts_view);
        final MemeStream memeStream = (MemeStream) getContext().getApplicationContext();

        setState(new State());

        memeStream.loadPosts().map(ListView.Partials::<Post, State>Loaded).observeOn(mainThread()).subscribe(this::applyPartial);

        final ListAdapter<Post> adapter = new ListAdapter<>(getStates()
                .map(PostsView.State::data), (first, second) -> {
            if (!first.equals(second))
                return NOTHING;
            return first.getRating().equals(second.getRating());
        });

        clicked = adapter.bind(PostView::new).flatMap(post ->
                post.onClicked().compose(always(post::getState))
        );

        getComponents(components -> {
            components.posts.attach(this)
                    .paginate(adapter)
                    .loading(memeStream.loadPosts().map(ListView.Partials::Loaded));
        });
    }


    public Observable<Post> onClick() {
        return clicked;
    }


    class State extends ListView.State<Post> {

    }
}
