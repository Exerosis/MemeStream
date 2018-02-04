package stream.meme.app.util;

import android.support.v4.util.Pair;

/**
 * Created by Exerosis on 1/25/2018.
 */

public class Functions {

    public static RuntimeException runtime(Throwable exception) {
        if (exception instanceof RuntimeException)
            return (RuntimeException) exception;
        return new RuntimeException(exception);
    }

    public static <First, Second> Pair<First, Second> of(First first, Second second) {
        return new Pair<>(first, second);
    }
}
