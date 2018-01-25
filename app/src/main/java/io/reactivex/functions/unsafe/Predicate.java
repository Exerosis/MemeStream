package io.reactivex.functions.unsafe;

import static stream.meme.app.util.Functions.runtime;

public interface Predicate<Type> extends io.reactivex.functions.Predicate<Type> {

    default boolean testUnsafe(Type type) {
        try {
            return test(type);
        } catch (Exception e) {
            throw runtime(e);
        }
    }

    default <To> Function<Type, To> map(To whenTrue, Function<Type, To> whenFalse) {
        return type -> test(type) ? whenTrue : whenFalse.apply(type);
    }

    default <To> Function<Type, To> map(To whenTrue, To whenFalse) {
        return type -> test(type) ? whenTrue : whenFalse;
    }

    default <To> Function<Type, To> map(Supplier<To> whenTrue, Supplier<To> whenFalse) {
        return type -> test(type) ? whenTrue.apply() : whenFalse.apply();
    }
}
