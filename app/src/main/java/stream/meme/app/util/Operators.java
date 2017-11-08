package stream.meme.app.util;

import com.google.common.base.Optional;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;

import static stream.meme.app.util.Nothing.NONE;

/**
 * Created by Exerosis on 11/8/2017.
 */
public class Operators {

    //--Ignored
    public static Observable<Nothing> ignored(Observable<Object> observable) {
        return observable.compose(ignored());
    }

    public static ObservableTransformer<Object, Nothing> ignored() {
        return always(NONE);
    }

    //--Always--
    public static <Type> Observable<Type> always(Type value, Observable<Object> observable) {
        return observable.compose(always(value));
    }

    public static <Type> ObservableTransformer<Object, Type> always(Type value) {
        return upstream -> upstream.map(ignored -> value);
    }

    //--If Present--
    public static <Type> Observable<Type> ifPresent(Observable<Optional<Type>> observable) {
        return observable.compose(ifPresent());
    }

    public static <Type> ObservableTransformer<Optional<Type>, Type> ifPresent() {
        return upstream -> upstream.filter(Optional::isPresent).map(Optional::get);
    }
}
