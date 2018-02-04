package stream.meme.app.util.components.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.subjects.BehaviorSubject;

import static io.reactivex.Completable.fromAction;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

/**
 * Created by Exerosis on 1/27/2018.
 */

public class Pagination extends LinearLayoutManager {
    private final Completable loader;
    private BehaviorSubject<Boolean> loading = BehaviorSubject.createDefault(false);

    public static PaginationBuilder paginate(Context context) {
        return new PaginationBuilder() {
            private boolean vertical = true;
            private boolean reversed = false;

            @Override
            public Pagination loading(Completable loader) {
                return new Pagination(context, loader, vertical, reversed);
            }

            @Override
            public PaginationBuilder horizontally() {
                vertical = false;
                return this;
            }

            @Override
            public PaginationBuilder reversed() {
                reversed = true;
                return this;
            }
        };

    }

    public interface PaginationBuilder {
        default Pagination loading(Action action) {
            return loading(fromAction(action));
        }

        Pagination loading(Completable loader);

        PaginationBuilder horizontally();

        PaginationBuilder reversed();
    }

    public Pagination(Context context, Completable loader, boolean vertical, boolean reversed) {
        super(context, vertical ? VERTICAL : HORIZONTAL, reversed);
        this.loader = loader.observeOn(mainThread());
    }

    public boolean isLoading() {
        return loading.getValue();
    }

    public Observable<Boolean> onLoad() {
        return loading;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int scroll = super.scrollHorizontallyBy(dx, recycler, state);
        checkPagination();
        return scroll;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int scroll = super.scrollVerticallyBy(dy, recycler, state);
        checkPagination();
        return scroll;
    }

    public Pagination load() {
        loading.onNext(true);
        loader.subscribe(() -> loading.onNext(false));
        return this;
    }

    private void checkPagination() {
        int firstItem = findFirstVisibleItemPosition();

        if (!loading.getValue() && firstItem >= 0 && getChildCount() + firstItem >= getItemCount())
            load();
    }
}