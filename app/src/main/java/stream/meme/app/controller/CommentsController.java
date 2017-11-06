package stream.meme.app.controller;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import stream.meme.app.R;
import stream.meme.app.application.Comment;
import stream.meme.app.application.User;
import stream.meme.app.databinding.CommentViewBinding;
import stream.meme.app.databinding.CommentsViewBinding;
import stream.meme.app.util.Nothing;
import stream.meme.app.util.bivsc.DatabindingBIVSCModule;
import stream.meme.app.util.bivsc.Reducer;
import stream.meme.app.util.rxadapter.RxAdapterAlpha;
import stream.meme.app.util.rxadapter.RxListCallback;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
import static com.google.common.base.Optional.fromNullable;
import static com.jakewharton.rxbinding2.view.RxView.clicks;
import static com.jakewharton.rxbinding2.view.RxView.visibility;
import static com.jakewharton.rxbinding2.widget.RxTextView.color;
import static com.jakewharton.rxbinding2.widget.RxTextView.text;
import static com.jakewharton.rxbinding2.widget.RxTextView.textChanges;
import static java.util.concurrent.TimeUnit.SECONDS;
import static stream.meme.app.util.Nothing.always;
import static stream.meme.app.util.Optionals.ifPresent;

public class CommentsController extends DatabindingBIVSCModule<CommentsViewBinding, CommentsController.State> {
    private static final int MIN_LENGTH = 10;
    private final Intents intents = new Intents();

    public CommentsController() {
        super(R.layout.comments_view);
    }

    static class Intents {
        Subject<User> ReplyIntent = PublishSubject.create();
        Observable<String> RepliedIntent;
        Observable<Nothing> RefreshIntent;
    }

    @Override
    protected void onContextAvailable(@NonNull Context context) {
        getActivity().getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_PAN);
        super.onContextAvailable(context);
    }

    class State {
        List<Comment> comments = new ArrayList<>();
        Throwable error = null;
        boolean loading = true;
        User replying = null;

        Optional<User> replying() {
            return Optional.fromNullable(replying);
        }

        boolean loading() {
            return loading;
        }

        Optional<Throwable> throwable() {
            return fromNullable(error);
        }

        List<Comment> comments() {
            return comments;
        }
    }


    interface Partials {
        static Reducer<State> PartialError(Throwable error) {
            return state -> {
                state.error = error;
                return state;
            };
        }

        static Reducer<State> PartialLoading() {
            return state -> {
                state.loading = true;
                return state;
            };
        }

        static Reducer<State> PartialReply(User user) {
            return state -> {
                state.replying = user;
                return state;
            };
        }

        static Reducer<State> PartialLoaded(List<Comment> comments) {
            return state -> {
                state.comments = comments;
                state.loading = false;
                return state;
            };
        }
    }

    @Override
    public BiConsumer<Observable<CommentsViewBinding>, Observable<State>> getBinder() {
        return (views, states) -> {
            new RxAdapterAlpha<>(views.map(view -> view.comments), new RxListCallback<>(states.map(State::comments)))
                    .<CommentViewBinding>bind(R.layout.comment_view, (commentView, comments) -> {
                        Observable<User> author = comments
                                .map(Comment::getAuthor)
                                .distinctUntilChanged();

                        //--Author--
                        author.subscribe(user -> {
                            commentView.author.setText(user.getName());
                            commentView.image.setImageBitmap(user.getImage());
                        });

                        //--Reply--
                        author.switchMap(user ->
                                always(user, clicks(commentView.reply)))
                                .subscribe(intents.ReplyIntent);

                        //--Content--
                        comments.map(Comment::getContent)
                                .distinctUntilChanged()
                                .subscribe(text(commentView.content));

                        //--Sent--
                        ifPresent(comments
                                .map(Comment::isSent))
                                .map(sent -> getResources().getColor(sent ? R.color.disabled_text : R.color.red))
                                .subscribe(color(commentView.content));

                        //--Date--
                        comments.map(Comment::getDate)
                                .distinctUntilChanged()
                                .subscribe(text(commentView.date));
                    });

            intents.RepliedIntent = views
                    .switchMap(view -> clicks(view.send)
                            .map(ignored -> view.reply.getText().toString()));

            views.subscribe(view -> {
                view.reply.requestFocus();

                //--Reply Hint--
                ifPresent(states
                        .map(State::replying)
                        .distinctUntilChanged())
                        .map(User::getName)
                        .subscribe(name -> {
                            view.reply.append("@" + name);
                            view.reply.requestFocus();
                        });

                //--Send Visibility--
                textChanges(view.reply)
                        .debounce(1, SECONDS)
                        .map(text -> text.length() > MIN_LENGTH)
                        .startWith(false)
                        .subscribe(visibility(view.send));
            });
        };
    }

    @Override
    public Observable<State> getController() {
        return Reducer.controller(new State(),
                intents.ReplyIntent.map(Partials::PartialReply)
        );
    }

}