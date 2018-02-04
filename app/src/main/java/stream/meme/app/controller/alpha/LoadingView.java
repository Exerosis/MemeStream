package stream.meme.app.controller.alpha;

import android.content.Context;
import android.support.annotation.NonNull;

import stream.meme.app.databinding.LoadingViewBinding;
import stream.meme.app.util.components.components.StatefulViewComponent;

import static stream.meme.app.R.layout.loading_view;

public class LoadingView extends StatefulViewComponent<Boolean, LoadingViewBinding> {
    public LoadingView(@NonNull Context context) {
        super(context, loading_view);

        setState(false);
        getViews().subscribe(view -> getStates().distinctUntilChanged().subscribe(visible ->
                view.progressBar.setVisibility(visible ? VISIBLE : INVISIBLE)));
    }
}
