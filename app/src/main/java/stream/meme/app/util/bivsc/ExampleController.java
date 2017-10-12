package stream.meme.app.util.bivsc;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import stream.meme.app.databinding.ProfileViewBinding;

import static com.jakewharton.rxbinding2.view.RxView.clicks;
import static stream.meme.app.R.layout.profile_view;
import static stream.meme.app.util.bivsc.Reducer.controller;

public class ExampleController extends DatabindingBIVSCModule<ProfileViewBinding, ExampleController.State> {
    private final Intents intents = new Intents();

    public ExampleController() {
        super(profile_view);
    }

    static class Intents {
        Observable<String> NameIntent;
    }

    class State {
        String name = null;

        String name() {
            return name;
        }
    }

    interface Partials {
        static Reducer<State> DefaultPartial(String name) {
            return state -> {
                state.name = name;
                return state;
            };
        }
    }

    @Override
    public BiConsumer<Observable<ProfileViewBinding>, Observable<State>> getBinder() {
        return (views, states) -> {
            views.subscribe(view -> {
                intents.NameIntent = clicks(view.getRoot())
                        .map(Object::toString);

                //--Name--
                states.map(State::name)
                        .distinctUntilChanged(Object::hashCode)
                        .subscribe(System.out::println);
            });
        };
    }

    @Override
    public Observable<State> getController() {
        return controller(new State(),
                //--Name--
                intents.NameIntent
                        .map(Partials::DefaultPartial)
        );
    }
}