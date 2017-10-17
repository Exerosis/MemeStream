package stream.meme.app.util.bivsc;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.View;

import com.bluelinelabs.conductor.Controller;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;


public abstract class BIVSCModule<ViewModel, State> extends Controller implements BIVSC<ViewModel, State> {
    private final List<Disposable> disposables = new ArrayList<>();
    private ConnectableObservable<State> state = null;
    Subject<ViewModel> viewModel = PublishSubject.create();
    private boolean fresh = true;

    public BIVSCModule() {

    }

    @Override
    @CallSuper
    protected void onContextAvailable(@NonNull Context context) {
        if (state != null)
            return;
        List<Observer<? super State>> earlySubscribers = new ArrayList<>();
        try {
            getBinder().accept(viewModel.observeOn(AndroidSchedulers.mainThread()), Observable.unsafeCreate(observer -> {
                if (state == null)
                    earlySubscribers.add(observer);
                else
                    state.subscribe(observer);
            }));
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        state = getController().observeOn(AndroidSchedulers.mainThread()).publish();
        for (Observer<? super State> subscriber : earlySubscribers)
            state.subscribe(subscriber);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        if (fresh)
            state.connect(disposables::add);
        fresh = false;
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        for (Disposable disposable : disposables)
            disposable.dispose();
        fresh = true;
    }

    @Override
    protected void onDestroy() {
        viewModel.onComplete();
    }
}