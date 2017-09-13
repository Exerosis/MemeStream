package stream.meme.app.profile;

import android.support.v4.util.Pair;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import stream.meme.app.R;
import stream.meme.app.bisp.DatabindingBIVSCModule;

public class ProfileController extends DatabindingBIVSCModule {
    public ProfileController() throws Exception {
        super(R.layout.meme_view);
    }

    @Override
    public Object getIntents() {
        return null;
    }

    @Override
    public Consumer<Observable<Pair>> getBinder() {
        return null;
    }

    @Override
    public Observable getController() {
        return null;
    }
}

