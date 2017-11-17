package stream.meme.app.controller;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.common.base.Optional;

import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import stream.meme.app.R;
import stream.meme.app.application.Comment;
import stream.meme.app.application.MemeStream;
import stream.meme.app.application.User;
import stream.meme.app.databinding.CommentsViewBinding;
import stream.meme.app.util.bivsc.DatabindingBIVSCModule;
import stream.meme.app.util.bivsc.Reducer;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
import static com.bluelinelabs.conductor.RouterTransaction.with;
import static com.google.common.base.Optional.fromNullable;
import static com.jakewharton.rxbinding2.view.RxView.clicks;
import static com.jakewharton.rxbinding2.view.RxView.enabled;
import static com.jakewharton.rxbinding2.widget.RxTextView.textChanges;
import static java.util.UUID.*;
import static stream.meme.app.util.Operators.ifPresent;
import static stream.meme.app.util.bivsc.Reducer.controller;

public class CommentsController extends DatabindingBIVSCModule<CommentsViewBinding, CommentsController.State> {
    public static final String EXTRA_POST = "POST";
    private static final int MIN_LENGTH = 10;
    private final Intents intents = new Intents();
    private final UUID post;
    private final Comments comments;
    private MemeStream memeStream;

    public CommentsController(Bundle bundle) {
        super(R.layout.comments_view);
        post = fromString(bundle.getString(EXTRA_POST));
        comments = new Comments(post, false);
    }

    @Override
    protected void onContextAvailable(@NonNull Context context) {
        memeStream = (MemeStream) getApplicationContext();
        getActivity().getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_PAN);
        super.onContextAvailable(context);
    }

    static class Intents {
        Observable<String> RepliedIntent;
    }

    class State {
        Comment replying = null;

        Optional<Comment> replying() {
            return fromNullable(replying);
        }
    }

    interface Partials {
        static Reducer<State> Reply(Comment replying) {
            return state -> {
                state.replying = replying;
                return state;
            };
        }
    }

    @Override
    public Observable<State> getController() {
        //Send and refresh comments.
        intents.RepliedIntent.subscribe(comment -> memeStream
                .addComment(post, comment)
                .subscribe(comments::addComments)
        );

        //Fill in user mention when reply is clicked.
        return controller(new State(), comments.onReply().map(Partials::Reply));
    }

    @Override
    public BiConsumer<Observable<CommentsViewBinding>, Observable<State>> getBinder() {
        return (views, states) -> {
            //Notify the controller when
            intents.RepliedIntent = views
                    .switchMap(view -> clicks(view.send)
                            .map(ignored -> view.reply.getText().toString())
                            .doOnNext(ignored -> view.reply.getText().clear()));

            views.subscribe(view -> {
                getChildRouter(view.comments).setRoot(with(comments));

                //Setup Recycler view and reply view.
                view.reply.requestFocus();

                //Append mention when a user clicks reply.
                states.map(State::replying)
                        .compose(ifPresent())
                        .map(Comment::getAuthor)
                        .map(User::getName)
                        .subscribe(name -> {
                            view.reply.append("@" + name + " ");
                            view.reply.requestFocus();
                        });

                //Disable send if the reply is under 10 characters.
                textChanges(view.reply)
                        .map(text -> text.length() >= MIN_LENGTH)
                        .startWith(false)
                        .distinctUntilChanged()
                        .subscribe(enabled(view.send));
            });
        };
    }
}