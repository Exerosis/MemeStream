package stream.meme.app;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import stream.meme.app.stream.Meme;

public class MemeStream extends Application {
    public Observable<List<Meme>> loadMemes(int page) {
        return Observable.just(new ArrayList<>());
    }
}
