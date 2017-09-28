package stream.meme.app.util.bivsc;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;

public interface BIVSC<ViewModel, State> {
    BiConsumer<Observable<ViewModel>, Observable<State>> getBinder();

    Observable<State> getController();
}