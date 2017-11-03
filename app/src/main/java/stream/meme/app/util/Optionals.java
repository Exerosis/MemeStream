package stream.meme.app.util;

import com.google.common.base.Optional;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public final class Optionals {

    private Optionals() {

    }

    public static <T> void ifPresent(Optional<T> optional, Consumer<T> callback) {
        if (optional.isPresent())
            try {
                callback.accept(optional.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public static <T> Observable<T> ifPresent(Observable<Optional<T>> observable) {
        return observable.filter(Optional::isPresent).map(Optional::get);
    }
    public static <T> Disposable ifPresent(Observable<Optional<T>> observable, Consumer<T> subscriber) {
        return ifPresent(observable).subscribe(subscriber);
    }
}