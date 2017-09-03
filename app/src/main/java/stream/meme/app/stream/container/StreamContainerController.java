package stream.meme.app.stream.container;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;
import stream.meme.app.R;
import stream.meme.app.bisp.BISPDatabindingController;
import stream.meme.app.databinding.StreamContainerViewBinding;
import stream.meme.app.profile.ProfileController;
import stream.meme.app.stream.StreamController;


public class StreamContainerController extends BISPDatabindingController<Intents, StreamContainerViewBinding, State> {

    public StreamContainerController() {
        super(R.layout.stream_container_view);
    }

    @Override
    public BiConsumer<StreamContainerViewBinding, Observable<State>> getStateToViewBinder() {
        return (view, viewState) -> {
            //Bind changes of state to changes of view.
            viewState.subscribe(state -> {
                //Sets the main content of our view to the specified controllers.
                getChildRouter(view.container).setRoot(RouterTransaction.with(state.Stream));
            });
        };
    }

    @Override
    public BiConsumer<Intents, Binder<StreamContainerViewBinding>> getViewToIntentBinder() {
        return (intents, binder) -> {
            //Bind changes in view to intents
            if (getActivity() != null)
                intents.NavigateIntent = binder.bind(view -> Observable.create(subscriber ->
                        new DrawerBuilder(getActivity())
                                .inflateMenu(R.menu.home_navigation_menu)
                                .withAccountHeader(new AccountHeaderBuilder()
                                        .addProfiles(new ProfileDrawerItem()
                                                .withEmail("exerosis@gmail.com")
                                                .withName("Exerosis")
                                                .withIcon("")
                                                .withIdentifier(0L))
                                        .build())
                                .withOnDrawerItemClickListener((v, position, drawerItem) -> {
                                    subscriber.onNext(((int) drawerItem.getIdentifier()));
                                    return true;
                                })
                                .build()));
            intents.ProfileClickedIntent = intents.NavigateIntent.filter(id -> id.equals(0)).map(id -> null);
        };
    }

    @Override
    public Function<Intents, Observable<State>> getIntentToStateBinder() {
        //Bind changes of intent to changes of state
        return intents -> Observable.merge(intents.ProfileClickedIntent.map(test -> {
            //Handle changes in test
            getRouter().setRoot(RouterTransaction.with(new ProfileController()));
            return new State();
        }), intents.NavigateIntent.map(id -> {
            State state = new State();
            switch (id) {
                case R.id.navigation_home: {
                    state.Stream = new StreamController();
                    break;
                }
                case R.id.navigation_top: {
                    state.Stream = new StreamController();
                }
            }
            return state;
        }));
    }

    @Override
    public Intents getIntents() {
        return new Intents();
    }
}

class Intents {
    Observable<Integer> NavigateIntent;
    Observable<Void> ProfileClickedIntent;
}

class State {
    Controller Stream;
}