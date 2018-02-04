package stream.meme.app.util.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;

import java.lang.reflect.Field;

//TODO Make this a ViewGroup for better performance.
public class ViewDelegate extends FrameLayout {
    public static final Field PARENT;

    static {
        try {
            PARENT = View.class.getDeclaredField("mParent");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private View view;

    public ViewDelegate(@NonNull Context context) {
        super(context);
    }

    @Override
    public void addView(View view, int index) {
        this.view = view;
        try {
            PARENT.set(this, null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        removeAllViews();
        setId(view.getId());
        view.setId(NO_ID);
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
