package stream.meme.app.controller;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Supplier;
import com.jakewharton.rxbinding2.view.RxView;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import stream.meme.app.R;
import stream.meme.app.activity.ProfileActivity;
import stream.meme.app.application.MemeStream;
import stream.meme.app.databinding.StreamContainerViewBinding;
import stream.meme.app.util.bivsc.DatabindingBIVSCModule;

import static android.support.v4.app.ActivityOptionsCompat.makeSceneTransitionAnimation;
import static com.bluelinelabs.conductor.RouterTransaction.with;

public class StreamContainerController extends DatabindingBIVSCModule<StreamContainerViewBinding, Supplier<StreamController>> {
    private final Intents intents = new Intents();
    private MemeStream memeStream;

    public StreamContainerController() {
        super(R.layout.stream_container_view);
    }

    @Override
    protected void onContextAvailable(@NonNull Context context) {
        memeStream = (MemeStream) getApplicationContext();
        super.onContextAvailable(context);
    }

    @SuppressWarnings("unchecked")
    @Override
    public BiConsumer<Observable<StreamContainerViewBinding>, Observable<Supplier<StreamController>>> getBinder() {
        return (viewModel, state) -> viewModel.subscribe(view -> {
            ((AppCompatActivity) getActivity()).setSupportActionBar(view.toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), view.drawerLayout, view.toolbar, R.string.description_open, R.string.description_close);
            view.drawerLayout.addDrawerListener(toggle);
            ViewGroup header = (ViewGroup) view.feedContainerViewNavigation.getHeaderView(0);
            View backgroundImage = header.findViewById(R.id.background_image);
            View profileImage = header.findViewById(R.id.profile_image);
            RxView.clicks(profileImage).subscribe(intents.ProfileClickedIntent);

            state.subscribe(provider -> {
                StreamController controller = provider.get();
                if (controller != null)
                    getChildRouter(view.container).setRoot(with(controller));
                else {
                    ActivityOptionsCompat options = makeSceneTransitionAnimation(getActivity(),
                            new Pair<>(backgroundImage, "background_image"),
                            new Pair<>(profileImage, "profile_image"));
                    getActivity().startActivity(new Intent(getActivity(), ProfileActivity.class), options.toBundle());
                }
            });
        });
    }

    @Override
    public Observable<Supplier<StreamController>> getController() {
        return Observable.<Supplier<StreamController>>merge(
                intents.ProfileClickedIntent.map(test ->
                        () -> null),
                intents.NavigateIntent.map(id ->
                        () -> id == R.id.navigation_home ? new StreamController() : new StreamController()),
                memeStream.getProfile().map(StreamController))
                .startWith(StreamController::new);
    }

    static class Intents {
        Subject<Integer> NavigateIntent = PublishSubject.create();
        Subject<Object> ProfileClickedIntent = PublishSubject.create();
    }
}