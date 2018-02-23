package stream.meme.app.util.components.superalpha;

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

import org.xmlpull.v1.XmlPullParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import stream.meme.app.R;
import stream.meme.app.databinding.TestInjectedLayoutBinding;

import static android.view.View.GONE;
import static android.view.View.NO_ID;

@SuppressLint("PrivateApi")
public class TestInflater extends LayoutInflater {
    private static final Method CREATE_VIEW_FROM_TAG;

    static {
        try {
            CREATE_VIEW_FROM_TAG = LayoutInflater.class.getDeclaredMethod(
                    "createViewFromTag", View.class,
                    String.class, Context.class,
                    AttributeSet.class, boolean.class
            );
            CREATE_VIEW_FROM_TAG.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final LayoutInflater original;
    private Integer layout = null;
    private AttributeSet lastAttributes = null;

    public TestInflater(LayoutInflater original, Context context) {
        super(original, context);
        this.original = original;

        //This isn't even really a view, it only needs to be one to appease the Android Studio Gods.
        //ie. if the tags name is a view it will auto complete, if not it will reach maximum levels of not helpful.
        final TestInjectedView injectedView = new TestInjectedView(context);
        injectedView.setVisibility(GONE);

        setFactory2(new Factory2() {
            @Override
            public View onCreateView(View viewGroup, String tag, Context context, AttributeSet attributes) {
                //Issue with this kind of id, is that if you have a different layout.xml for different orientations then
                //you will end up with two instances that it switches between. Maybe that's a good thing though who knows lol.
                //It could be solved by hijacking "transactionName" or making a new tag that serves as a link between two views in different layouts.
                String tagID = "res:" + layout + "line:" + ((XmlResourceParser) attributes).getLineNumber();
                //Here we should be totally free to use as much info as we want to do what's needed.


                if (lastAttributes != null) {
//                    attributes = lastAttributes;
                    lastAttributes = null;
                }

                //This won't quite work if there isn't a parent of some kind... so either find a work around or just make that a req.
                if (tag.equals(TestInjectedView.class.getName())) {
                    //Ok so this is the tag that triggers the inflater to inject custom content
                    ViewGroup parent = (ViewGroup) viewGroup;

                    View attributeManifestation = new View(context, attributes);

                    lastAttributes = attributes;
                    //So first step is to get our injected binding prepared using a copy of this inflater.(Although the original would work too I think)
                    View inflate = inflate(R.layout.test_injected_layout, parent, false);
                    TestInjectedLayoutBinding binding = TestInjectedLayoutBinding.bind(inflate);
                    //Sweet we got content that we want to inject into this tag, but we need to deal with the tags id before we return anything.

                    ViewGroup content = (ViewGroup) binding.getRoot();
                    //Now we can remove the ID from our injected content and prepare to return it.
                    injectedView.setId(attributeManifestation.getId());
                    content.setId(NO_ID);

                    //Now we can inject our completely undisplayed controller view into the parent without saying a word.
                    content.addView(injectedView);

                    return content;
                }


                //If not we return null and it falls back on the the outer classes onCreateView method.
                //If and only if it can't directly create an instance of the tag.(Stupid Android)
                try {
                    return (View) CREATE_VIEW_FROM_TAG.invoke(original, viewGroup, tag, context, attributes, false);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public View onCreateView(String tag, Context context, AttributeSet attributes) {
                //Just ignore this, we want to work with the parent most of the time anyway.
                return null;
            }
        });
    }

    @Override
    public View inflate(int resource, @Nullable ViewGroup root, boolean attachToRoot) {
        //Hold onto the previous value so we can restore it when we finish inflating this resource.
        Integer layout = this.layout;
        try {
            //When someone asks us to inflate a layout, we need to save the ID so we can use it
            //along with the line number to create a unique identifier for each custom tag.
            this.layout = resource;
            return super.inflate(resource, root, attachToRoot);
        } catch (InflateException e) {
            throw new InflateException("Did you register your component?", e);
        } finally {
            //As soon as we finish inflating we need to restore this value in case the next call
            //is either an old inflation finishing up, or someone trying to inflate directly
            //from a PullParser which we can't allow.
            this.layout = layout;
        }
    }

    @Override
    public View inflate(XmlPullParser parser, @Nullable ViewGroup root, boolean attachToRoot) {
        //If this method doesn't get called as a result of inflating a resource id we die.
        if (layout != null)
            return super.inflate(parser, root, attachToRoot);
        throw new UnsupportedOperationException("This layout inflater can only inflate layout resources!");
    }

    @Override
    public LayoutInflater cloneInContext(Context context) {
        return new TestInflater(original, context);
    }


    public static ContextWrapper inject(Context context) {
        return new ContextWrapper(context) {
            private TestInflater inflater;

            @Override
            public Object getSystemService(String name) {
                if (LAYOUT_INFLATER_SERVICE.equals(name)) {
                    if (inflater == null) {
                        inflater = new TestInflater(from(getBaseContext()), context);
                    }
                    return inflater;
                }
                return super.getSystemService(name);
            }
        };
    }
}
