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

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static java.lang.String.format;
import static stream.meme.app.util.Functions.runtime;

//FIXME figure out how to clear out views when activites get destroyed.
@SuppressWarnings("JavaReflectionMemberAccess")
@SuppressLint("PrivateApi")
public class TagInflater<Component extends View & TriFunction<Context, ViewGroup, AttributeSet, View>> extends LayoutInflater {
    private static final Field PARENT;
    private static final Method CREATE_VIEW_FROM_TAG;
    private static final String ERR_CHILDREN = "Cannot add children to '%s' on line #%s";
    private static final String NAMESPACE = "http://schemas.android.com/apk/res-auto";
    private static final String ATTR_CHILD_ROOT = "child_root";
    public static final String ATTR_ID = "id";

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
                int line = ((XmlResourceParser) attributes).getLineNumber();
                String id = attributes.getAttributeValue(NAMESPACE, ATTR_ID);
                if (id == null)
                    id = "res:" + layout + "line:" + line;

                final View view;
                final Component component = registry.inflate(tag, id, getContext());
                if (component != null) {
                    //Inflate the components content.
                    final View content = component.applyUnsafe(context, (ViewGroup) parent, attributes);

                    //Search the content for a children tag to fill with children.
                    final ViewGroup children = content.findViewWithTag(ATTR_CHILD_ROOT);

                    //Create a holder to catch children and house our component.
                    view = new FrameLayout(component.getContext(), attributes) {
                        {
                            //Transfer the id to the component.
                            component.setId(getId());
                            setId(NO_ID);

                            //Add the component to the view.
                            component.setVisibility(GONE);
                            addViewInLayout(component, -1, generateDefaultLayoutParams(), true);

                            //Add the content to the view.
                            super.addView(content, -1, new LayoutParams(MATCH_PARENT, MATCH_PARENT));
                        }

                        @Override
                        public void addView(View child, int index, ViewGroup.LayoutParams params) {
                            //A child is being added, add it to the children holder or null.
                            if (children != null && children.getParent() != this)
                                children.addView(child, index, params);
                            else if (content instanceof ViewGroup)
                                ((ViewGroup) content).addView(child, index, params);
                            else
                                throw new IllegalStateException(format(ERR_CHILDREN, tag, line));
                        }

                        @Override
                        protected void onDetachedFromWindow() {
                            removeAllViews();
                            super.onDetachedFromWindow();
                        }
                    };
                } else {
                    //If not we return null and it falls back on the the outer classes onCreateView method.
                    //If and only if it can't directly create an instance of the tag.(Stupid Android)
                    try {
                        view = (View) CREATE_VIEW_FROM_TAG.invoke(original, parent, tag, context, attributes, false);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (attributes.getAttributeBooleanValue(NAMESPACE, ATTR_CHILD_ROOT, false))
                    view.setTag(ATTR_CHILD_ROOT);
                return view;
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
            layout = resource;
            return super.inflate(resource, root, attachToRoot);
        } catch (InflateException e) {
            throw new InflateException("Did you register your component?", e);
        } finally {
            layout = null;
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
