package io.reactivex.functions.unsafe;


import io.reactivex.annotations.NonNull;

import static stream.meme.app.util.Functions.runtime;

public interface BiFunction<First, Second, To> extends io.reactivex.functions.BiFunction<First, Second, To> {

    default To applyUnsafe(@NonNull First first, @NonNull Second second) {
        try {
            return apply(first, second);
        } catch (Exception e) {
            throw runtime(e);
        }
    }

    default BiFunction<First, Second, To> whenNotNull(BiFunction<First, Second, To> when) {
        return (first, second) -> {
            To to = when.apply(first, second);
            if (to != null)
                return to;
            return apply(first, second);
        };
    }

    default <Final> BiFunction<First, Second, Final> andThen(Function<To, Final> next) {
        return (first, second) -> next.apply(apply(first, second));
    }
}
