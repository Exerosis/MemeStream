package stream.meme.app.util.components.test;

import android.content.Context;
import android.support.annotation.NonNull;

import stream.meme.app.databinding.ThirdLayoutBinding;
import stream.meme.app.util.components.components.ViewComponent;

import static stream.meme.app.R.layout.third_layout;

/**
 * Created by Exerosis on 2/4/2018.
 */

public class ThirdLayout extends ViewComponent<ThirdLayoutBinding> {
    public ThirdLayout(@NonNull Context context) {
        super(context, third_layout);
    }
}
