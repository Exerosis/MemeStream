package stream.meme.app.util.components.components;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.ViewGroup;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Consumer3;
import io.reactivex.subjects.BehaviorSubject;
import stream.meme.app.util.components.ViewDelegate;

import static android.view.LayoutInflater.from;
import static stream.meme.app.util.Functions.runtime;


public class ViewComponent<ViewModel extends ViewDataBinding> extends ViewDelegate implements Consumer3<Context, ViewGroup, AttributeSet> {
    private final BehaviorSubject<ViewModel> binding = BehaviorSubject.create();
    private final int layout;

    public ViewComponent(@NonNull Context context) {
        this(context, -1);
    }

    public ViewComponent(@NonNull Context context, @LayoutRes int layout) {
        super(context);
        this.layout = layout;
    }

    public Observable<ViewModel> getViews() {
        return binding;
    }

    public void getComponents(Consumer<ViewModel> components) {
        try {
            if (binding.hasValue())
                components.accept(binding.getValue());
            else
                binding.firstElement().subscribe(components);
        } catch (Exception e) {
            throw runtime(e);
        }
    }

    @LayoutRes
    protected int inflate(@NonNull AttributeSet attributes) {
        if (layout == -1)
            throw new IllegalStateException("View was not set!");
        return layout;
    }

    @Override
    public void apply(Context context, ViewGroup parent, AttributeSet attributes) throws Exception {
        ViewModel viewModel = DataBindingUtil.inflate(from(context), inflate(attributes), parent, false);
        resetView(viewModel.getRoot(), attributes);
        binding.onNext(viewModel);
    }
}