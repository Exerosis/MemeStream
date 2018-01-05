package stream.meme.app.util.viewcomp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import io.reactivex.Observable;

/**
 * Created by Exerosis on 1/1/2018.
 */
public interface ViewComponent<ViewModel> {
    ViewModel inflate(Context context, ViewGroup parent, AttributeSet attributes);

    void bind(Observable<ViewModel> views);

    //TODO maybe this isn't such a good idea :|
    default String name() {
        return getClass().getSimpleName();
    }
}