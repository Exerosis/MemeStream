package stream.meme.app.bisp.alpha;

import android.util.Pair;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;


public abstract class BIVSCModule<ViewModel, State> implements BIVSC<ViewModel, State> {
    private Disposable disposable;

    public BIVSCModule() throws Exception {
        Maybe<ViewModel> viewModel = getViewModel();
        getBinder().accept(viewModel.flatMap(view -> {

            disposable = controller.subscribe(state::onNext, state::onError, state::onComplete);
            Pair.create()
        }));
        Observable<State> controller = getController();
        viewModel.subscribe(view -> {
            disposable = controller.subscribe(state::onNext, state::onError, state::onComplete);
        }, ignored -> {
            if (disposable != null)
                disposable.dispose();
        });
    }

    abstract Maybe<ViewModel> getViewModel();
}
