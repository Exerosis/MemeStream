package stream.meme.app

import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import com.jakewharton.rxbinding2.support.design.widget.RxNavigationView
import io.reactivex.Observable
import io.reactivex.functions.BiConsumer
import io.reactivex.functions.Function
import stream.meme.app.bisp.BISPPresenter
import stream.meme.app.databinding.StreamContainerViewBinding

class StreamContainerController : BISPDataBindingController<HomeContainerBinder, StreamContainerIntents, StreamContainerState, StreamContainerViewBinding>(R.layout.stream_container_view) {
    override fun getIntents() = StreamContainerIntents()

    override fun getBinder() = HomeContainerBinder(this)

    override fun getPresenter() = HomeContainerPresenter()
}

class HomeContainerPresenter : BISPPresenter<StreamContainerIntents, StreamContainerState> {
    override fun apply(intents: StreamContainerIntents): Observable<StreamContainerState> {
        return intents.NavigateIntent.map { StreamContainerState(StreamContainerController()) }
    }
}

class HomeContainerBinder(val controller: Controller) : BISPBinder<StreamContainerIntents, StreamContainerViewBinding, StreamContainerState>() {
    override fun onCreate(view: StreamContainerViewBinding) {
        view.drawerLayout.setAc
    }

    override fun onBind(intents: StreamContainerIntents) {
        //Bind NavigateIntent to itemSelection on the navView.
        intents.NavigateIntent = bind(Function {
            RxNavigationView.itemSelections(it.navigationView).map { it.itemId }
        })
        //Bind the Stream controller to the root of container
        bind(BiConsumer { view, state ->
            controller.activity
            controller.getChildRouter(view.container).setRoot(RouterTransaction.with(state.Stream))
        })
    }
}

class StreamContainerIntents {
    lateinit var NavigateIntent: Observable<Int>
}

class StreamContainerState(val Stream: Controller)