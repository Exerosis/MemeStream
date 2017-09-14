package stream.meme.app.profile;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import stream.meme.app.R;
import stream.meme.app.bisp.DatabindingBIVSCModule;

public class ProfileController extends DatabindingBIVSCModule {
    public ProfileController() throws Exception {
        super(R.layout.meme_view);
    }

    @Override
    public BiConsumer<Observable, Observable> getBinder() {
        return null;
    }

    @Override
    public Observable getController() {
        return null;
    }
}

