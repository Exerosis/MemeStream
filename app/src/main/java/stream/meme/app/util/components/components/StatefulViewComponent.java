package stream.meme.app.util.components.components;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;

import io.reactivex.Observable;
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
