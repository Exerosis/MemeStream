package stream.meme.app.controller.alpha;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import stream.meme.app.R;
import stream.meme.app.databinding.ListViewBinding;
import stream.meme.app.util.ItemOffsetDecoration;
import stream.meme.app.util.Nothing;
import stream.meme.app.util.bivsc.Reducer;
import stream.meme.app.util.components.adapters.ListAdapter;
import stream.meme.app.util.components.adapters.Pagination;
import stream.meme.app.util.components.components.StatefulViewComponent;
import stream.meme.app.util.components.components.ViewComponent;

import static com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout.refreshes;
import static com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout.refreshing;
import static com.jakewharton.rxbinding2.view.RxView.visibility;
import static io.reactivex.Completable.create;
import static stream.meme.app.R.layout.list_view;
import static stream.meme.app.R.layout.loading_view;
import static stream.meme.app.util.Operators.ignored;

public class ListView extends ViewComponent<ListViewBinding> {
    private final Observable<Nothing> refresh;

    public ListView(@NonNull Context context) {
        super(context, list_view);

        //Notify watching components when the view needs to be refreshed.
        refresh = getViews().switchMap(view -> refreshes(view.refreshLayout).compose(ignored()));
    }

    public <Data> AdapterAttachment<Data> attach(StatefulViewComponent<? extends State<Data>, ?> parent) {
        return attach(parent.getStates(), partial -> parent.applyPartial((Function) partial));
    }

    public <Data> AdapterAttachment<Data> attach(Observable<? extends State<Data>> state, Consumer<Function<State<Data>, State<Data>>> partials) {
        return new AdapterAttachment<Data>() {

            @Override
            public AdapterAttachment adapter(ListAdapter<Data> adapter) {
                getViews().subscribe(view -> {
                    view.recyclerView.setAdapter(adapter);
                    view.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    view.recyclerView.addItemDecoration(new ItemOffsetDecoration(getContext(), R.dimen.item_offset));

                    //Show a progress bar when the view is loading.
                    state.map(State::loading)
                            .distinctUntilChanged()
                            .subscribe(visibility(view.progressBar, INVISIBLE));

                    //Show a refreshing circle when the view is refreshing.
                    state.map(State::refreshing)
                            .distinctUntilChanged()
                            .subscribe(refreshing(view.refreshLayout));
                });
                return this;
            }

            @Override
            public PaginationAttachment paginate(ListAdapter<Data> adapter) {
                adapter(adapter);
                return new PaginationAttachment<Data>() {
                    private boolean vertical = true;
                    private boolean reversed = false;

                    @Override
                    public Pagination loading(Single<Function<State<Data>, State<Data>>> loader) {
                        Pagination pagination = new Pagination(getContext(), create(observer -> {
                            partials.accept(Partials.LoadMore());
                            partials.accept(loader.blockingGet());
                        }), vertical, reversed);
                        adapter.addBind(position -> position == adapter.size() - 1 && pagination.isLoading(), loading_view);
                        pagination.onLoad().subscribe(loading -> adapter.reinjectBindings());

                        getViews().subscribe(view -> view.recyclerView.setLayoutManager(pagination));
                        return pagination;
                    }

                    @Override
                    public PaginationAttachment horizontally() {
                        vertical = false;
                        return this;

                    }

                    @Override
                    public PaginationAttachment reversed() {
                        reversed = true;
                        return this;
                    }
                };
            }
        };
    }

    public Observable<Nothing> onRefresh() {
        return refresh;
    }

    public static class State<Data> {
        boolean refreshing = false;
        boolean loading = true;
        boolean loadingMore = false;
        Throwable error = null;
        List<Data> data = new ArrayList<>();

        public boolean refreshing() {
            return refreshing;
        }

        public boolean loadingMore() {
            return loadingMore;
        }

        public boolean loading() {
            return loading;
        }

        public Optional<Throwable> error() {
            return Optional.fromNullable(error);
        }

        public List<Data> data() {
            return data;
        }
    }

    public interface Partials {
        static <State extends ListView.State> Reducer<State> Error(Throwable error) {
            return state -> {
                state.error = error;
                state.loading = false;
                state.refreshing = false;
                state.loadingMore = false;
                return state;
            };
        }

        static <State extends ListView.State> Reducer<State> LoadMore() {
            return state -> {
                state.loadingMore = true;
                return state;
            };
        }

        static <State extends ListView.State> Reducer<State> Refreshing() {
            return state -> {
                if (!state.loading)
                    state.refreshing = true;
                return state;
            };
        }

        static <Data, State extends ListView.State<Data>> Reducer<State> Loaded(List<Data> data) {
            return state -> {
                state.data.removeAll(data);
                state.data.addAll(data);
                state.loading = false;
                state.refreshing = false;
                state.loadingMore = false;
                return state;
            };
        }
    }

    public interface PaginationAttachment<Data> {
        default Pagination loading(Observable<Function<State<Data>, State<Data>>> loader) {
            return loading(loader.single(Partials.Error(new IllegalStateException("A new state was not returned!"))));
        }

        Pagination loading(Single<Function<State<Data>, State<Data>>> loader);

        PaginationAttachment horizontally();

        PaginationAttachment reversed();
    }

    public interface Attachment<Data> {

    }

    public interface AdapterAttachment<Data> {
        AdapterAttachment adapter(ListAdapter<Data> adapter);

        PaginationAttachment paginate(ListAdapter<Data> adapter);
    }
}