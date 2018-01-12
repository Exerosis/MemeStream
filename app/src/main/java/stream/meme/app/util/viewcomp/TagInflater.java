package stream.meme.app.util.viewcomp;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.XmlResourceParser;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParser;

public class TagInflater extends LayoutInflater {
    private static final String[] CLASS_PREFIX_LIST = {"android.widget.", "android.webkit.", "android.app."};
    private final TagRegistry registry;
    private Integer resource = null;
    private static ViewDelegate viewDelegate;

    public TagInflater(LayoutInflater original, Context context, TagRegistry registry) {
        super(original, context);
        this.registry = registry;
    }

    @Override
    protected View onCreateView(View parent, String tag, AttributeSet attributes) throws ClassNotFoundException {
        String id = "res:" + resource + "line:" + ((XmlResourceParser) attributes).getLineNumber();
        View view = registry.inflate(tag, id, getContext(), (ViewGroup) parent, attributes);
        if (view != null)
            return view;

        //Copied from Android PhoneLayoutInflater for inflating normal Android views.
        for (String prefix : CLASS_PREFIX_LIST) {
            try {
                view = createView(tag, prefix, attributes);
                if (view != null)
                    return view;
            } catch (ClassNotFoundException ignored) {

            }
        }
        return super.onCreateView(parent, tag, attributes);
    }

    @Override
    public View inflate(int resource, @Nullable ViewGroup root, boolean attachToRoot) {
        try {
            this.resource = resource;
            return super.inflate(resource, root, attachToRoot);
        } finally {
            this.resource = null;
        }
    }

    @Override
    public View inflate(XmlPullParser parser, @Nullable ViewGroup root, boolean attachToRoot) {
        if (resource != null)
            return super.inflate(parser, root, attachToRoot);
        throw new UnsupportedOperationException("This layout inflater can only inflate layout resources!");
    }

    @Override
    public LayoutInflater cloneInContext(Context context) {
        return new TagInflater(this, context, registry);
    }

    public static ContextWrapper inject(Context context, TagRegistry registry) {
        return new ContextWrapper(context) {
            private TagInflater inflater;

            @Override
            public Object getSystemService(String name) {
                if (LAYOUT_INFLATER_SERVICE.equals(name)) {
                    if (inflater == null)
                        inflater = new TagInflater(from(getBaseContext()), context, registry);
                    return inflater;
                }
                return super.getSystemService(name);
            }
        };
    }
}
