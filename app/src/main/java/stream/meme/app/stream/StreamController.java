package stream.meme.app.stream;

import android.support.v7.util.SortedList;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.pacoworks.rxsealedunions2.Union2;
import com.pacoworks.rxsealedunions2.Union3;
import com.pacoworks.rxsealedunions2.generic.UnionFactories;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;
import stream.meme.app.MemeStream;
import stream.meme.app.R;
import stream.meme.app.bisp.BISPDatabindingController;
import stream.meme.app.databinding.StreamViewBinding;

public class StreamController extends BISPDatabindingController<Intents, StreamViewBinding, State> {
    private final MemeStream memeStream;
    private final Union2.Factory<State.LoadNext, State.Refresh> stateFactory = UnionFactories.doubletFactory();

    public StreamController() {
        super(R.layout.stream_view);
        memeStream = (MemeStream) getActivity().getApplicationContext();
    }

    @Override
    public BiConsumer<StreamViewBinding, Observable<State>> getStateToViewBinder() {
        return (view, state) -> {

        };
    }

    @Override
    public BiConsumer<Intents, Binder<StreamViewBinding>> getViewToIntentBinder() {
        return (intents, view) -> {
            SortedList list = null;
            list.end
        };
    }

    //TODO
    @Override
    public Function<Intents, Observable<State>> getIntentToStateBinder() {
        return intents -> intents.RefreshIntent
                .flatMap(ignored -> memeStream.loadMemes(0))
                .<State.Refresh>map(State.Refresh.Loaded::new)
                .startWith(new State.Refresh.Loading())
                .onErrorReturn(error -> new State.Refresh.Error(error)).
                .map(stateFactory::second)
                .scan(new State(), this::reduceState);
    }

    @Override
    public Intents getIntents() {
        return new Intents();
    }

    private State reduceState(State state, Union3<State.LoadNext, State.Refresh, State.LoadFirst> partialState) throws Exception {
       state.error = partialState.join(loadNext -> loadNext.error, refresh -> refresh.error, loadFirst -> loadFirst.error);

        partialState.continued(loadNext -> {
            state.loadNextError = loadNext.error;
            state.loadNextLoading = loadNext.loading;
            for (Meme meme : loadNext.memes)
                state.memes.addLast(meme);
            state.refreshError = null;
        }, refresh -> {
            state.refreshError = refresh.error;
            state.refreshLoading = refresh.loading;
            for (Meme meme : Lists.reverse(refresh.memes))
                if (!state.memes.contains(meme))
                    state.memes.addFirst(meme);
        }, loadFirst -> {
            state.loadNextError = loadFirst.error;
            state.loadNextLoading = loadFirst.loading;
            state.memes.addAll(loadFirst.memes);
        });


        return state;
        state.error = null;
    }
}

class Intents {
    Observable<Void> RefreshIntent;
    Observable<Void> LoadNextIntent;
    Observable<Void> LoadFirstIntent;
}

class State {
    boolean refreshLoading = false;
    boolean loadNextLoading = false;
    boolean loadFirstLoading = false;
    Throwable error = new Throwable();
    Deque<Meme> memes = new ArrayDeque<>();

    static class LoadFirst {
        final boolean loading;
        final List<Meme> memes;
        final Throwable error;

        private LoadFirst(boolean loading, List<Meme> memes, Throwable error) {
            this.loading = loading;
            this.memes = memes;
            this.error = error;
        }

        static class Error extends LoadFirst {

            Error(Throwable error) {
                super(false, new ArrayList<>(), error);
            }
        }

        static class Loaded extends LoadFirst {

            Loaded(List<Meme> memes) {
                super(false, memes, null);
            }
        }

        static class Loading extends LoadFirst {
            Loading() {
                super(false, new ArrayList<>(), null);
            }
        }
    }

    static class LoadNext {
        final boolean loading;
        final List<Meme> memes;
        final Throwable error;

        private LoadNext(boolean loading, List<Meme> memes, Throwable error) {
            this.loading = loading;
            this.memes = memes;
            this.error = error;
        }

        static class Error extends LoadNext {

            Error(Throwable error) {
                super(false, new ArrayList<>(), error);
            }
        }

        static class Loaded extends LoadNext {

            Loaded(List<Meme> memes) {
                super(false, memes, null);
            }
        }

        static class Loading extends LoadNext {
            Loading() {
                super(false, new ArrayList<>(), null);
            }
        }
    }

    static class Refresh {
        final boolean loading;
        final List<Meme> memes;
        final Throwable error;

        private Refresh(boolean loading, List<Meme> memes, Throwable error) {
            this.loading = loading;
            this.memes = memes;
            this.error = error;
        }

        static class Error extends Refresh {

            Error(Throwable error) {
                super(false, new ArrayList<>(), error);
            }
        }

        static class Loaded extends Refresh {

            Loaded(List<Meme> memes) {
                super(false, memes, null);
            }
        }

        static class Loading extends Refresh {
            Loading() {
                super(false, new ArrayList<>(), null);
            }
        }
    }

}

