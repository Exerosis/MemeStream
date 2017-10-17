package stream.meme.app.controller;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.common.base.Optional;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import jp.wasabeef.blurry.Blurry;
import stream.meme.app.R;
import stream.meme.app.application.MemeStream;
import stream.meme.app.application.Profile;
import stream.meme.app.application.login.LoginType;
import stream.meme.app.databinding.ProfileViewBinding;
import stream.meme.app.util.bivsc.DatabindingBIVSCModule;

import static android.support.v4.content.ContextCompat.getColor;
import static com.google.common.base.Optional.fromNullable;
import static com.jakewharton.rxbinding2.view.RxView.clicks;
import static com.jakewharton.rxbinding2.view.RxView.visibility;
import static stream.meme.app.util.Optionals.ifPresent;

public class ProfileController extends DatabindingBIVSCModule<ProfileViewBinding, ProfileController.State> {
    private MemeStream memeStream;
    private final Intents intents = new Intents();

    public ProfileController() {
        super(R.layout.profile_view);
    }


    @Override
    protected void onContextAvailable(@NonNull Context context) {
        memeStream = (MemeStream) getApplicationContext();

        super.onContextAvailable(context);
    }

    @Override
    public BiConsumer<Observable<ProfileViewBinding>, Observable<State>> getBinder() {
        return (views, states) -> {
            views.subscribe(view -> {
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                activity.setSupportActionBar(view.toolbar);
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
                activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(getActivity(), R.color.yourTranslucentColor)));

                states.map(State::loading).subscribe(visibility(view.progressBar));

                ifPresent(states.map(State::profile), profile -> {
                    ifPresent(profile.getImage(), image -> {
                        view.profileImage.setImageBitmap(image);
                        Blurry.with(getActivity()).from(image).into(view.backgroundImage);
                    });
                    view.name.setText(profile.getName());
                    view.email.setText(profile.getEmail());

                    intents.EditProfile = clicks(view.editProfile);

                    for (LoginType type : profile.getLogins())
                        switch (type) {
                            case FACEBOOK: {
                                view.facebook.setEnabled(false);
                                break;
                            }
                            case TWITTER: {
                                view.twitter.setEnabled(false);
                                break;
                            }
                            case GOOGLE: {
                                view.google.setEnabled(false);
                                break;
                            }
                        }
                });
            });
        };
        //TODO remove loading... it doesn't make much sense.
    }

    @Override
    public Observable<State> getController() {
        return memeStream.getProfile().map(State::new).startWith(new State());
    }

    class Intents {
        Observable<Object> EditProfile;
    }

    class State {
        boolean loading = true;
        Optional<Profile> profile = fromNullable(null);

        public State(@NonNull Profile profile) {
            this.profile = fromNullable(profile);
            loading = false;
        }

        public State() {
            loading = true;
        }

        public boolean loading() {
            return loading;
        }

        public Optional<Profile> profile() {
            return profile;
        }
    }
}