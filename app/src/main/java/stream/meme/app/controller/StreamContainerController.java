package stream.meme.app.controller;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.google.common.base.Optional;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import jp.wasabeef.blurry.Blurry;
import stream.meme.app.R;
import stream.meme.app.activity.ProfileActivity;
import stream.meme.app.application.MemeStream;
import stream.meme.app.application.Profile;
import stream.meme.app.databinding.StreamContainerViewBinding;
import stream.meme.app.util.bivsc.DatabindingBIVSCModule;
import stream.meme.app.util.bivsc.Reducer;

import static android.support.v4.app.ActivityOptionsCompat.makeSceneTransitionAnimation;
import static stream.meme.app.util.Optionals.ifPresent;
import static stream.meme.app.util.bivsc.Reducer.controller;

public class StreamContainerController extends DatabindingBIVSCModule<StreamContainerViewBinding, StreamContainerController.State> {
    private final Intents intents = new Intents();
    private final Map<Integer, Controller> streams = new HashMap<>();
    private MemeStream memeStream;

    public StreamContainerController() {
        super(R.layout.stream_container_view);
        streams.put(R.id.navigation_home, new StreamController());
        streams.put(R.id.navigation_top, new StreamController());
    }

    @Override
    protected void onContextAvailable(@NonNull Context context) {
        memeStream = (MemeStream) getApplicationContext();
        super.onContextAvailable(context);
    }

    @SuppressWarnings("unchecked")
    @Override
    public BiConsumer<Observable<StreamContainerViewBinding>, Observable<State>> getBinder() {
        return (views, states) -> views.subscribe(view -> {
            ((AppCompatActivity) getActivity()).setSupportActionBar(view.toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), view.drawerLayout, view.toolbar, R.string.description_open, R.string.description_close);
            view.drawerLayout.addDrawerListener(toggle);
            ViewGroup header = (ViewGroup) view.navigationView.getHeaderView(0);
            ImageView backgroundImage = header.findViewById(R.id.background_image);
            ImageView profileImage = header.findViewById(R.id.profile_image);
            TextView name = header.findViewById(R.id.name);
            TextView email = header.findViewById(R.id.email);

            RxView.clicks(profileImage).doOnNext(test -> {
                System.out.println(test);
            }).subscribe(intents.ProfileClickedIntent);

            intents.ProfileClickedIntent.subscribe(test -> {
                System.out.println(test);
            });

            ifPresent(states.map(State::profile), profile -> {
                ifPresent(profile.getImage(), image -> {
                    Blurry.with(getActivity()).from(image).into(backgroundImage);
                    profileImage.setImageBitmap(image);
                });
                name.setText(profile.getName());
                email.setText(profile.getEmail());
            });

            states.map(State::controller)
                    .distinctUntilChanged(Object::hashCode)
                    .map(RouterTransaction::with)
                    .subscribe(getChildRouter(view.container)::setRoot);

            states.map(State::settings).doOnNext(test -> {
                System.out.println(test);
            })
                    .distinctUntilChanged()
                    .filter(it -> it)
                    .subscribe(ignored -> {
                        ActivityOptionsCompat options = makeSceneTransitionAnimation(getActivity(),
                                new Pair<>(backgroundImage, "background_image"),
                                new Pair<>(profileImage, "profile_image"),
                                new Pair<>(name, "name"),
                                new Pair<>(email, "email"),
                                new Pair<>(view.toolbar, "toolbar"));
                        getActivity().startActivity(new Intent(getActivity(), ProfileActivity.class), options.toBundle());
                        intents.SettingsOpenedIntent.onNext(false);
                    });

        });
    }

    @Override
    public Observable<State> getController() {
        return controller(new State(streams.get(R.id.navigation_home)),
                intents.NavigateIntent.map(streams::get).map(Partial::Navigated),
                intents.ProfileClickedIntent.map(ignored ->
                        Partial.SettingsOpen()),
                intents.SettingsOpenedIntent.map(ignored ->
                        Partial.SettingsClose()),
                memeStream.getProfile().map(Partial::Loaded)
        );
    }

    static class Intents {
        Subject<Integer> NavigateIntent = PublishSubject.create();
        Subject<Object> ProfileClickedIntent = PublishSubject.create();
        Subject<Object> SettingsOpenedIntent = PublishSubject.create();
    }

    interface Partial {
        static Reducer<State> Navigated(Controller controller) {
            return state -> {
                state.controller = controller;
                return state;
            };
        }

        static Reducer<State> Loaded(Profile profile) {
            return state -> {
                state.loading = false;
                state.profile = profile;
                return state;
            };
        }

        static Reducer<State> SettingsOpen() {
            return state -> {
                state.settings = true;
                return state;
            };
        }

        static Reducer<State> SettingsClose() {
            return state -> {
                state.settings = false;
                return state;
            };
        }
    }

    class State {
        boolean loading = true;
        Profile profile = null;
        boolean settings = false;
        Controller controller;

        public State(Controller controller) {
            this.controller = controller;
        }

        public boolean settings() {
            return settings;
        }

        public boolean loading() {
            return loading;
        }

        public Controller controller() {
            return controller;
        }

        public Optional<Profile> profile() {
            return Optional.fromNullable(profile);
        }
    }
}