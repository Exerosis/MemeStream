package stream.meme.app.util.bivsc;


import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

import static io.reactivex.Observable.merge;
import static java.util.Arrays.asList;

public interface Reducer<State> extends Function<State, State> {
    static <State> BiFunction<State, Function<State, State>, State> accumulator() {
        return (state, reducer) -> reducer.apply(state);
    }

    @SafeVarargs
    static <State> Observable<State> controller(State defaultState, Observable<Function<State, State>>... partials) {
        return merge(asList(partials)).scan(defaultState, accumulator());
    }
}
