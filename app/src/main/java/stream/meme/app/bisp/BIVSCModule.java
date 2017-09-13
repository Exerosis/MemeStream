package stream.meme.app.bisp;

import com.bluelinelabs.conductor.Controller;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;


public abstract class BIVSCModule<ViewModel, State> extends Controller implements BIVSC<ViewModel, State> {
    private Disposable disposable;
    private Observable<State> controller = null;

    public BIVSCModule() throws Exception {

    }


    abstract public Observable<ViewModel> getViewModel();
}
