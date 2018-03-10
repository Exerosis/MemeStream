package stream.meme.app.util.components.components;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import static android.databinding.DataBindingUtil.inflate;

public abstract class ComponentFragment<ViewModel extends ViewDataBinding> extends Fragment {
    private final Subject<ViewModel> views = BehaviorSubject.create();
    private final int layout;

    public ComponentFragment(@LayoutRes int layout) {
        this.layout = layout;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle in) {
        ViewModel view = inflate(inflater, layout, parent, false);
        views.onNext(view);
        return view.getRoot();
    }

    public ComponentFragment<ViewModel> getComponents(Consumer<ViewModel> callback) {
        views.subscribe(callback);
        return this;
    }

    public Observable<ViewModel> getComponents() {
        return views;
    }
}
