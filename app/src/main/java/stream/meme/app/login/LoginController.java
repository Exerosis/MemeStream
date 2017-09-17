package stream.meme.app.login;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bluelinelabs.conductor.RouterTransaction;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiConsumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import stream.meme.app.application.MemeStream;
import stream.meme.app.R;
import stream.meme.app.bisp.DatabindingBIVSCModule;
import stream.meme.app.databinding.LoginViewBinding;
import stream.meme.app.stream.container.StreamContainerController;

import static com.jakewharton.rxbinding2.view.RxView.visibility;

public class LoginController extends DatabindingBIVSCModule<LoginViewBinding, State> {
    private final Intents intents = new Intents();
    private MemeStream memeStream;

    public LoginController() throws Exception {
        super(R.layout.login_view);
    }

    @Override
    protected void onContextAvailable(@NonNull Context context) {
        memeStream = (MemeStream) getApplicationContext();
        super.onContextAvailable(context);
    }

    @Override
    public BiConsumer<Observable<LoginViewBinding>, Observable<State>> getBinder() {
        return (views, states) -> views.subscribe(view -> {
            states.map(State::authenticating).subscribe(authenticating -> {
                visibility(view.progressBar).accept(authenticating);
                visibility(view.buttons).accept(!authenticating);
            });
            view.setIntents(intents);
        });
    }

    @Override
    public Observable<State> getController() {
        return intents.LoginStartIntent
                .flatMap(loginType -> memeStream.login(loginType)
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(result -> {
                            if (result)
                                getRouter().setRoot(RouterTransaction.with(new StreamContainerController()));
                            return new State(false);
                        }).startWith(new State(true)));
    }

    public static class Intents {
        Subject<LoginType> LoginStartIntent = PublishSubject.create();

        public void login(LoginType type) {
            LoginStartIntent.onNext(type);
        }
    }
}

class State {
    boolean authenticating = false;

    State(boolean authenticating) {
        this.authenticating = authenticating;
    }

    public boolean authenticating() {
        return authenticating;
    }
}

