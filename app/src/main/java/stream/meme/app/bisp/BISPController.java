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
    protected int layout;
    private final List<Disposable> disposables = new ArrayList<>();
    private final Map<Subject<?>, Function<View, ?>> viewBinders = new HashMap<>();
    private Observable<State> state;

    public BISPController(@LayoutRes int layout) {
        this.layout = layout;
        try {
            state = getIntentToStateBinder().apply(getIntents());
            getViewToIntentBinder().accept(getIntents(), new Binder<View>() {
                @Override
                public <Type> Observable<Type> bind(Function<View, Observable<Type>> binder) {
                    Subject<Type> subject = BehaviorSubject.create();
                    viewBinders.put(subject, binder);
                    return subject;
                }
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
        Subject<State> subject = BehaviorSubject.create();
        try {
            for (Map.Entry<Subject<?>, Function<View, ?>> entry : viewBinders.entrySet()) {
                Subject<Object> key = (Subject<Object>) entry.getKey();
                disposables.add(((Function<View, Observable<Object>>) entry.getValue()).apply(getViewHolder()).subscribe(key::onNext, key::onError, key::onComplete));
            }
            getStateToViewBinder().accept(getViewHolder(), subject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        disposables.add(state.subscribe(subject::onNext, subject::onError, subject::onComplete));
    }

    @Override
    protected void onDetach(@NonNull android.view.View view) {
        for (Disposable disposable : disposables)
            disposable.dispose();
    }
}