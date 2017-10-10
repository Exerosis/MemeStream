package stream.meme.app.util;

import com.google.common.base.Optional;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public final class Optionals {

    private Optionals() {

    }

    public static <T> Disposable ifPresent(Observable<Optional<T>> observable, Consumer<T> subscriber) {
        return observable.filter(Optional::isPresent).map(Optional::get).subscribe(subscriber);
    }
}