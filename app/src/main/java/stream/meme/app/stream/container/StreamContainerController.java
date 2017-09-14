package stream.meme.app.stream.container;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import stream.meme.app.R;
import stream.meme.app.bisp.DatabindingBIVSCModule;
import stream.meme.app.databinding.StreamContainerViewBinding;
import stream.meme.app.profile.ProfileController;
import stream.meme.app.stream.StreamController;


public class StreamContainerController extends DatabindingBIVSCModule<StreamContainerViewBinding, State> {
    private final Intents intents = new Intents();

    public StreamContainerController() {
        super(R.layout.stream_container_view);
    }

    @Override
    public BiConsumer<Observable<StreamContainerViewBinding>, Observable<State>> getBinder() {
        return (viewModel, state) -> {
            intents.NavigateIntent = viewModel.switchMap(view -> Observable.create(subscriber -> {
                new DrawerBuilder(getActivity())
                        .inflateMenu(R.menu.home_navigation_menu)
                        .withAccountHeader(new AccountHeaderBuilder()
                                .withActivity(getActivity())
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
                        .build();
            }));
            intents.ProfileClickedIntent = intents.NavigateIntent.filter(id -> id.equals(0)).map(id -> null);

            viewModel.subscribe(view -> {
                state.map(State::stream).map(RouterTransaction::with).subscribe(getChildRouter(view.container)::setRoot);
            });
        };
    }

    @Override
    public Observable<State> getController() {
        return Observable.merge(intents.ProfileClickedIntent.map(test -> {
            StreamContainerController.this.getRouter().setRoot(RouterTransaction.with(new ProfileController()));
            return new State();
        }), intents.NavigateIntent.map(id -> {
            State state = new State();
            //TODO push tags into controller
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
}

class Intents {
    Observable<Integer> NavigateIntent;
    Observable<Void> ProfileClickedIntent;
}

class State {
    Controller Stream;

    Controller stream() {
        return Stream;
    }
}