package io.reactivex.functions.unsafe;

import static stream.meme.app.util.Functions.runtime;

public interface Action extends io.reactivex.functions.Action {
    @Override
    void run() throws Exception;

    default void runUnsafe() {
        try {
            run();
        } catch (Exception e) {
            throw runtime(e);
        }
    }
}
