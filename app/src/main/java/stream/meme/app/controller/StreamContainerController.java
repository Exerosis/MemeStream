package stream.meme.app.controller;

import android.view.View;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import stream.meme.app.R;
import stream.meme.app.databinding.StreamContainerViewBinding;
import stream.meme.app.util.bivsc.DatabindingBIVSCModule;

public class StreamContainerController extends DatabindingBIVSCModule<StreamContainerViewBinding, StreamContainerController.State> {
    private final Intents intents = new Intents();

    public StreamContainerController() {
        super(R.layout.stream_container_view);
    }

    @Override
    public BiConsumer<Observable<StreamContainerViewBinding>, Observable<State>> getBinder() {
        return (viewModel, state) -> {
            viewModel.subscribe(view -> {
                new DrawerBuilder(getActivity())
                        .inflateMenu(R.menu.home_navigation_menu)
                        .withAccountHeader(new AccountHeaderBuilder()
                                .withActivity(getActivity())
                                .withOnAccountHeaderProfileImageListener(new AccountHeader.OnAccountHeaderProfileImageListener() {
                                    @Override
                                    public boolean onProfileImageClick(View view, IProfile profile, boolean current) {
                                        intents.ProfileClickedIntent.onNext(true);
                                        return true;
                                    }

                                    @Override
                                    public boolean onProfileImageLongClick(View view, IProfile profile, boolean current) {
                                        return false;
                                    }
                                })
                                .withSelectionListEnabled(false)
                                .addProfiles(new ProfileDrawerItem()
                                        .withEmail("exerosis@gmail.com")
                                        .withName("Exerosis")
                                        .withIcon("")
                                        .withIdentifier(0L))
                                .build())
                        .withOnDrawerItemClickListener((v, position, drawerItem) -> {
                            intents.NavigateIntent.onNext(((int) drawerItem.getIdentifier()));
                            return true;
                        })
                        .build();
            });

            viewModel.subscribe(view -> {
                state.map(State::stream).map(RouterTransaction::with).subscribe(getChildRouter(view.container)::setRoot);
            });
        };
    }

    @Override
    public Observable<State> getController() {
        return Observable.merge(
                intents.ProfileClickedIntent.map(test ->
                        Partial.Open(getRouter(), new StreamController())),
                intents.NavigateIntent.map(id ->
                        Partial.Navigate(id == R.id.navigation_home ? new StreamController() : new StreamController())))
                .scan(new State(new StreamController()), (state, partial) -> partial.apply(state));
    }

    static class Intents {
        Subject<Integer> NavigateIntent = PublishSubject.create();
        Subject<Boolean> ProfileClickedIntent = PublishSubject.create();
    }

    interface Partial {
        static Function<State, State> Navigate(Controller controller) {
            return state -> {
                state.Stream = controller;
                return state;
            };
        }

        static Function<State, State> Open(Router activity, Controller controller) {
            return state -> {
//                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, )
//                router.pushController(RouterTransaction.with(controller).pushChangeHandler(new CircularRevealChangeHandler()));
                return state;
            };
        }
    }

    static class State {
        Controller Stream;

        Controller stream() {
            return Stream;
        }

        State(Controller stream) {
            Stream = stream;
        }
    }
}