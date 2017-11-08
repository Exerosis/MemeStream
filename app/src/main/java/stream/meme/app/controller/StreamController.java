package stream.meme.app.controller;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableByte;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;

import com.google.common.collect.Lists;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import stream.meme.app.R;
import stream.meme.app.application.Comment;
import stream.meme.app.application.MemeStream;
import stream.meme.app.application.Post;
import stream.meme.app.databinding.CommentViewBinding;
import stream.meme.app.databinding.MemeViewBinding;
import stream.meme.app.databinding.StreamViewBinding;
import stream.meme.app.util.ItemOffsetDecoration;
import stream.meme.app.util.bivsc.DatabindingBIVSCModule;
import stream.meme.app.util.rxadapter.RxAdapterAlpha;
import stream.meme.app.util.rxadapter.RxFooter;
import stream.meme.app.util.rxadapter.RxListCallback;

import static android.support.v4.util.Pair.create;
import static com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout.refreshes;
import static com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout.refreshing;
import static com.jakewharton.rxbinding2.view.RxView.clicks;
import static com.jakewharton.rxbinding2.view.RxView.visibility;
import static com.jakewharton.rxbinding2.widget.RxTextView.text;
import static com.squareup.picasso.Picasso.with;
import static stream.meme.app.util.rxadapter.RxPagination.on;

public class StreamController extends DatabindingBIVSCModule<StreamViewBinding, StreamController.State> {
    private final Intents intents = new Intents();
    private MemeStream memeStream;
    private UUID last;

    public StreamController() {
        super(R.layout.stream_view);
    }

    @Override
    public BiConsumer<Observable<StreamViewBinding>, Observable<State>> getBinder() {
        return (views, state) -> {
            intents.RefreshIntent = views.switchMap(view -> refreshes(view.refreshLayout));

            new RxFooter(1, new RxAdapterAlpha<>(views.map(view -> view.recyclerView), new RxListCallback<>(state.map(State::memes)))
                    .<MemeViewBinding>bind(0, R.layout.meme_view, (memeView, memes) -> {
                        new RxAdapterAlpha<>(memeView.comments, new RxListCallback<>(memes.map(Post::getPreviewComments)))
                                .<CommentViewBinding>bind(R.layout.comment_view, (commentView, comments) -> {
                                    clicks(commentView.getRoot()).flatMap(ignored -> comments).subscribe(intents.ReplyIntent);
                                    comments.subscribe(comment -> {
                                        text(commentView.content).accept(comment.getContent());
                                        text(commentView.author).accept(comment.getAuthor().getName());
                                        text(commentView.date).accept(comment.getDate());
                                        commentView.image.setImageBitmap(comment.getAuthor().getImage());
                                    });
                                });
                        //Add a shown observable if there isn't already one.
                        if (memeView.getShown() == null)
                            memeView.setShown(new ObservableBoolean(false));
                        if (memeView.getRating() == null)
                            memeView.setRating(new ObservableByte((byte) 0));

                        //Bind expanding layout to toggle.
                        clicks(memeView.toggle).subscribe(ignored ->
                                memeView.expandableLayout.setExpanded(memeView.toggle.isChecked()));
                        memeView.comments.setLayoutManager(new LinearLayoutManager(getActivity()));

                        memes.subscribe(post -> {
                            //Add post information.
                            memeView.image.setImageBitmap(post.getThumbnail());
                            with(getActivity()).load(post.getImage()).into(memeView.image);
                            memeView.title.setText(post.getTitle());
                            memeView.subtitle.setText(post.getSubtitle());

                            //Setup ratings intents
                            clicks(memeView.like).map(ignored -> 1).mergeWith(clicks(memeView.dislike).map(ignored -> -1)).doOnNext(rating ->
                                    memeView.getRating().set(rating.byteValue())).subscribe(rating ->
                                    intents.RatedIntent.onNext(create(post, rating.byteValue())));

                            //Add view click listener.
                            memeView.getRoot().setOnClickListener(v -> intents.MemeClickIntent.onNext(post));
                        });
                    })
                    .bind(1, R.layout.stream_footer, (footerView, ignored) -> {
                    })).showFooter(state.map(State::nextPageLoading).distinctUntilChanged());

            views.subscribe(view -> {
                view.recyclerView.addItemDecoration(new ItemOffsetDecoration(getActivity(), R.dimen.item_offset));
                view.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                on(view.recyclerView, state.map(State::nextPageLoading).distinctUntilChanged()).subscribe(intents.LoadNextIntent);
                state.map(State::firstPageLoading).distinctUntilChanged().subscribe(visibility(view.progressBar));
                state.map(State::refreshing).distinctUntilChanged().subscribe(refreshing(view.refreshLayout));
            });
        };
    }

