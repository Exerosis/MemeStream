package stream.meme.app.util.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.XmlResourceParser;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParser;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import io.reactivex.functions.unsafe.TriFunction;

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
    private final Stack<Pair<ViewGroup, List<View>>> children = new Stack<>();
    private final LayoutInflater original;
    private Integer layout = null;
    private ComponentRoot root;


    class ComponentInflater {
        ComponentInflater(LayoutInflater original) {

        }
    }

    static class ComponentRoot extends ViewGroup {
        static final int TAG_COMPONENT = 1999;
        static final Object TAG = 1;

        public ComponentRoot(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            if (changed)
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    if (child.getTag(TAG_COMPONENT) == TAG)
                        child.layout(l, t, r, b);
                }
        }

        @Override
        protected void onDetachedFromWindow() {
            removeAllViews();
            super.onDetachedFromWindow();
        }

        public void addComponent(View component) {
            try {
                PARENT.set(component, null);
            } catch (IllegalAccessException e) {
                throw runtime(e);
            }
            component.setTag(TAG_COMPONENT, TAG);
            addViewInLayout(component, -1, generateDefaultLayoutParams(), true);
        }
    }

    public TagInflater(LayoutInflater original, Context context, TagRegistry<Component> registry) {
        super(original, context);
        this.original = original;
        this.registry = registry;
        setFactory2(new Factory2() {
            @Override
            public View onCreateView(View parent, String tag, Context context, AttributeSet attributes) {
                if (tag.equals("component"))
                    return root = new ComponentRoot(context, attributes);

                if (tag.equals("children"))
                    return new ViewGroup(context, attributes) {
                        {
                            Pair<ViewGroup, List<View>> pair = TagInflater.this.children.pop();
                            for (View child : pair.second) {
                                pair.first.removeView(child);
                                addView(child);
                            }
                        }

                        @Override
                        protected void onLayout(boolean changed, int l, int t, int r, int b) {
                            if (changed)
                                for (int i = 0; i < getChildCount(); i++) {
                                    View child = getChildAt(i);
                                    child.layout(l, t, r, b);
                                }
                        }
                    };

                //TODO override ID when custom attribute is present.
                String id = "res:" + layout + "line:" + ((XmlResourceParser) attributes).getLineNumber();

                View content;

                Component component = registry.inflate(tag, id, getContext());
                if (component != null) {
                    if (root == null)
                        throw new IllegalStateException("Components must be used within a <component /> tag.");

                    //Manifest an ID from the current attribute set and assign it to the component.
                    int idManifestation = new View(context, attributes).getId();
                    component.setId(idManifestation);

                    //Add the component to the component root.
                    root.addComponent(component);


                    //Inflate the components content.
                    content = component.applyUnsafe(context, (ViewGroup) parent, attributes);

                    //Create an empty list to hold children until we find a place for them.
                    children.push(new Pair<>((ViewGroup) content, new ArrayList<>()));
                }


                //If not we return null and it falls back on the the outer classes onCreateView method.
                //If and only if it can't directly create an instance of the tag.(Stupid Android)
                try {
                    content = (View) CREATE_VIEW_FROM_TAG.invoke(original, parent, tag, context, attributes, false);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }

                if (children.peek().first == parent)
                    children.peek().second.add(content);

                return content;
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
            root = null;
            layout = null;
            children.clear();
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
