package stream.meme.app.util.viewcomp.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import io.reactivex.Observable;
import io.reactivex.functions.unsafe.Action;
import io.reactivex.subjects.BehaviorSubject;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.computation;
import static stream.meme.app.R.layout.loading_view;

/**
 * Created by Exerosis on 1/27/2018.
 */

public class Pagination extends LinearLayoutManager {
    private BehaviorSubject<Boolean> loading = BehaviorSubject.createDefault(false);
    private final Action loader;


    public static PaginationBuilder paginate(Context context) {
        return paginate(context, null);
    }

    public static PaginationBuilder paginate(Context context, ListAdapter<?> adapter) {
        return new PaginationBuilder() {
            private boolean vertical = true;
            private boolean reversed = false;

            @Override
            public Pagination loading(Action loader) {
                Pagination pagination = new Pagination(context, loader, vertical, reversed);
                if (adapter != null) {
                    pagination.loading.observeOn(mainThread()).subscribe(loading -> adapter.reinjectBindings());
                    adapter.addBind(position -> pagination.loading.getValue() && position == adapter.size() - 1, loading_view);
                }
                return pagination;
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
        Pagination loading(Action action);

        PaginationBuilder horizontally();

        PaginationBuilder reversed();
    }


    public Pagination(Context context, Action loader, boolean vertical, boolean reversed) {
        super(context, vertical ? VERTICAL : HORIZONTAL, reversed);
        this.loader = loader;
    }


    public Observable<Boolean> isLoading() {
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
        computation().createWorker().schedule(() -> {
            loading.onNext(true);
            loader.runUnsafe();
            loading.onNext(false);
        });
        return this;
    }

    private void checkPagination() {
        int firstItem = findFirstVisibleItemPosition();

        if (!loading.getValue() && firstItem >= 0 && getChildCount() + firstItem >= getItemCount())
            load();
    }
}