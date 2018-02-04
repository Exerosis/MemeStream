package stream.meme.app.util.components.test;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Consumer3;
import io.reactivex.functions.Function4;
import stream.meme.app.util.components.TagInflater;
import stream.meme.app.util.components.TagRegistry;

public class GlobalTagRegistry implements TagRegistry {
    private static GlobalTagRegistry instance;

    public static GlobalTagRegistry getInstance() {
        if (instance == null)
            instance = new GlobalTagRegistry();
        return instance;
    }

    public static ContextWrapper injectContext(Context context) {
        return TagInflater.inject(context, getInstance());
    }

    @SafeVarargs
    @Override
    public final <Tag extends View & Consumer3<Context, ViewGroup, AttributeSet>> void register(Class<? extends Tag>... types) {
        TagRegistry.super.register(types);
    }

    private final Map<String, Function4<String, Context, ViewGroup, AttributeSet, View>> inflaters = new HashMap<>();

    private GlobalTagRegistry() {

    }

    @Override
    public Map<String, Function4<String, Context, ViewGroup, AttributeSet, View>> getInflaters() {
        return inflaters;
    }
}
