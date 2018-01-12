package stream.meme.app.util.viewcomp;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer3;
import io.reactivex.subjects.Subject;

import static android.view.LayoutInflater.from;
import static io.reactivex.subjects.BehaviorSubject.create;


public abstract class Component<ViewModel extends ViewDataBinding> extends ViewDelegate implements Consumer3<Context, ViewGroup, AttributeSet> {
    private final Subject<ViewModel> binding = create();

    public Component(@NonNull Context context) {
        super(context);
    }

    protected Observable<ViewModel> getViews() {
        return binding;
    }

    @LayoutRes
    protected abstract int inflate(@NonNull AttributeSet attributes);

    @Override
    public void apply(Context context, ViewGroup parent, AttributeSet attributes) throws Exception {
        ViewModel viewModel = DataBindingUtil.inflate(from(context), inflate(attributes), parent, false);

        View view = viewModel.getRoot();
        setId(view.getId());
        view.setId(NO_ID);
        setView(view);

        binding.onNext(viewModel);
    }
}