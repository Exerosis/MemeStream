package stream.meme.app.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import stream.meme.app.application.Comment;
import stream.meme.app.application.MemeStream;
import stream.meme.app.application.User;
import stream.meme.app.databinding.CommentViewBinding;
import stream.meme.app.databinding.CommentsBinding;
import stream.meme.app.util.Nothing;
import stream.meme.app.util.bivsc.DatabindingBIVSCModule;
import stream.meme.app.util.bivsc.Reducer;
import stream.meme.app.util.rxadapter.RxAdapterAlpha;
import stream.meme.app.util.rxadapter.RxListCallback;

import static com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout.refreshes;
import static com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout.refreshing;
import static com.jakewharton.rxbinding2.view.RxView.clicks;
import static com.jakewharton.rxbinding2.view.RxView.visibility;
import static com.jakewharton.rxbinding2.widget.RxTextView.text;
import static stream.meme.app.R.color.disabled_text;
import static stream.meme.app.R.color.red;
import static stream.meme.app.R.layout.comment_view;
import static stream.meme.app.R.layout.comments;
import static stream.meme.app.util.Nothing.NONE;
import static stream.meme.app.util.Operators.always;
import static stream.meme.app.util.Operators.ifPresent;

/**
 * Created by Exerosis on 11/10/2017.
 */

public class Comments extends DatabindingBIVSCModule<CommentsBinding, Comments.State> {
    private final Intents intents = new Intents();
    private final UUID post;
    private final boolean nested;
    private MemeStream memeStream;

    public Comments() {
        this(null, false);
    }

    public Comments(UUID post, boolean nested) {
        super(comments);
        this.post = post;
        this.nested = nested;
    }

    @Override
    protected void onContextAvailable(@NonNull Context context) {
        memeStream = (MemeStream) getApplicationContext();
        super.onContextAvailable(context);
    }

    public Observable<Comment> onReply() {
        return intents.ReplyIntent;
    }

    public void addComments(List<Comment> comments) {
        intents.AddIntent.onNext(comments);
    }

    static class Intents {
        Subject<Comment> ReplyIntent = PublishSubject.create();
        Observable<Nothing> RefreshIntent;
        Subject<List<Comment>> AddIntent = PublishSubject.create();
    }


    class State {
        List<Comment> comments = new ArrayList<>();
        boolean loading = true;
        boolean refreshing = false;
        Throwable error = null;

        List<Comment> comments() {
            return comments;
        }

        public boolean loading() {
            return loading;
        }

        public boolean refreshing() {
            return refreshing;
        }

        public Throwable error() {
            return error;
        }
    }


    interface Partials {
        static Reducer<State> Error(Throwable error) {
            return state -> {
                state.error = error;
                return state;
            };
        }

        static Reducer<State> Refreshing() {
            return state -> {
                if (!state.loading)
                    state.refreshing = true;
                return state;
            };
        }

        static Reducer<State> Refreshed(List<Comment> comments) {
            return state -> {
                for (Comment comment : comments)
                    if (state.comments.contains(comment))
                        state.comments.remove(comment);
                state.comments.addAll(comments);
                state.loading = false;
                state.refreshing = false;
                return state;
            };
        }
    }

    @Override
    public BiConsumer<Observable<CommentsBinding>, Observable<State>> getBinder() {
        return (views, states) -> {
            RxAdapterAlpha<Comment> adapter = new RxAdapterAlpha<>(views.map(view -> view.comments), new RxListCallback<>(states.map(State::comments),
                    (first, second) -> first.equals(second) && first.getStatus().equals(second.getStatus()), Object::equals))
                    .<CommentViewBinding>bind(comment_view, (commentView, comments) -> {
                        Observable<User> author = comments
                                .map(Comment::getAuthor)
                                .distinctUntilChanged();

                        //--Author--
                        author.subscribe(user -> {
                            commentView.author.setText(user.getName());
                            commentView.image.setImageBitmap(user.getImage());
                        });

                        //--Reply--
                        comments.switchMap(comment ->
                                clicks(commentView.reply).compose(always(comment)))
                                .subscribe(intents.ReplyIntent::onNext);

                        //--Content--
                        comments.map(Comment::getContent)
                                .distinctUntilChanged()
                                .subscribe(text(commentView.content));

                        //--Sent--
                        comments
                                .map(Comment::getStatus)
                                .compose(ifPresent())
                                .subscribe(sending -> {
                                    if (sending) {
                                        commentView.image.setAlpha(0.5f);
                                        commentView.reply.setAlpha(0.5f);
                                        commentView.author.setAlpha(0.5f);
                                        commentView.date.setAlpha(0.5f);
                                        commentView.content.setAlpha(0.5f);
                                    }
                                    int color = getResources().getColor(sending ? disabled_text : red);
                                    commentView.author.setTextColor(color);
                                    commentView.date.setTextColor(color);
                                    commentView.content.setTextColor(color);
                                });

                        //--Date--
                        comments.map(Comment::getDate)
                                .distinctUntilChanged()
                                .subscribe(text(commentView.date));
                    });

            //Notify the controller when a user refreshes or the activity first loads.
            intents.RefreshIntent = views
                    .switchMap(view -> refreshes(view.refreshLayout).map(ignored -> NONE))
                    .startWith(NONE);

            views.subscribe(view -> {
                view.comments.setLayoutManager(new LinearLayoutManager(getActivity()));
                if (nested) {
                    view.comments.setNestedScrollingEnabled(false);
                    view.refreshLayout.setEnabled(false);
                    view.comments.getLayoutManager().setAutoMeasureEnabled(true);
                } else
                    adapter.getAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                        @Override
                        public void onItemRangeInserted(int positionStart, int itemCount) {
                            if (positionStart <= adapter.getList().size() - 1)
                                view.comments.smoothScrollToPosition(adapter.getList().size() - 1);
                        }
                    });

                states.map(State::refreshing)
                        .distinctUntilChanged()
                        .subscribe(refreshing(view.refreshLayout));

                states.map(State::loading)
                        .distinctUntilChanged()
                        .subscribe(visibility(view.progressBar));

                states.map(State::loading)
                        .map(showing -> !showing)
                        .distinctUntilChanged()
                        .subscribe(visibility(view.refreshLayout));
            });
        };
    }

    @Override
    public Observable<State> getController() {
        return Reducer.controller(new State(),
                //Get top page of comments when refreshed.
                intents.RefreshIntent.flatMap(ignored -> memeStream.getComments(post)
                        .map(Partials::Refreshed)
                        .onErrorReturn(Partials::Error)
                        .startWith(Partials.Refreshing())
                ),

                //Forward new comments from above
                intents.AddIntent.map(Partials::Refreshed)
        );
    }
}
