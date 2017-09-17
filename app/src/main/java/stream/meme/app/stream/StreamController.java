package stream.meme.app.stream;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.common.collect.Lists;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import stream.meme.app.ItemOffsetDecoration;
import stream.meme.app.PaginationListener;
import stream.meme.app.R;
import stream.meme.app.application.MemeStream;
import stream.meme.app.bisp.DatabindingBIVSCModule;
import stream.meme.app.databinding.MemeViewBinding;
import stream.meme.app.databinding.StreamViewBinding;

import static com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout.refreshes;
import static com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout.refreshing;
import static com.jakewharton.rxbinding2.view.RxView.visibility;

public class StreamController extends DatabindingBIVSCModule<StreamViewBinding, State> {
    private List<Meme> memes = new ArrayList<>();
    private boolean footerShown = false;
    private final Intents intents = new Intents();
    private MemeStream memeStream;
    private int page = 1;

    public StreamController() {
        super(R.layout.stream_view);
    }

    @Override
    public BiConsumer<Observable<StreamViewBinding>, Observable<State>> getBinder() {
        return (views, state) -> {
            intents.RefreshIntent = views.switchMap(view -> refreshes(view.refreshLayout));

   /*         new RxAdapter<>(views.map(view -> view.recyclerView), new RxListCallback<>(state.map(State::memes))).bind(R.layout.meme_view, (Meme meme, MemeViewBinding memeView) -> {
                Picasso.with(getActivity()).load(meme.getImage()).into(memeView.image);
                memeView.title.setText(meme.getTitle());
                memeView.subtitle.setText(meme.getSubtitle());

                memeView.like.setOnClickListener(v -> intents.LikeClickIntent.onNext(meme));
                memeView.share.setOnClickListener(v -> intents.ShareClickIntent.onNext(meme));
                memeView.getRoot().setOnClickListener(v -> intents.MemeClickIntent.onNext(meme));
            }).footer(R.layout.stream_footer).showFooter(state.map(State::nextPageLoading).distinctUntilChanged());*/

            views.subscribe(view -> {
                RecyclerView.Adapter adapter = new RecyclerView.Adapter<ViewHolder<MemeViewBinding>>() {
                    @Override
                    public ViewHolder<MemeViewBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
                        if (viewType != 1)
                            return new ViewHolder<>(DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.meme_view, parent, false));
                        return new ViewHolder<>(DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.stream_footer, parent, false));
                    }

                    @Override
                    public void onBindViewHolder(ViewHolder<MemeViewBinding> holder, int position) {
                        if (position == memes.size())
                            return;
                        Meme meme = memes.get(position);
                        MemeViewBinding binding = holder.getBinding();
                        Picasso.with(getActivity()).load(meme.getImage()).into(binding.image);
                        binding.title.setText(meme.getTitle());
                        binding.subtitle.setText(meme.getSubtitle());
                    }

                    @Override
                    public int getItemCount() {
                        return memes.size() + (footerShown ? 1 : 0);
                    }
                };
                state.map(State::memes).map(newMemes -> {
                    DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                        @Override
                        public int getOldListSize() {
                            return memes.size();
                        }

                        @Override
                        public int getNewListSize() {
                            return newMemes.size();
                        }

                        @Override
                        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                            return memes.get(oldItemPosition).equals(newMemes.get(newItemPosition));
                        }

                        @Override
                        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                            return memes.get(oldItemPosition).equals(newMemes.get(newItemPosition));
                        }
                    });
                    memes = newMemes;
                    return result;
                }).subscribe(result -> result.dispatchUpdatesTo(adapter));
                state.map(State::nextPageLoading).distinctUntilChanged().subscribe(loading -> {
                    footerShown = loading;
                    if (loading)
                        adapter.notifyItemInserted(memes.size());
                    else
                        adapter.notifyItemRemoved(memes.size());
                });
                view.recyclerView.setAdapter(adapter);
                view.recyclerView.addItemDecoration(new ItemOffsetDecoration(getActivity(), R.dimen.item_offset));
                view.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                PaginationListener.on(view.recyclerView, state.map(State::nextPageLoading).distinctUntilChanged()).subscribe(intents.LoadNextIntent);
                state.map(State::nextPageLoading).distinctUntilChanged().subscribe(visibility(view.progressBar));
                state.map(State::refreshing).distinctUntilChanged().subscribe(refreshing(view.refreshLayout));
            });
        };
    }

    @Override
    public Observable<State> getController() {
        return Observable.merge(
                intents.LoadNextIntent
                        .flatMap(ignored -> memeStream.loadMemes(page++)
                                .map(Partial::NextPageLoaded)
                                .startWith(Partial.NextPageLoading())
                                .onErrorReturn(Partial::NextPageError)),
                intents.LoadFirstIntent
                        .flatMap(ignored -> memeStream.loadMemes(0)
                                .map(Partial::FirstPageLoaded)
                                .startWith(Partial.FirstPageLoading())
                                .onErrorReturn(Partial::FirstPageError)),
                intents.RefreshIntent
                        .flatMap(ignored -> memeStream.loadMemes(0)
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
}


class Partial {
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

    static Function<State, State> NextPageLoaded(List<Meme> memes) {
        return state -> {
            state.nextPageLoading = false;
            for (Meme meme : memes)
                state.memes.addLast(meme);
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

    static Function<State, State> FirstPageLoaded(List<Meme> memes) {
        return state -> {
            state.firstPageLoading = false;
            state.memes.addAll(memes);
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

    static Function<State, State> Refreshed(List<Meme> memes) {
        return state -> {
            state.refreshing = false;
            for (Meme meme : Lists.reverse(memes))
                if (!state.memes.contains(meme))
                    state.memes.addFirst(meme);
            return state;
        };
    }
}

class Intents {
    Observable<Object> RefreshIntent;
    Observable<Boolean> LoadFirstIntent = Observable.just(true);
    Subject<Boolean> LoadNextIntent = PublishSubject.create();
    Subject<Meme> MemeClickIntent = PublishSubject.create();
    Subject<Meme> LikeClickIntent = PublishSubject.create();
    Subject<Meme> ShareClickIntent = PublishSubject.create();
}

class State {
    public boolean refreshing = false;
    boolean nextPageLoading = false;
    boolean firstPageLoading = false;
    Throwable error = null;
    LinkedList<Meme> memes = new LinkedList<>();

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

    public LinkedList<Meme> memes() {
        return memes;
    }
}

class ViewHolder<Binding extends ViewDataBinding> extends RecyclerView.ViewHolder {
    private Binding binding;

    public ViewHolder(Binding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public Binding getBinding() {
        return binding;
    }
}
