package io.reactivex.functions.unsafe;

import io.reactivex.annotations.NonNull;

import static stream.meme.app.util.Functions.runtime;

public interface Function<From, To> extends io.reactivex.functions.Function<From, To> {
    /**
     * Apply some calculation to the input value and return some other value. Throw a runtime exception on errors.
     *
     * @param from the input value
     * @return the output value
     */
    default To applyUnsafe(@NonNull From from) {
        try {
            return apply(from);
        } catch (Exception e) {
            throw runtime(e);
        }
    }

    default Function<From, To> returnWhen(To value, io.reactivex.functions.Predicate<From> when) {
        return from -> when.test(from) ? value : apply(from);
    }

    default <Final> Function<From, Final> andThen(Function<To, Final> next) {
        return from -> next.apply(apply(from));
    }
}
