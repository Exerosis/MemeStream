package stream.meme.app.util;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;

import static stream.meme.app.util.Nothing.NONE;

public interface Operators {

    //--Ignored
    static Observable<Nothing> ignored(Observable<Object> observable) {
        return observable.compose(ignored());
    }

    static ObservableTransformer<Object, Nothing> ignored() {
        return always(NONE);
    }

    //--Always--
    static <Type> Observable<Type> always(Type value, Observable<Object> observable) {
        return observable.compose(always(value));
    }

    static <Type> ObservableTransformer<Object, Type> always(Supplier<Type> value) {
        return upstream -> upstream.map(ignored -> value.get());
    }

    static <Type> ObservableTransformer<Object, Type> always(Type value) {
        return upstream -> upstream.map(ignored -> value);
    }

    //--If Present--
    static <Type> Observable<Type> ifPresent(Observable<Optional<Type>> observable) {
        return observable.compose(ifPresent());
    }

    static <Type> ObservableTransformer<Optional<Type>, Type> ifPresent() {
        return upstream -> upstream.filter(Optional::isPresent).map(Optional::get);
    }
}