    @Override
    public Observable<State> getController() {
        return Observable.merge(
                intents.LoadNextIntent
                        .flatMap(ignored -> memeStream.loadPosts(last)
                                .doOnNext(posts -> last = posts.get(posts.size() - 1).getId())
                                .map(Partial::NextPageLoaded)
                                .startWith(Partial.NextPageLoading())
                                .onErrorReturn(Partial::NextPageError)),
                intents.LoadFirstIntent
                        .flatMap(ignored -> memeStream.loadPosts(last)
                                .map(Partial::FirstPageLoaded)
                                .startWith(Partial.FirstPageLoading())
                                .onErrorReturn(Partial::FirstPageError)),
                intents.RefreshIntent
                        .flatMap(ignored -> memeStream.loadPosts(last)
                                .map(Partial::Refreshed)
                                .startWith(Partial.Refreshing())
                                .onErrorReturn(Partial::RefreshError)))
                .scan(new State(), (state, partial) -> partial.apply(state));
    }

    @Override
    protected void onContextAvailable(@NonNull Context context) {
        memeStream = (MemeStream) getApplicationContext();
        super.onContextAvailable(context);
    }

    class Intents {
        Observable<Object> RefreshIntent;
        Observable<Boolean> LoadFirstIntent = Observable.just(true);
        Subject<Boolean> LoadNextIntent = PublishSubject.create();
        Subject<Post> MemeClickIntent = PublishSubject.create();
        Subject<Pair<Post, Byte>> RatedIntent = PublishSubject.create();
        Subject<Post> ShareClickIntent = PublishSubject.create();
        Subject<Comment> ReplyIntent = PublishSubject.create();
        Subject<Object> CommentsIntent = PublishSubject.create();
    }

    class State {
        boolean refreshing = false;
        boolean nextPageLoading = false;
        boolean firstPageLoading = false;
        Throwable error = null;
        LinkedList<Post> posts = new LinkedList<>();

        public boolean refreshing() {
            return refreshing;
        }

        public boolean nextPageLoading() {
            return nextPageLoading;
        }

        public boolean firstPageLoading() {
            return firstPageLoading;
        }

        public Throwable error() {
            return error;
        }

        public LinkedList<Post> memes() {
            return posts;
        }
    }

    interface Partial {
        static Function<State, State> NextPageLoading() {
            return state -> {
                state.nextPageLoading = true;
                return state;
            };
        }

        static Function<State, State> NextPageError(Throwable error) {
            return state -> {
                state.nextPageLoading = false;
                state.error = error;
                return state;
            };
        }

        static Function<State, State> NextPageLoaded(List<Post> posts) {
            return state -> {
                state.nextPageLoading = false;
                for (Post post : posts)
                    state.posts.addLast(post);
                return state;
            };
        }

        static Function<State, State> FirstPageLoading() {
            return state -> {
                state.firstPageLoading = true;
                return state;
            };
        }

        static Function<State, State> FirstPageError(Throwable error) {
            return state -> {
                state.firstPageLoading = false;
                state.error = error;
                return state;
            };
        }

        static Function<State, State> FirstPageLoaded(List<Post> posts) {
            return state -> {
                state.firstPageLoading = false;
                state.posts.addAll(posts);
                return state;
            };
        }

        static Function<State, State> Refreshing() {
            return state -> {
                state.refreshing = true;
                return state;
            };
        }

        static Function<State, State> RefreshError(Throwable error) {
            return state -> {
                state.refreshing = false;
                state.error = error;
                return state;
            };
        }

        static Function<State, State> Refreshed(List<Post> posts) {
            return state -> {
                state.refreshing = false;
                for (Post post : Lists.reverse(posts))
                    if (!state.posts.contains(post))
                        state.posts.addFirst(post);
                return state;
            };
        }
    }
}