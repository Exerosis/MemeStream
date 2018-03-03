package stream.meme.app.util.components.components;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import stream.meme.app.util.components.TagRegistry;

import static stream.meme.app.util.components.TagInflater.inject;

public class ComponentActivity<ViewModel extends ViewDataBinding> extends AppCompatActivity {
    private final Subject<ViewModel> components = BehaviorSubject.create();
    private final int layout;
    private TagRegistry<ViewComponent<?>> registry;

    public ComponentActivity(@LayoutRes int layout, TagRegistry<ViewComponent<?>> registry) {
        this.layout = layout;
        this.registry = registry;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(inject(base, registry));
    }

    @Override
    protected void onCreate(@Nullable Bundle in) {
        super.onCreate(in);
        ViewModel viewModel = DataBindingUtil.inflate(getLayoutInflater(), layout, null, false);
        components.onNext(viewModel);
        setContentView(viewModel.getRoot());
    }

    public void getComponents(Consumer<ViewModel> callback) {
        getComponents().subscribe(callback);
    }

    public Observable<ViewModel> getComponents() {
        return components;
    }
}