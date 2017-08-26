package stream.meme.app.alpha.beta

import com.google.common.base.Supplier
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

abstract class BasePMVIViewModel<Intents, View, ViewState> : Supplier<ViewBinder<View, ViewState>>, Function<Intents, IntentBinder<View, Any>> {
    val viewBinders: ArrayList<Function<View, Consumer<Observable<ViewState>>>> = Lists.newArrayList()
    val intentBinders: HashMap<Subject<Any>, Function<View, Observable<Any>>> = Maps.newHashMap()

    abstract fun bindIntents(intents: Intents)

    @Suppress("UNCHECKED_CAST")
    fun <Type> intent(binder: Function<View, Observable<Type>>): Observable<Type> {
        val subject: Subject<Type> = BehaviorSubject.create()
        intentBinders.put(subject as Subject<Any>, binder as Function<View, Observable<Any>>)
        return subject
    }

    fun view(applier: Function<View, Consumer<Observable<ViewState>>>) {
        viewBinders.add(applier)
    }

    override fun apply(intents: Intents): IntentBinder<View, Any> {
        bindIntents(intents)
        return intentBinders
    }

    override fun get(): ViewBinder<View, ViewState> {
        return viewBinders
    }
}