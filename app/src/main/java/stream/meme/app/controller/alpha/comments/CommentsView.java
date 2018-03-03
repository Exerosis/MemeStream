package stream.meme.app.controller.alpha.comments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;

import com.google.common.base.Optional;

import java.util.UUID;

import stream.meme.app.R;
import stream.meme.app.application.Comment;
import stream.meme.app.application.MemeStream;
import stream.meme.app.controller.alpha.ListView;
import stream.meme.app.databinding.CommentsViewBinding;
import stream.meme.app.util.bivsc.Reducer;
import stream.meme.app.util.components.adapters.ListAdapter;
import stream.meme.app.util.components.components.StatefulViewComponent;

import static com.jakewharton.rxbinding2.view.RxView.clicks;
import static com.jakewharton.rxbinding2.view.RxView.enabled;
import static com.jakewharton.rxbinding2.widget.RxTextView.textChanges;
import static stream.meme.app.util.Operators.always;
import static stream.meme.app.util.Operators.ifPresent;
import static stream.meme.app.util.components.adapters.ListAdapter.NOTHING;

public class CommentsView extends StatefulViewComponent<CommentsView.State, CommentsViewBinding> {
    private static final int MIN_LENGTH = 10;
    //Replace with observable in some way?
    private UUID post;

    public CommentsView(@NonNull Context context) {
        super(context, R.layout.comments_view);
        final MemeStream memeSteam = (MemeStream) context.getApplicationContext();

        setState(new State()); //Geez don't forget this, it takes a while to debug.

        //Feed the data to the list adapter.
        final ListAdapter<Comment> adapter = new ListAdapter<>(getStates()
                .map(State::data), (first, second) -> {
            if (!first.equals(second))
                return NOTHING;
            return first.getStatus().equals(second.getStatus());
        });


        //Attach the adapter to the list view component.
        getComponents(components -> {
            components.comments.attach(this).adapter(adapter);

            //Update the state whenever a user clicks reply on a comment.
            adapter.bind(CommentView::new)
                    .flatMap(comment -> comment.onReply()
                            .compose(always(comment::getState))
                    ).map(Partials::Reply)
                    .subscribe(this::applyPartial);
        });


        //Add a comment whenever send is clicked
        getViews().switchMap(view -> clicks(view.send)
                .compose(always(view.reply::getText))
                .doAfterNext(Editable::clear)
                .map(Editable::toString))
                .flatMap(comment -> memeSteam.addComment(post, comment))
                .map(ListView.Partials::<Comment, State>Loaded)
                .subscribe(this::applyPartial);


        getViews().subscribe(view -> {
            //Disable send if the reply is under 10 characters.
            textChanges(view.reply)
                    .map(text -> text.length() >= MIN_LENGTH)
                    .startWith(false)
                    .distinctUntilChanged()
                    .subscribe(enabled(view.send));


            //Append mention when a user clicks reply.
            getStates().map(State::replying)
                    .compose(ifPresent())
                    .subscribe(comment -> {
                        view.reply.append("@" + comment.getAuthor().getName() + " ");
                        view.reply.requestFocus();
                    });
        });
    }

    public void setPost(UUID post) {
        this.post = post;
    }


    interface Partials {
        static Reducer<State> Reply(Comment reply) {
            return state -> {
                state.reply = reply;
                return state;
            };
        }
    }

    class State extends ListView.State<Comment> {
        Comment reply = null;

        Optional<Comment> replying() {
            return Optional.fromNullable(reply);
        }
    }
}
