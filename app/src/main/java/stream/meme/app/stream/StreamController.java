package stream.meme.app.stream;

import com.google.common.collect.Lists;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;
import stream.meme.app.ItemOffsetDecoration;
import stream.meme.app.MemeStream;
import stream.meme.app.R;
import stream.meme.app.RxAdapter;
import stream.meme.app.RxListCallback;
import stream.meme.app.bisp.BISPDatabindingController;
import stream.meme.app.databinding.MemeViewBinding;
import stream.meme.app.databinding.StreamViewBinding;

import static com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout.refreshes;
import static com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout.refreshing;
import static com.jakewharton.rxbinding2.view.RxView.visibility;

public class StreamController extends BISPDatabindingController<Intents, StreamViewBinding, State> {
    private final MemeStream memeStream;
    private int page = 1;

    public StreamController() {
        super(R.layout.stream_view);
        memeStream = (MemeStream) getApplicationContext();
    }

    @Override
    public BiConsumer<StreamViewBinding, Observable<State>> getStateToViewBinder() {
        return (view, viewState) -> {
            RxAdapter.on(view.recyclerView, new RxListCallback<>(viewState.map(State::memes))).bind(R.layout.meme_view, (Meme meme, MemeViewBinding memeView) -> {
                Picasso.with(getActivity()).load(meme.getImage()).into(memeView.image);
                memeView.title.setText(meme.getTitle());
                memeView.subtitle.setText(meme.getSubtitle());
            });
            view.recyclerView.addItemDecoration(new ItemOffsetDecoration(getActivity(), R.dimen.item_offset));
            viewState.map(State::refreshing).subscribe(refreshing(view.refreshLayout));
            viewState.map(State::firstPageLoading).subscribe(visibility(view.progressBar));

        };
    }

    @Override
    public BiConsumer<Intents, Binder<StreamViewBinding>> getViewToIntentBinder() {
        return (intents, binder) -> {
            intents.RefreshIntent = binder.bind(view -> refreshes(view.refreshLayout));
        };
    }

    @Override
    public Function<Intents, Observable<State>> getIntentToStateBinder() {
        return intents -> Observable.merge(
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
    public Intents getIntents() {
        return new Intents();
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
    Observable<Void> LoadNextIntent;
    Observable<Void> LoadFirstIntent;
}

class State {
    public boolean refreshing = false;
    boolean nextPageLoading = false;
    boolean firstPageLoading = false;
    Throwable error = new Throwable();
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
