package stream.meme.app.profile;

import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;
import stream.meme.app.R;
import stream.meme.app.bisp.BISPDatabindingController;

public class ProfileController extends BISPDatabindingController {
    public ProfileController() {
        super(R.layout.meme_view);
    }

    @Override
    public BiConsumer getStateToViewBinder() {
        return (o, o2) -> {

        };
    }

    @Override
    public BiConsumer getViewToIntentBinder() {
        return (o, o2) -> {

        };
    }

    @Override
    public Function getIntentToStateBinder() {
        return o -> null;
    }

    @Override
    public Object getIntents() {
        return null;
    }
}

