package stream.meme.app.util.viewcomp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Consumer3;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function4;

import static net.jodah.typetools.TypeResolver.resolveRawArgument;

public interface TagRegistry {
    Map<String, Function4<String, Context, ViewGroup, AttributeSet, View>> getInflaters();

    default <Tag extends View & Consumer3<Context, ViewGroup, AttributeSet>> void register(String name, Function<Context, Tag> inflaters) {
        final Map<String, Tag> tags = new HashMap<>();
        getInflaters().put(name, (id, context, parent, attributes) -> {
            Tag tag = tags.get(id);
            if (tag == null) {
                tag = inflaters.apply(context);
                tags.put(id, tag);
            }
            tag.apply(context, parent, attributes);
            return tag;
        });
    }

    @SuppressWarnings("unchecked")
    default <Tag extends View & Consumer3<Context, ViewGroup, AttributeSet>> void register(Class<? extends Tag>... types) {
        for (Class<? extends Tag> type : types)
            register(type.getName(), context -> type.getConstructor(Context.class).newInstance(context));
    }

    default <Tag extends View & Consumer3<Context, ViewGroup, AttributeSet>> void register(Function<Context, Tag> inflaters) {
        register(resolveRawArgument(Function.class, inflaters.getClass()).getName(), inflaters);
    }

    default View inflate(String tag, String id, Context context, ViewGroup parent, AttributeSet attributes) {
        try {
            return getInflaters().get(tag).apply(id, context, parent, attributes);
        } catch (Exception e) {
            return null;
        }
    }
}
