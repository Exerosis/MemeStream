package stream.meme.app.bisp.alpha;

import android.support.v4.util.Pair;

import java.util.function.Consumer;

import io.reactivex.Observable;

public interface BIVSC<ViewModel, State> {
    Consumer<Observable<Pair<ViewModel, Observable<State>>>> getBinder();

    Observable<State> getController();
}