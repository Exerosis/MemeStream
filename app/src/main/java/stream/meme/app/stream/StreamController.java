package stream.meme.app.stream;

import android.support.v7.util.SortedList;

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

import static stream.meme.app.stream.State.Load.*;

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
            list
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

    private State reduceState(State state, Union3<State.Load.First, State.Load.Next, State.Load.Refresh> loadState) throws Exception {
        state.error = loadState.join(First::error, Next::error, Refresh::error);
        state.memes = loadState.join()

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

    static class Load {
        final boolean loading;
        final List<Meme> memes;
        final Throwable error;

        private Load(List<Meme> memes) {
            this.loading = false;
            this.memes = memes;
            this.error = null;
        }

        private Load() {
            this.loading = true;
            this.memes = new ArrayList<>();
            this.error = null;
        }

        private Load(Throwable error) {
            this.loading = false;
            this.memes = new ArrayList<>();
            this.error = error;
        }

        public List<Meme> memes() {
            return memes;
        }

        public Throwable error() {
            return error;
        }

        public boolean loading() {
            return loading;
        }

        static class Refresh extends Load {

        }

        static class First extends Load {

        }

        static class Next extends Load {

        }
    }

}


