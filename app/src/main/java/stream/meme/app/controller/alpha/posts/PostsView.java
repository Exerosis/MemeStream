package stream.meme.app.controller.alpha.posts;

import android.content.Context;
import android.support.annotation.NonNull;

import stream.meme.app.R;
import stream.meme.app.application.MemeStream;
import stream.meme.app.application.Post;
import stream.meme.app.controller.alpha.ListView;
import stream.meme.app.databinding.PostsListBinding;
import stream.meme.app.util.components.adapters.ListAdapter;
import stream.meme.app.util.components.components.StatefulViewComponent;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static stream.meme.app.util.components.adapters.ListAdapter.NOTHING;

public class PostsView extends StatefulViewComponent<PostsView.State, PostsListBinding> {

    public PostsView(@NonNull Context context) {
        super(context, R.layout.posts_list);
        final MemeStream memeStream = (MemeStream) getContext().getApplicationContext();

        setState(new State());

        memeStream.loadPosts().map(ListView.Partials::<Post, State>Loaded).observeOn(mainThread()).subscribe(this::applyPartial);

        final ListAdapter<Post> adapter = new ListAdapter<>(getStates()
                .map(PostsView.State::data), (first, second) -> {
            if (!first.equals(second))
                return NOTHING;
            return first.getRating().equals(second.getRating());
        });

        getComponents(components -> {
            components.posts.attach(this)
                    .paginate(adapter)
                    .loading(memeStream.loadPosts().map(ListView.Partials::Loaded));
            adapter.bind(PostView::new);
        });
    }


    class State extends ListView.State<Post> {

    }
}
