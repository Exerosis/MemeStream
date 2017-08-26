package stream.meme.app.alpha;

import android.view.View;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.BehaviorSubject;

public class Test  {
    protected void bindIntents() {
        Observable<String> test = null;

        bind(view -> viewState -> viewState.subscribe(view::setTransitionName))
    }

    public <T> Observable<T> test(Function<View, Observable<T>> binder) {
        BehaviorSubject<T> subject = BehaviorSubject.create();


        return subject;
    }
}
