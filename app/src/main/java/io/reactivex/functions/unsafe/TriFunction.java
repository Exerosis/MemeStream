package io.reactivex.functions.unsafe;

import static stream.meme.app.util.Functions.runtime;

public interface TriFunction<First, Second, Third, Return> {
    Return apply(First first, Second second, Third third) throws Exception;

    default Return applyUnsafe(First first, Second second, Third third) {
        try {
            return apply(first, second, third);
        } catch (Exception e) {
            throw runtime(e);
        }
    }
}
