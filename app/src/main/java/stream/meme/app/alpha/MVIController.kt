package stream.meme.app.alpha

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.google.common.base.Supplier
import io.reactivex.Observable
import io.reactivex.functions.Consumer

abstract class MVIController<ViewState, Intent, Presenter> : Controller() where
Intent : Supplier<View>,
Intent : Consumer<Observable<ViewState>>,
Presenter : Supplier<Observable<ViewState>>,
Presenter : Consumer<Intent?> {
    lateinit var intentPresenter: Presenter
    lateinit var intent: Intent

    abstract fun getView(inflater: LayoutInflater, container: ViewGroup): Intent

    abstract fun getPresenter(): Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        intent = getView(inflater, container)
        intentPresenter = getPresenter()
        intentPresenter.accept(intent)
        intent.accept(intentPresenter.get())
        return intent.get();
    }

    override fun onDestroyView(view: View) {
        intentPresenter.accept(intent);
    }
}