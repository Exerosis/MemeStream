package stream.meme.app.util.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import java.lang.reflect.Field;

//TODO Make this a ViewGroup for better performance. Better yet just a View or something x)
public class ViewDelegate extends FrameLayout {
    public static final Field PARENT;

    static {
        try {
            PARENT = View.class.getDeclaredField("mParent");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private View view = null;

    public ViewDelegate(@NonNull Context context) {
        super(context);
    }

    //Throw for other add methods?
    @Override
    public void addView(View view, int index) {
        throw new UnsupportedOperationException("Adding views directly to this layout is not supported. Use resetView instead!");
    }

    public void resetView(View view, AttributeSet attributes) {
        try {
            this.view = view;
            removeAllViews();
            try {
                PARENT.set(this, null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            setId(attributes.getIdAttributeResourceValue(NO_ID));
            setLayoutParams(new LayoutParams(getContext(), attributes));
            super.addView(view, 0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean hasView() {
        return view != null;
    }

    public View getView() {
        return view;
    }
}
