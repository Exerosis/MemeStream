package stream.meme.app.controller.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

import com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import stream.meme.app.R;
import stream.meme.app.application.Post;
import stream.meme.app.util.ItemOffsetDecoration;
import stream.meme.app.util.Nothing;
import stream.meme.app.util.views.RxListView;

import static stream.meme.app.util.Operators.ignored;
import static stream.meme.app.util.rxadapter.RxPagination.on;

/**
 * Created by Exerosis on 11/22/2017.
 */
public class PostListView extends SwipeRefreshLayout {
    private final RxListView<Post, PostView> postsView;
    private final Observable<PostView> views;
    private final Subject<Boolean> loading = PublishSubject.create();
    private final Observable<Nothing> loadsMore;

    //Fix RxListView so that it can support loading view.
    public PostListView(@NonNull Context context, @Nullable AttributeSet attributes) {
        super(context, attributes);
        postsView = new RxListView<>(context, attributes);
        postsView.layoutManager(new LinearLayoutManager(context));
        postsView.addDecoration(new ItemOffsetDecoration(context, R.dimen.item_offset));
        loadsMore = on(postsView, loading);
        views = Observable.<PostView>create(subscriber -> postsView.bind(() -> new PostView(context), (view, posts) -> {
            subscriber.onNext(view);
            view.post(posts);
        })).replay(1);
        addView(postsView);
    }

    public Observable<Nothing> loadsMore() {
        return loadsMore;
    }

    public Observable<Pair<Post, Boolean>> rated() {
        return views.flatMap(PostView::rated);
    }

    public Observable<Post> shared() {
        return views.flatMap(PostView::shared);
    }

    public Observable<Post> reply() {
        return views.flatMap(PostView::reply);
    }

    public Observable<Post> clicked() {
        return views.flatMap(PostView::clicked);
    }

    public Observable<Nothing> refreshes() {
        return RxSwipeRefreshLayout.refreshes(this).compose(ignored());
    }

    public PostListView loading(Observable<Boolean> loading) {
        loading.distinctUntilChanged().subscribe(this.loading);
        return this;
    }

    public PostListView refreshing(Observable<Boolean> refreshing) {
        refreshing.subscribe(RxSwipeRefreshLayout.refreshing(this));
        return this;
    }

    public PostListView posts(Observable<List<Post>> posts) {
        this.postsView.data(posts);
        return this;
    }

    public PostListView posts(List<Post> posts) {
        this.postsView.data(posts);
        return this;
    }
}