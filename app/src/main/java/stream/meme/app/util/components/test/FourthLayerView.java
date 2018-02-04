package stream.meme.app.util.components.test;

import android.content.Context;
import android.support.annotation.NonNull;

import stream.meme.app.R;
import stream.meme.app.databinding.FourthLayoutBinding;
import stream.meme.app.util.components.components.ViewComponent;

/**
 * Created by Exerosis on 2/4/2018.
 */

public class FourthLayerView extends ViewComponent<FourthLayoutBinding> {
    public FourthLayerView(@NonNull Context context) {
        super(context, R.layout.fourth_layout);
    }
}
