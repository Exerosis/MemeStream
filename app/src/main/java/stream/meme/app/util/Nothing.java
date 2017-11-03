package stream.meme.app.util;

import io.reactivex.Observable;

/**
 * Created by Exerosis on 10/31/2017.
 */
public final class Nothing {
    public static Nothing NONE = new Nothing();

    private Nothing() {

    }

    public static Observable<Nothing> ignored(Observable<Object> observable) {
        return observable.map(ignored -> NONE);
    }

    public static <T> Observable<T> always(T value, Observable<?> observable) {
        return observable.map(ignored -> value);
    }
}