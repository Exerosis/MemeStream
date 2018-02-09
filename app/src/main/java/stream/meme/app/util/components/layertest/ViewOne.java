package stream.meme.app.util.components.layertest;

import android.content.Context;
import android.support.annotation.NonNull;

import stream.meme.app.R;
import stream.meme.app.databinding.ViewOneBinding;
import stream.meme.app.util.components.components.ViewComponent;

/**
 * Created by Exerosis on 2/4/2018.
 */

public class ViewOne extends ViewComponent<ViewOneBinding> {
    public ViewOne(@NonNull Context context) {
        super(context, R.layout.view_one);
        getComponents(components -> {
        });
    }
}
