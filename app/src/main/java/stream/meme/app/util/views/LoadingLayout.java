package stream.meme.app.util.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import io.reactivex.Observable;

/**
 * Created by Exerosis on 11/21/2017.
 */
public class LoadingLayout extends FrameLayout {

    public LoadingLayout(@NonNull Context context, @Nullable AttributeSet attributes) {
        super(context);
        addView(new ProgressBar(context, attributes), 0);
        loading(attributes.getAttributeBooleanValue("http://meme.stream.com/bivsc", "start", true));
    }

    public void loading(Observable<Boolean> loading) {
        loading.distinctUntilChanged().subscribe(this::loading);
    }

    public void loading(boolean loading) {
        getChildAt(0).setVisibility(loading ? VISIBLE : INVISIBLE);
        for (int i = 1; i < getChildCount(); i++)
            getChildAt(i).setVisibility(loading ? INVISIBLE : VISIBLE);
    }
}