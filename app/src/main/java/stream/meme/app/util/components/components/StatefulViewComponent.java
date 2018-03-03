package stream.meme.app.util.components.components;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.subjects.BehaviorSubject;

import static io.reactivex.subjects.BehaviorSubject.create;
import static stream.meme.app.util.Functions.runtime;

public class StatefulViewComponent<State, ViewModel extends ViewDataBinding> extends ViewComponent<ViewModel> {
    private final BehaviorSubject<State> state = create();

    public StatefulViewComponent(@NonNull Context context, int layout) {
        super(context, layout);
    }

    public StatefulViewComponent(@NonNull Context context) {
        super(context);
    }

    public void applyPartial(Function<State, State> partial) {
        try {
            setState(partial.apply(getState()));
        } catch (Exception e) {
            throw runtime(e);
        }
    }

    public <Return> Observable<Return> componentSwitchMap(Function<ViewModel, ObservableSource<Return>> mapper) {
        return getViews().firstElement().flatMapObservable(mapper::apply);
    }

    public <Return> Observable<Return> viewSwitchMap(Function<ViewModel, ObservableSource<Return>> mapper) {
        return getViews().switchMap(mapper);
    }

    public <Return> Observable<Return> viewStateSwitchMap(BiFunction<ViewModel, State, ObservableSource<Return>> mapper) {
        return getViews().flatMap(view -> getStates().switchMap(state -> mapper.apply(view, state)));
    }

    public void setState(State state) {
        this.state.onNext(state);
    }

    public void setState(Observable<State> state) {
        state.subscribe(this.state);
    }

    public State getState() {
        return state.getValue();
    }

    public Observable<State> getStates() {
        return state;
    }
}
