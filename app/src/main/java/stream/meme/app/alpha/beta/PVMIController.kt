package stream.meme.app.alpha.beta

import com.google.common.base.Supplier
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.subjects.Subject

typealias IntentBinder<View, Type> = Map<Subject<Type>, Function<View, Observable<Type>>>
typealias ViewBinder<View, ViewState> = List<Function<View, Consumer<Observable<ViewState>>>>
typealias Presenter<Intents, ViewState> = Function<Intents, Observable<ViewState>>

interface PMVIController<View, Intents, ViewState, out ViewModel> where
ViewModel : Supplier<ViewBinder<View, ViewState>>,
ViewModel : Function<Intents, IntentBinder<View, Any>> {
    fun getViewModel(): ViewModel
    fun getPresenter(): Presenter<Intents, ViewState>
    fun getIntents(): Intents
}