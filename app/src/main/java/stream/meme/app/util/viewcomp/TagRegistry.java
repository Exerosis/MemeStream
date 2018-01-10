package stream.meme.app.util.viewcomp;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Function3;
import io.reactivex.functions.Function4;
import io.reactivex.functions.Supplier;

import static net.jodah.typetools.TypeResolver.resolveRawArgument;


public interface TagRegistry {

    Map<String, Function4<String, Context, ViewGroup, AttributeSet, View>> getInflaters();

    default View inflate(String tag, String id, Context context, ViewGroup parent, AttributeSet attributes) {
        try {
            return getInflaters().get(tag).apply(id, context, parent, attributes);
        } catch (Exception e) {
            return null;
        }
    }

    default void register(Class<? extends Function3<Context, ViewGroup, AttributeSet, View>>[] types) {
        for (Class<? extends Function3<Context, ViewGroup, AttributeSet, View>> type : types)
            register(type.getName(), type::newInstance);
    }

    default void register(Supplier<Function3<Context, ViewGroup, AttributeSet, View>> inflaters) {
        register(resolveRawArgument(Supplier.class, inflaters.getClass()).getName(), inflaters);
    }

    default void register(String tag, Supplier<Function3<Context, ViewGroup, AttributeSet, View>> inflaters) {
        final Map<String, Function3<Context, ViewGroup, AttributeSet, View>> cache = new HashMap<>();
        getInflaters().put(tag, (id, context, parent, attributes) -> {
            Function3<Context, ViewGroup, AttributeSet, View> inflater = cache.get(id);
            if (inflater == null) {
                inflater = inflaters.apply();
                cache.put(id, inflater);
            }
            return inflater.apply(context, parent, attributes);
        });
    }
}