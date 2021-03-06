package stream.meme.app.controller;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bluelinelabs.conductor.RouterTransaction;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiConsumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import stream.meme.app.R;
import stream.meme.app.application.MemeStream;
import stream.meme.app.application.login.ProviderType;
import stream.meme.app.util.bivsc.DatabindingBIVSCModule;
import stream.meme.app.databinding.LoginViewBinding;

public class LoginController extends DatabindingBIVSCModule<LoginViewBinding, Void> {
    private final Intents intents = new Intents();
    private MemeStream memeStream;

    public LoginController() {
        super(R.layout.login_view);
    }

    @Override
    protected void onContextAvailable(@NonNull Context context) {
        memeStream = (MemeStream) getApplicationContext();
        super.onContextAvailable(context);
    }

    @Override
    public BiConsumer<Observable<LoginViewBinding>, Observable<Void>> getBinder() {
        return (views, states) -> views.subscribe(view -> {
            view.setIntents(intents);
        });
    }

    @Override
    public Observable<Void> getController() {
        intents.LoginStartIntent.subscribe(loginType -> {
            if (!memeStream.isAuthenticated())
                memeStream.authenticate(loginType, getActivity()).observeOn(AndroidSchedulers.mainThread()).subscribe(result -> {
                    if (result)
                        getRouter().setRoot(RouterTransaction.with(new StreamContainerController()));
                }, Throwable::printStackTrace);
            else
                getRouter().setRoot(RouterTransaction.with(new StreamContainerController()));
        });
        return Observable.empty();
    }

    public class Intents {
        Subject<ProviderType> LoginStartIntent = PublishSubject.create();

        public void login(ProviderType type) {
            LoginStartIntent.onNext(type);
        }
    }
}

