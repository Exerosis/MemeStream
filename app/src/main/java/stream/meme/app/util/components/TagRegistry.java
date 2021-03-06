package stream.meme.app.util.components;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Function;
import io.reactivex.functions.unsafe.BiFunction;

import static net.jodah.typetools.TypeResolver.resolveRawArgument;

public interface TagRegistry<Component> {
    Map<String, BiFunction<String, Context, Component>> getInflaters();

    default void register(String tag, Function<Context, Component> inflater) {
        final Map<String, Component> components = new HashMap<>();
        getInflaters().put(tag, (id, context) -> {
            if (context == null)
                return components.remove(id);
            Component component = components.get(id);
            if (component == null) {
                component = inflater.apply(context);
                components.put(id, component);
            }
            return component;
        });
    }

    default void register(Class<? extends Component>... types) {
        for (Class<? extends Component> type : types)
            register(type.getName(), context -> type.getConstructor(Context.class).newInstance(context));
    }

    default void register(Function<Context, Component> inflaters) {
        register(resolveRawArgument(Function.class, inflaters.getClass()).getName(), inflaters);
    }

    default Component destroy(String tag, String id) {
        return inflate(tag, id, null); //Not an overload, this is just how we clear views.s
    }

    default Component inflate(String tag, String id, Context context) {
        BiFunction<String, Context, Component> compressed = getInflaters().get(tag);
        return compressed == null ? null : compressed.applyUnsafe(id, context);
    }
}
