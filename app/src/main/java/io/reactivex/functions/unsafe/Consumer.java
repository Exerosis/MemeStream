package io.reactivex.functions.unsafe;

import static stream.meme.app.util.Functions.runtime;

public interface Consumer<Type> extends io.reactivex.functions.Consumer<Type> {
    @Override
    void accept(Type type) throws Exception;

    default void acceptUnsafe(Type type) {
        try {
            accept(type);
        } catch (Exception e) {
            throw runtime(e);
        }
    }
}
