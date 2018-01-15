package stream.meme.app.util.viewcomp;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.ViewGroup;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Consumer3;
import io.reactivex.subjects.BehaviorSubject;

import static android.view.LayoutInflater.from;


public abstract class ViewComponent<ViewModel extends ViewDataBinding> extends ViewDelegate implements Consumer3<Context, ViewGroup, AttributeSet> {
    private final BehaviorSubject<ViewModel> binding = BehaviorSubject.create();
    private boolean setup = true;

    public ViewComponent(@NonNull Context context) {
        super(context);
    }

    //TODO ehhh I don't like this much not gonna lie.
    protected boolean setupOnly(Runnable runnable) {
        if (!setup)
            return false;
        runnable.run();
        setup = false;
        return true;
    }

    public Observable<ViewModel> getViews() {
        return binding;
    }

    public Maybe<ViewModel> getComponents() {
        return binding.firstElement();
    }

    public void getComponents(Consumer<ViewModel> components) {
        try {
            if (binding.hasValue())
                components.accept(binding.getValue());
            else
                getComponents().subscribe(components);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @LayoutRes
    protected abstract int inflate(@NonNull AttributeSet attributes);

    @Override
    public void apply(Context context, ViewGroup parent, AttributeSet attributes) throws Exception {
        ViewModel viewModel = DataBindingUtil.inflate(from(context), inflate(attributes), parent, false);
        setView(viewModel.getRoot());
        binding.onNext(viewModel);
    }
}