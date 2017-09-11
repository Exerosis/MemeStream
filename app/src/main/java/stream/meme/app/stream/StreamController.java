package stream.meme.app.stream;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.google.common.collect.Lists;
import com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxProgressBar;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import stream.meme.app.ItemOffsetDecoration;
import stream.meme.app.MemeStream;
import stream.meme.app.PaginationListener;
import stream.meme.app.R;
import stream.meme.app.bisp.BISPDatabindingController;
import stream.meme.app.databinding.MemeViewBinding;
import stream.meme.app.databinding.StreamViewBinding;
import stream.meme.app.rxadapter.RxAdapter;
import stream.meme.app.rxadapter.RxListCallback;

import static com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout.refreshes;
import static com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout.refreshing;
import static com.jakewharton.rxbinding2.view.RxView.visibility;

public class StreamController extends BISPDatabindingController<Intents, StreamViewBinding, State> {
    private MemeStream memeStream;
    private Intents intents;
    private int page = 1;

    public StreamController() {
        super(R.layout.stream_view);
    }

    @Override
    protected void onContextAvailable(@NonNull Context context) {
        memeStream = (MemeStream) getApplicationContext();
    }

    @Override
    public BiConsumer<StreamViewBinding, Observable<State>> getStateToViewBinder() {
        return (StreamViewBinding view, Observable<State> state) -> {
            state = state.observeOn(AndroidSchedulers.mainThread());
            RxAdapter.on(view.recyclerView, new RxListCallback<>(state.map(State::memes))).bind(R.layout.meme_view, (Meme meme, MemeViewBinding memeView) -> {
                Picasso.with(getActivity()).load(meme.getImage()).into(memeView.image);
                memeView.title.setText(meme.getTitle());
                memeView.subtitle.setText(meme.getSubtitle());
                memeView.like.setOnClickListener(v -> {
                    getIntents().LikeClickIntent.onNext(meme);
                });
                memeView.share.setOnClickListener(v -> {
                    getIntents().ShareClickIntent.onNext(meme);
                });
                memeView.getRoot().setOnClickListener(v -> {
                    getIntents().MemeClickIntent.onNext(meme);
                });
            }).footer(R.layout.stream_footer).showFooter(state.map(State::nextPageLoading));

            view.recyclerView.addItemDecoration(new ItemOffsetDecoration(getActivity(), R.dimen.item_offset));
            view.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            PaginationListener.on(view.recyclerView, state.map(State::nextPageLoading)).subscribe(t -> getIntents().LoadNextIntent.onNext(t));

            state.map(State::refreshing).subscribe(refreshing(view.refreshLayout));
            state.map(State::firstPageLoading).subscribe(visibility(view.progressBar));
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
        return intents -> {
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
        };
    }


    @Override
    public Intents getIntents() {

        Intents intents = null;
        Observable<StreamViewBinding> views = null;
        Observable<Object> mockIntent = views.switchMap(view -> RxView.clicks(currentView.refreshLayout));


        Observable<State> state = pretendIntent.flatMap(ignored -> memeStream.loadMemes(0)
                .map(Partial::Refreshed)
                .startWith(Partial.Refreshing())
                .onErrorReturn(Partial::RefreshError))
                .scan(new State(), (s, partial) -> partial.apply(s));

        view.map(currentView -> {

        })


        state.flatMap(currentState -> view.map(currentView -> {

            return currentView.progressBar.setVisibility();
        }));
        theView.subscribe(view -> {

        });


        Consumer<Integer> test = viewStateStateBinder.bind(view -> view::setVisibility);
        if (intents == null)
            intents = new Intents();
        return intents;
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
    //TODO I'm fairly sure all of these are signs of issues x)
    Subject<Boolean> LoadNextIntent = PublishSubject.create();
    Subject<Meme> MemeClickIntent = PublishSubject.create();
    Subject<Meme> LikeClickIntent = PublishSubject.create();
    Subject<Meme> ShareClickIntent = PublishSubject.create();
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
