package stream.meme.app.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import stream.meme.app.R;
import stream.meme.app.application.Comment;
import stream.meme.app.application.MemeStream;
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
import static com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout.refreshes;
import static com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout.refreshing;
import static com.jakewharton.rxbinding2.view.RxView.clicks;
import static com.jakewharton.rxbinding2.view.RxView.enabled;
import static com.jakewharton.rxbinding2.widget.RxTextView.color;
import static com.jakewharton.rxbinding2.widget.RxTextView.text;
import static com.jakewharton.rxbinding2.widget.RxTextView.textChanges;
import static stream.meme.app.util.Nothing.NONE;
import static stream.meme.app.util.Operators.always;
import static stream.meme.app.util.Operators.ifPresent;
import static stream.meme.app.util.bivsc.Reducer.controller;

public class CommentsController extends DatabindingBIVSCModule<CommentsViewBinding, CommentsController.State> {
    private static final int MIN_LENGTH = 10;
    private final Intents intents = new Intents();
    private final UUID post = UUID.randomUUID();
    private MemeStream memeStream;

    public CommentsController() {
        super(R.layout.comments_view);
    }

    @Override
    protected void onContextAvailable(@NonNull Context context) {
        memeStream = (MemeStream) getApplicationContext();
        getActivity().getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_PAN);
        super.onContextAvailable(context);
    }

    static class Intents {
        Subject<User> ReplyIntent = PublishSubject.create();
        Observable<String> RepliedIntent;
        Observable<Nothing> RefreshIntent;
    }

    class State {
        List<Comment> comments = new ArrayList<>();
        Throwable error = null;
        boolean loading = true;
        User replying = null;

        Optional<User> replying() {
            return fromNullable(replying);
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
        static Reducer<State> RefreshError(Throwable error) {
            return state -> {
                state.error = error;
                return state;
            };
        }

        static Reducer<State> Refreshing() {
            return state -> {
                state.loading = true;
                return state;
            };
        }

        static Reducer<State> Refreshed(List<Comment> comments) {
            return state -> {
                state.comments.addAll(comments);
                state.loading = false;
                return state;
            };
        }

        static Reducer<State> Reply(User user) {
            return state -> {
                state.replying = user;
                return state;
            };
        }
    }

    @Override
    public Observable<State> getController() {
        return controller(new State(),
                //Fill in user mention when reply is clicked.
                intents.ReplyIntent.map(Partials::Reply),

                //Get top page of comments when refreshed.
                intents.RefreshIntent.flatMap(ignored -> memeStream
                        .getComments(post)
                        .map(Partials::Refreshed)
                        .onErrorReturn(Partials::RefreshError)
                        .startWith(Partials.Refreshing())
                ),

                //Send and refresh comments.
                intents.RepliedIntent.flatMap(comment -> memeStream
                        .addComment(post, comment)
                        .map(Partials::Refreshed)
                )
        );
    }

    @Override
    public BiConsumer<Observable<CommentsViewBinding>, Observable<State>> getBinder() {
        return (views, states) -> {
            RxAdapterAlpha<Comment> adapter = new RxAdapterAlpha<>(views.map(view -> view.comments), new RxListCallback<>(states.map(State::comments)))
                    .<CommentViewBinding>bind(0, R.layout.comment_view, (commentView, comments) -> {
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
                                clicks(commentView.reply).compose(always(user)))
                                .subscribe(intents.ReplyIntent::onNext);

                        //--Content--
                        comments.map(Comment::getContent)
                                .distinctUntilChanged()
                                .subscribe(text(commentView.content));

                        //TODO color whole view all nicely.
                        //--Sent--
                        comments
                                .map(Comment::getStatus)
                                .compose(ifPresent())
                                .map(sending -> getResources().getColor(sending ? R.color.disabled_text : R.color.red))
                                .doOnNext(color(commentView.author))
                                .subscribe(color(commentView.content));

                        //--Date--
                        comments.map(Comment::getDate)
                                .distinctUntilChanged()
                                .subscribe(text(commentView.date));
                    });

            //Notify the controller when a user refreshes or the activity first loads.
            intents.RefreshIntent = views
                    .switchMap(view -> refreshes(view.refreshLayout).map(ignored -> NONE))
                    .startWith(NONE);

            //Notify the controller when
            intents.RepliedIntent = views
                    .switchMap(view -> clicks(view.send)
                            .doOnNext(ignored -> view.reply.getText().clear())
                            .compose(always(view.reply.getText().toString())));

            views.subscribe(view -> {
                //Setup Recycler view and reply view.
                view.comments.setLayoutManager(new LinearLayoutManager(getActivity()));
                view.reply.requestFocus();
                adapter.getAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        boolean scroll = false;
                        for (int i = positionStart; i < (positionStart + itemCount) - 1; i++) {
                            if (!adapter.getList().get(i).getStatus().isPresent())
                                continue;
                            scroll = true;
                            break;
                        }
                        if (scroll)
                            view.comments.smoothScrollToPosition(adapter.getList().size() - 1);
                    }
                });

                //Stop refresh layout from refreshing when state isn't loading.
                states.map(State::loading).distinctUntilChanged().subscribe(refreshing(view.refreshLayout));

                //Append mention when a user clicks reply.
                states.map(State::replying)
                        .compose(ifPresent())
                        .map(User::getName)
                        .distinctUntilChanged()
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