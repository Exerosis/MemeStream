package stream.meme.app.util.viewcomp.alpha;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Exerosis on 1/12/2018.
 */
public class ViewDelegateTwo extends FrameLayout {
    private int rotCounter = 0;

    public ViewDelegateTwo(@NonNull Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    public String getRots() {
        return "Rots so far: " + ++rotCounter;
    }

    public void setView(View view) {
        removeAllViews();
        addView(view);
    }
}
