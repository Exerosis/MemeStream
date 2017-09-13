package stream.meme.app.bisp;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.observables.ConnectableObservable;

public interface BIVSC<ViewModel, State> {
    BiConsumer<ConnectableObservable<ViewModel>, ConnectableObservable<State>> getBinder();

    Observable<State> getController();
}