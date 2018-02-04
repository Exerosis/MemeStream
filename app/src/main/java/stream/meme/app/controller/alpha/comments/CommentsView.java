package stream.meme.app.controller.alpha.comments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;

import com.google.common.base.Optional;

import java.util.List;

import io.reactivex.Observable;
import stream.meme.app.R;
import stream.meme.app.application.Comment;
import stream.meme.app.application.MemeStream;
import stream.meme.app.controller.alpha.ListView;
import stream.meme.app.databinding.CommentsViewBinding;
import stream.meme.app.util.bivsc.Reducer;
import stream.meme.app.util.components.components.StatefulViewComponent;
import stream.meme.app.util.components.adapters.ListAdapter;

import static com.jakewharton.rxbinding2.view.RxView.clicks;
import static com.jakewharton.rxbinding2.view.RxView.enabled;
import static com.jakewharton.rxbinding2.widget.RxTextView.textChanges;
import static java.util.UUID.randomUUID;
import static stream.meme.app.util.Operators.always;
import static stream.meme.app.util.Operators.ifPresent;
import static stream.meme.app.util.components.adapters.ListAdapter.CONTENT;

public class CommentsView extends StatefulViewComponent<CommentsView.State, CommentsViewBinding> {
    private static final int MIN_LENGTH = 10;
    private final MemeStream memeSteam;

    public CommentsView(@NonNull Context context) {
        super(context, R.layout.comments_view);
        memeSteam = (MemeStream) context.getApplicationContext();

        //Feed the data to the list adapter.
        Observable<List<Comment>> data = getStates()
                .map(State::data)
                .distinctUntilChanged();
        final ListAdapter<Comment> adapter = new ListAdapter<>(data, (first, second) ->
                !first.getStatus().equals(second.getStatus()) ? first.equals(second) : CONTENT
        );

/*

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
*/


        //Add a comment whenever send is clicked
        getViews().switchMap(view -> clicks(view.send)
                .compose(always(view.reply::getText))
                .doOnNext(Editable::clear)
                .map(Editable::toString))
                .flatMap(comment -> memeSteam.addComment(randomUUID(), comment))
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
