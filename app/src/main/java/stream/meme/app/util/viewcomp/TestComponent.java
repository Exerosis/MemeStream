package stream.meme.app.util.viewcomp;

import android.util.AttributeSet;

import com.jakewharton.rxbinding2.view.RxView;

import io.reactivex.Observable;
import stream.meme.app.R;
import stream.meme.app.databinding.StreamViewBinding;
import stream.meme.app.util.Nothing;
import stream.meme.app.util.views.LoadingLayout;

import static stream.meme.app.util.Operators.ignored;

/**
 * Created by Exerosis on 1/2/2018.
 */
public class TestComponent implements DatabindingComponent<StreamViewBinding> {
    private Observable<Nothing> clicks;

    @Override
    public void bind(Observable<StreamViewBinding> views) {
        //Map changes in view to fields to be accessed from other views.
        clicks = views.flatMap(view -> RxView.clicks(view.getRoot()))
                .compose(ignored());

        //Make view changes here, emits whenever the configuration changes.
        views.subscribe(view -> {
            LoadingLayout loadingLayout = view.loadingLayout;
            loadingLayout.loading(true);
            //Further view work here!
        });
    }

    public Observable<Nothing> onClick() {
        return clicks;
    }

    @Override
    public int getLayout(AttributeSet attributes) {
        //Handle any attribs this view has and return the 'content'.
        return R.layout.stream_view;
    }

    @Override
    public String name() {
        //Return whatever XML tag you want to associate this view with, defaults to the classes simple name.
        return DatabindingComponent.super.name();
    }
}