package stream.meme.app.bisp;


import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;

public interface BISP<Intents, View, State> {

    BiConsumer<View, Observable<State>> getStateToViewBinder();

    BiConsumer<Intents, Binder<View>> getViewToIntentBinder();

    Function<Intents, Observable<State>> getIntentToStateBinder();

    Intents getIntents();

    interface Binder<View> {
        <Type> Observable<Type> bind(Function<View, Observable<Type>> binder);
    }
}