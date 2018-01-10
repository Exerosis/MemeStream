package io.reactivex.functions;

/**
 * Created by Exerosis on 1/10/2018.
 */

public interface Supplier<Type> {
    Type apply() throws Exception;
}
