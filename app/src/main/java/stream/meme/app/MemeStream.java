package stream.meme.app;

import android.app.Application;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import stream.meme.app.stream.Meme;

public class MemeStream extends Application {
    public Observable<List<Meme>> loadMemes(int page) {
        return Observable.just(Arrays.asList(
                new Meme(UUID.randomUUID(), "test0", "page " + page, "https://i.vimeocdn.com/portrait/58832_300x300"),
                new Meme(UUID.randomUUID(), "test1", "page " + page, "https://i.vimeocdn.com/portrait/58832_300x300"),
                new Meme(UUID.randomUUID(), "test2", "page " + page, "https://i.vimeocdn.com/portrait/58832_300x300"),
                new Meme(UUID.randomUUID(), "test3", "page " + page, "https://i.vimeocdn.com/portrait/58832_300x300"),
                new Meme(UUID.randomUUID(), "test4", "page " + page, "https://i.vimeocdn.com/portrait/58832_300x300"),
                new Meme(UUID.randomUUID(), "test5", "page " + page, "https://i.vimeocdn.com/portrait/58832_300x300")))
                .delay(2, TimeUnit.SECONDS);
    }
}
