package stream.meme.app.bisp;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

public abstract class BISPController<Intents, View, State> extends Controller implements BISP<Intents, View, State> {
    private final Intents intents;
    private final List<Disposable> disposables = new ArrayList<>();
    private final Map<Subject<?>, Function<View, ?>> viewBinders = new HashMap<>();
    protected int layout;
    private Observable<State> state;
    private Subject<State> stateSubject = BehaviorSubject.create();

    public BISPController(@LayoutRes int layout) {
        this.layout = layout;
        intents = getIntents();
        try {
            getViewToIntentBinder().accept(intents, new Binder<View>() {
                @Override
                public <Type> Observable<Type> bind(Function<View, Observable<Type>> binder) {
                    Subject<Type> subject = BehaviorSubject.create();
                    viewBinders.put(subject, binder);
                    return subject;
                }
            });
            state = getIntentToStateBinder().apply(intents);
            state.subscribe(test -> {
                System.out.println(test);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    abstract public View getViewHolder();

    @NonNull
    @Override
    protected android.view.View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(layout, container, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onAttach(@NonNull android.view.View view) {
        try {
            for (Map.Entry<Subject<?>, Function<View, ?>> entry : viewBinders.entrySet()) {
                Subject<Object> key = (Subject<Object>) entry.getKey();
                disposables.add(((Function<View, Observable<Object>>) entry.getValue()).apply(getViewHolder()).subscribe(key::onNext, key::onError, key::onComplete));
            }
            getStateToViewBinder().accept(getViewHolder(), stateSubject);
            disposables.add(state.subscribe(stateSubject::onNext, stateSubject::onError, stateSubject::onComplete));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDetach(@NonNull android.view.View view) {
        for (Disposable disposable : disposables)
            disposable.dispose();
    }
}