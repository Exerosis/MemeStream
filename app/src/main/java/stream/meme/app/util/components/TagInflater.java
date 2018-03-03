package stream.meme.app.util.components;

import android.annotation.SuppressLint;
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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.reactivex.functions.unsafe.TriFunction;

import static android.view.View.*;
import static stream.meme.app.util.Functions.runtime;

//FIXME figure out how to clear out views when activites get destroyed.
@SuppressLint("PrivateApi")
public class TagInflater<Component extends View & TriFunction<Context, ViewGroup, AttributeSet, View>> extends LayoutInflater {
    private static final Field PARENT;
    private static final Method CREATE_VIEW_FROM_TAG;

    static {
        try {
            PARENT = View.class.getDeclaredField("mParent");
            PARENT.setAccessible(true);
            CREATE_VIEW_FROM_TAG = LayoutInflater.class.getDeclaredMethod(
                    "createViewFromTag", View.class,
                    String.class, Context.class,
                    AttributeSet.class, boolean.class
            );
            CREATE_VIEW_FROM_TAG.setAccessible(true);
        } catch (Exception e) {
            throw runtime(e);
        }
    }

    private final TagRegistry<Component> registry;
    private final LayoutInflater original;
    private Integer layout = null;

    public TagInflater(LayoutInflater original, Context context, TagRegistry<Component> registry) {
        super(original, context);
        this.original = original;
        this.registry = registry;
        setFactory2(new Factory2() {
            @Override
            public View onCreateView(View parent, String tag, Context context, AttributeSet attributes) {
                //TODO override ID when custom attribute is present.
                String id = "res:" + layout + "line:" + ((XmlResourceParser) attributes).getLineNumber();
                Component component = registry.inflate(tag, id, getContext());
                if (component != null) {
                    try {
                        PARENT.set(component, null);
                    } catch (IllegalAccessException e) {
                        throw runtime(e);
                    }
                    int idManifestation = new View(context, attributes).getId();
                    View content = component.applyUnsafe(context, (ViewGroup) parent, attributes);

                    //--Id--
                    component.setId(idManifestation);
                    if (idManifestation != NO_ID) {
                        if (parent == null)
                            throw new IllegalStateException("ViewComponents must have parents to have an id.");
                        ((ViewGroup) parent).addView(component);
                        return content;
                    }
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
                }


                //If not we return null and it falls back on the the outer classes onCreateView method.
                //If and only if it can't directly create an instance of the tag.(Stupid Android)
                try {
                    return (View) CREATE_VIEW_FROM_TAG.invoke(original, parent, tag, context, attributes, false);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public View onCreateView(String tag, Context context, AttributeSet attributes) {
                return null;
            }
        });
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
        return new TagInflater<>(original, context, registry);
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
