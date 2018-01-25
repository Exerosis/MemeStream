package io.reactivex.functions.unsafe;

import static stream.meme.app.util.Functions.runtime;

public interface Supplier<Type> extends com.google.common.base.Supplier<Type> {
    Type apply() throws Exception;


    @Override
    default Type get() {
        return applyUnsafe();
    }

    default Type applyUnsafe() {
        try {
            return apply();
        } catch (Exception e) {
            throw runtime(e);
        }
    }
}
