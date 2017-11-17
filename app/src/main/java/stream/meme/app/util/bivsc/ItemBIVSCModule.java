package stream.meme.app.util.bivsc;

import android.databinding.ViewDataBinding;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public abstract class ItemBIVSCModule<View extends ViewDataBinding, Data> implements BIVSC<View, Data>, BiConsumer<View, Observable<Data>> {
    private final Subject<View> views = PublishSubject.create();
    private Observable<Data> controller = null;

    @Override
    public Observable<Data> getController() {
        return controller;
    }

    @Override
    public void accept(View view, Observable<Data> data) throws Exception {
        if (controller == null) {
            getBinder().accept(views, controller = data);
        }
        views.onNext(view);
    }
}