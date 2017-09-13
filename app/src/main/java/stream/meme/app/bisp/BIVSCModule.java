package stream.meme.app.bisp;

import android.support.annotation.NonNull;
import android.view.View;

import com.bluelinelabs.conductor.Controller;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;


public abstract class BIVSCModule<ViewModel, State> extends Controller implements BIVSC<ViewModel, State> {
    private final List<Disposable> disposables = new ArrayList<>();
    final Subject<ViewModel> viewModel = PublishSubject.create();
    private ConnectableObservable<State> state;

    public BIVSCModule() throws Exception {
        state = null;
        List<Observer<? super State>> subscribers = new ArrayList<>();
        getBinder().accept(viewModel, Observable.unsafeCreate(observer -> {
            if (state == null)
                subscribers.add(observer);
            else
                state.subscribe(observer);
        }));
        state = getController().publish();
        for (Observer<? super State> subscriber : subscribers)
            state.subscribe(subscriber);
        state.connect(disposables::add);
    }

    @Override
    protected void onDetach(@NonNull View view) {
        for (Disposable disposable : disposables)
            disposable.dispose();
    }

    @Override
    protected void onDestroy() {
        viewModel.onComplete();
    }
}