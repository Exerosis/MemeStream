package stream.meme.app;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class PaginationListener extends RecyclerView.OnScrollListener {
    private final Subject<Boolean> loadMore = PublishSubject.create();
    private final LinearLayoutManager manager;
    private boolean loading = false;

    public static Observable<Boolean> on(RecyclerView view, Observable<Boolean> loadingObservable) {
        PaginationListener scrollListener = new PaginationListener((LinearLayoutManager) view.getLayoutManager(), loadingObservable);
        view.addOnScrollListener(scrollListener);
        return scrollListener.loadMoreObservable();
    }

    public PaginationListener(LinearLayoutManager manager, Observable<Boolean> loadingObservable) {
        this.manager = manager;
        loadingObservable.subscribe(loading -> this.loading = loading);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        int firstItem = manager.findFirstVisibleItemPosition();

        if (!loading && (manager.getChildCount() + firstItem) >= manager.getItemCount() && firstItem >= 0)
            loadMore.onNext(true);
    }

    public Observable<Boolean> loadMoreObservable() {
        return loadMore;
    }
}