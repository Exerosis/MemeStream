package stream.meme.app;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.jakewharton.rxbinding2.support.design.widget.RxNavigationView;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import stream.meme.app.databinding.StreamContainerViewBinding;

public class TestHomeController extends BISPDataBindingController<BISPBinder<TestHomeIntents, StreamContainerViewBinding, TestHomeState>, TestHomeIntents, TestHomeState, StreamContainerViewBinding> {
    public TestHomeController() {
        super(R.layout.stream_container_view);
    }

    @NotNull
    @Override
    public BISPBinder<TestHomeIntents, StreamContainerViewBinding, TestHomeState> getBinder() {
        return new BISPBinder<TestHomeIntents, StreamContainerViewBinding, TestHomeState>() {
            @Override
            public void onBind(TestHomeIntents intents) {
                intents.NavigateIntent = bind((StreamContainerViewBinding view) -> {
                    return RxNavigationView.itemSelections(view.navigationView).map(menuItem -> menuItem.getItemId());
                });
                bind((view, state) -> getChildRouter(view.container).setRoot(RouterTransaction.with(state.getController())));
            }
        };
    }

    @NotNull
    @Override
    public Function<TestHomeIntents, Observable<TestHomeState>> getPresenter() {
        return intents -> intents.NavigateIntent.map(id -> new TestHomeState(new StreamContainerController()));
    }

    @Override
    public TestHomeIntents getIntents() {
        return new TestHomeIntents();
    }
}

class TestHomeIntents {
    public Observable<Integer> NavigateIntent;
}

class TestHomeState {
    private Controller controller;

    public TestHomeState(Controller controller) {
        this.controller = controller;
    }

    public Controller getController() {
        return controller;
    }
}