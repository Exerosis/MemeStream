package stream.meme.app.util.viewcomp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;

//TODO Make this a ViewGroup for better performance.
public class ViewDelegate extends FrameLayout {
    private View view;

    public ViewDelegate(@NonNull Context context) {
        super(context);
    }

    @Override
    public void addView(View view, int index) {
        this.view = view;
        removeAllViews();
        super.addView(view, index);
    }

    public void setView(View view) {
        addView(view);
    }

    public boolean hasView() {
        return view != null;
    }

    public View getView() {
        return view;
    }
}