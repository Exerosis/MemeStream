package stream.meme.app.bisp;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public abstract class DatabindingBIVSCModule<View extends ViewDataBinding, State, Intents> extends BIVSCModule<View, State> {
    private final Subject<View> viewModel = PublishSubject.create();
    private final List<Disposable> disposables = new ArrayList<>();
    private int layout;

    public DatabindingBIVSCModule(@LayoutRes int layout) throws Exception {
        this.layout = layout;
        ConnectableObservable<State> state = getController().publish();
        getBinder().accept(getViewModel(), state.replay(1));
        state.connect(disposables::add);

        getBinder().accept(getViewModel().doOnError(throwable -> {
            if (disposable != null)
                disposable.dispose();
            disposable = null;
        }).map(view -> {
            if (disposable != null)
                disposable.dispose();
            disposable = null;
            Subject<State> subject = PublishSubject.create();
            disposable = controller.subscribe(subject::onNext, subject::onError, subject::onComplete);
            return Pair.create(view, subject.publish());
        }).publish());
        controller = getController();
    }

    abstract public Intents getIntents();

    @NonNull
    @Override
    protected android.view.View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = DataBindingUtil.inflate(inflater, layout, container, false);
        viewModel.onNext(view);
        return view.getRoot();
    }

    @Override
    protected void onDetach(@NonNull android.view.View view) {
        try {
            viewModel.onError(new Exception("View deflating, not an actual error!"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        viewModel.onComplete();
    }

    @Override
    public ConnectableObservable<View> getViewModel() {
        return viewModel.publish();
    }
}