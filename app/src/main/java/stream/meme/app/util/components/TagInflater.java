package stream.meme.app.util.components;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.XmlResourceParser;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.xmlpull.v1.XmlPullParser;

import io.reactivex.functions.unsafe.TriFunction;

import static android.view.View.NO_ID;

public class TagInflater<Component extends View & TriFunction<Context, ViewGroup, AttributeSet, View>> extends LayoutInflater {
    private static final String[] CLASS_PREFIX_LIST = {"android.widget.", "android.webkit.", "android.app."};
    private final TagRegistry<Component> registry;
    private Integer layout = null;

    public TagInflater(LayoutInflater original, Context context, TagRegistry<Component> registry) {
        super(original, context);
        this.registry = registry;
        setFactory2(new Factory2() {
            @Override
            public View onCreateView(View parent, String tag, Context context, AttributeSet attributes) {
                //TODO override ID when custom attribute is present.
                String id = "res:" + layout + "line:" + ((XmlResourceParser) attributes).getLineNumber();
                Component component = registry.inflate(tag, id, getContext());
                if (component != null) {
                    View content = component.applyUnsafe(context, (ViewGroup) parent, attributes);
                    component.setId(content.getId());
                    content.setId(NO_ID);
                    if (content instanceof ViewGroup) {
                        ((ViewGroup) content).addView(component);
                        return content;
                    }
                    if (parent == null) {
                        parent = new FrameLayout(context);
                        ((ViewGroup) parent).addView(content);
                        ((ViewGroup) parent).addView(component);
                        return parent;
                    }
                    ((ViewGroup) parent).addView(component);
                    return content;
                }
                return null;
            }

            @Override
            public View onCreateView(String tag, Context context, AttributeSet attributes) {
                return null;
            }
        });
    }

    @Override
    protected View onCreateView(View parent, String tag, AttributeSet attributes) throws ClassNotFoundException {
        //Copied from Android PhoneLayoutInflater for inflating normal Android views.
        for (String prefix : CLASS_PREFIX_LIST) {
            try {
                View view = createView(tag, prefix, attributes);
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
            this.layout = resource;
            return super.inflate(resource, root, attachToRoot);
        } catch (InflateException e) {
            throw new InflateException("Did you register your component?", e);
        } finally {
            this.layout = null;
        }
    }

    @Override
    public View inflate(XmlPullParser parser, @Nullable ViewGroup root, boolean attachToRoot) {
        if (layout != null)
            return super.inflate(parser, root, attachToRoot);
        throw new UnsupportedOperationException("This layout inflater can only inflate layout resources!");
    }

    @Override
    public LayoutInflater cloneInContext(Context context) {
        return new TagInflater<>(this, context, registry);
    }

    public static <Component extends View & TriFunction<Context, ViewGroup, AttributeSet, View>> ContextWrapper inject(Context context, TagRegistry<Component> registry) {
        return new ContextWrapper(context) {
            private TagInflater inflater;

            @Override
            public Object getSystemService(String name) {
                if (LAYOUT_INFLATER_SERVICE.equals(name)) {
                    if (inflater == null)
                        inflater = new TagInflater<>(from(getBaseContext()), context, registry);
                    return inflater;
                }
                return super.getSystemService(name);
            }
        };
    }
}
