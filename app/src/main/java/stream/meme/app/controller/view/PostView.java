package stream.meme.app.controller.view;

import android.content.Context;
import android.graphics.Bitmap;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import stream.meme.app.R;
import stream.meme.app.util.Nothing;

public class PostView extends AngularAndroidView<PostViewBinding> {
    public static final Boolean UP_VOTE = true;
    public static final Boolean NO_VOTE = null;
    public static final Boolean DOWN_VOTE = false;
    private Observable<Nothing> shareSubject;
    private Observable<Boolean> ratedSubject;
    private Observable<Object> title;
    private Observable<Object> subtitle;
    private Observable<Bitmap> image;

    public PostView(Context context) {
        super(context, R.layout.post_view);
    }

    @Override
    protected void bind(Observable<PostViewBinding> views) {
        shareSubject = views.switchMap(view -> RxView.clicks(view.share));
        ratedSubject = views.switchMap(view -> ...).startWith(NO_VOTE);

        views.subscribe(view -> {
            title.map(Object::toString).subscribe(RxTextView.text(view.title));
            subtitle.map(Object::toString).subscribe(RxTextView.text(view.subtitle));
            image.map(Object::toString).subscribe(RxTextView.text(view.image));
        });
    }


    public void title(@Clairfiers.Text Observable<Object> title){
        this.title = title;
    }

    public void subtitle(@Clairfiers.Text Observable<Object> subtitle){
        this.subtitle = subtitle;
    }

    public void image(Observable<Bitmap> image){
        this.image = image;
    }

    public Observable<Nothing> shareClicked() {
        return shareSubject;
    }

    public Observable<Boolean> rated() {
        return ratedSubject;
    }
}