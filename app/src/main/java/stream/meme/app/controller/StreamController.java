package stream.meme.app.controller;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.google.common.collect.Lists;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import stream.meme.app.R;
import stream.meme.app.application.MemeStream;
import stream.meme.app.application.Post;
import stream.meme.app.controller.view.PostListView;
import stream.meme.app.databinding.StreamViewBinding;
import stream.meme.app.util.ControllerActivity;
import stream.meme.app.util.Nothing;
import stream.meme.app.util.bivsc.DatabindingBIVSCModule;
import stream.meme.app.util.bivsc.Reducer;

import static stream.meme.app.controller.CommentsController.EXTRA_POST;
import static stream.meme.app.util.ControllerActivity.EXTRA_CONTROLLER;
import static stream.meme.app.util.bivsc.Reducer.controller;

public class StreamController extends DatabindingBIVSCModule<StreamViewBinding, StreamController.State> {
    private final Intents intents = new Intents();
    private MemeStream memeStream;
    private UUID last;

    public StreamController() {
        super(R.layout.stream_view);
    }

    @Override
    protected void onContextAvailable(@NonNull Context context) {
        memeStream = (MemeStream) getApplicationContext();
        super.onContextAvailable(context);
    }

    @Override
    public BiConsumer<Observable<StreamViewBinding>, Observable<State>> getBinder() {
        return (views, state) -> {
            Observable<PostListView> posts = views.map(view -> view.posts);

            //Handle post list.
            intents.RefreshIntent = posts.switchMap(PostListView::refreshes);
            intents.LoadMoreIntent = posts.switchMap(PostListView::loadsMore);

            //Handle merged posts
            intents.PostClickedIntent = posts.switchMap(PostListView::clicked);
            intents.PostSharedIntent = posts.switchMap(PostListView::shared);
            intents.PostRatedIntent = posts.switchMap(PostListView::rated);
            intents.PostReplyIntent = posts.switchMap(PostListView::reply);

            views.subscribe(view -> {
                //Show and hide first page, next page, and refreshing indicators.
                view.loadingLayout.loading(state.map(State::firstPageLoading));
                view.posts.loading(state.map(State::nextPageLoading));
                view.posts.refreshing(state.map(State::refreshing));

                //Display data on the list.
                view.posts.posts(state.map(State::memes));
            });
        };
    }

    @Override
    public Observable<State> getController() {
        intents.PostReplyIntent.subscribe(ignored -> {
            Intent intent = new Intent(getActivity(), ControllerActivity.class);
            intent.putExtra(EXTRA_CONTROLLER, CommentsController.class.getName());
            intent.putExtra(EXTRA_POST, UUID.randomUUID().toString());
            getActivity().startActivity(intent);
        });

        return controller(new State(),
                intents.RefreshIntent.flatMap(ignored -> memeStream.loadPosts(last)
                        .map(Partial::Refreshed)
                        .startWith(Partial.Refreshing())
                        .onErrorReturn(Partial::RefreshError)
                ),

                intents.LoadMoreIntent.flatMap(ignored -> memeStream.loadPosts(last)
                        .doOnNext(posts -> last = posts.get(posts.size() - 1).getId())
                        .map(Partial::NextPageLoaded)
                        .startWith(Partial.NextPageLoading())
                        .onErrorReturn(Partial::NextPageError)
                ),

                intents.LoadFirstIntent.flatMap(ignored -> memeStream.loadPosts(last)
                        .map(Partial::FirstPageLoaded)
                        .startWith(Partial.FirstPageLoading())
                        .onErrorReturn(Partial::FirstPageError)
                ),

                intents.PostRatedIntent.flatMap(rating -> memeStream.rate(rating.first, rating.second)
                        .map(Partial::Rated)
                )
        );
    }

    class Intents {
        Observable<Nothing> RefreshIntent;
        Observable<Nothing> LoadMoreIntent;
        Observable<Nothing> LoadFirstIntent;
        Observable<Post> PostClickedIntent;
        Observable<Post> PostSharedIntent;
        Observable<Post> PostReplyIntent;
        Observable<Pair<Post, Boolean>> PostRatedIntent;
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
        static Reducer<State> Rated(Post post) {
            return state -> {
                state.posts.remove(post);
                state.posts.add(post);
                return state;
            };
        }

        static Reducer<State> NextPageLoading() {
            return state -> {
                state.nextPageLoading = true;
                return state;
            };
        }

        static Reducer<State> NextPageError(Throwable error) {
            return state -> {
                state.nextPageLoading = false;
                state.error = error;
                return state;
            };
        }

        static Reducer<State> NextPageLoaded(List<Post> posts) {
            return state -> {
                state.nextPageLoading = false;
                for (Post post : posts)
                    state.posts.addLast(post);
                return state;
            };
        }

        static Reducer<State> FirstPageLoading() {
            return state -> {
                state.firstPageLoading = true;
                return state;
            };
        }

        static Reducer<State> FirstPageError(Throwable error) {
            return state -> {
                state.firstPageLoading = false;
                state.error = error;
                return state;
            };
        }

        static Reducer<State> FirstPageLoaded(List<Post> posts) {
            return state -> {
                state.firstPageLoading = false;
                state.posts.addAll(posts);
                return state;
            };
        }

        static Reducer<State> Refreshing() {
            return state -> {
                state.refreshing = true;
                return state;
            };
        }

        static Reducer<State> RefreshError(Throwable error) {
            return state -> {
                state.refreshing = false;
                state.error = error;
                return state;
            };
        }

        static Reducer<State> Refreshed(List<Post> posts) {
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