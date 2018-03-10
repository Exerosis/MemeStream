package stream.meme.app.util.components.test;

import android.content.Context;
import android.content.ContextWrapper;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.unsafe.BiFunction;
import stream.meme.app.util.components.TagInflater;
import stream.meme.app.util.components.TagRegistry;
import stream.meme.app.util.components.components.ViewComponent;

public class GlobalTagRegistry implements TagRegistry<ViewComponent<?>> {
    private static GlobalTagRegistry instance;

    public static GlobalTagRegistry getInstance() {
        if (instance == null)
            instance = new GlobalTagRegistry();
        return instance;
    }

    public static ContextWrapper injectContext(Context context) {
        return TagInflater.inject(context, getInstance());
    }

    private final Map<String, BiFunction<String, Context, ViewComponent<?>>> inflaters = new HashMap<>();

    private GlobalTagRegistry() {

    }

    @Override
    @SafeVarargs
    public final void register(Class<? extends ViewComponent<?>>... types) {
        TagRegistry.super.register(types);
    }

    @Override
    public Map<String, BiFunction<String, Context, ViewComponent<?>>> getInflaters() {
        return inflaters;
    }
}
