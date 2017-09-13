package stream.meme.app.stream.container;

import android.support.v4.util.Pair;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import stream.meme.app.R;
import stream.meme.app.bisp.DatabindingBIVSCModule;
import stream.meme.app.databinding.StreamContainerViewBinding;
import stream.meme.app.profile.ProfileController;
import stream.meme.app.stream.StreamController;


public class StreamContainerController extends DatabindingBIVSCModule<StreamContainerViewBinding, State, Intents> {
    private Intents intents;

    public StreamContainerController() throws Exception {
        super(R.layout.stream_container_view);
    }

    @Override
    public Consumer<Observable<Pair<StreamContainerViewBinding, Observable<State>>>> getBinder() {
        return binder -> {
            Observable<StreamContainerViewBinding> viewModel = binder.map(pair -> pair.first);
            Observable<State> state = binder.flatMap(pair -> pair.second);

            getIntents().NavigateIntent = viewModel.switchMap(view -> Observable.create(subscriber -> {
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
            getIntents().ProfileClickedIntent = getIntents().NavigateIntent.filter(id -> id.equals(0)).map(id -> null);

            viewModel.subscribe(view -> {
                state.map(State::stream).map(RouterTransaction::with).forEach(getChildRouter(view.container)::setRoot);
            });
        };
    }

    @Override
    public Observable<State> getController() {
        return Observable.merge(getIntents().ProfileClickedIntent.map(test -> {
            StreamContainerController.this.getRouter().setRoot(RouterTransaction.with(new ProfileController()));
            return new State();
        }), getIntents().NavigateIntent.map(id -> {
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

    @Override
    public Intents getIntents() {
        if (intents == null)
            intents = new Intents();
        return intents;
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