package stream.meme.app.alpha

import com.google.common.collect.Lists
import com.google.common.collect.Maps
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.subjects.BehaviorSubject

abstract class MVIPresenter<ViewState, Intent : Consumer<Observable<ViewState>>>(default: ViewState?) : Consumer<Intent> {
    val states: BehaviorSubject<ViewState> = BehaviorSubject.createDefault(default)
    val disposables: ArrayList<Disposable> = Lists.newArrayList()
    val binders: HashMap<BehaviorSubject<Any>, Function<Intent, Observable<Any>>> = Maps.newHashMap()

    override fun accept(intent: Intent) {
        if (intent == null) {
            disposables.forEach {
                it.dispose();
            }
        } else {
            intent.accept(states)
            binders.forEach {
                disposables.add(it.value.apply(intent).subscribe(it.key::onNext, it.key::onError, it.key::onComplete))
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <Type> bind(binder: Function<Intent, Observable<Type>>): Observable<Type> {
        val subject: BehaviorSubject<Type> = BehaviorSubject.create();
        binders.put(subject, binder as Function<Intent, Observable<*>>)
        return subject
    }


    abstract fun bindIntents(): Observable<ViewState>

}