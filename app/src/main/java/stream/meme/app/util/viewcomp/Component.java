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
import io.reactivex.functions.Function3;
import io.reactivex.subjects.Subject;

import static android.view.LayoutInflater.from;
import static io.reactivex.subjects.BehaviorSubject.create;


//LOL ;) Not really a view, but we shall pretend because Android is a pile of stupidity.
//TODO find a way to make this not really so... viewy...
public abstract class Component<ViewModel extends ViewDataBinding> extends View implements Function3<Context, ViewGroup, AttributeSet, View> {
    private final Subject<ViewModel> binding = create();

    public Component() {
        super(null);
    }

    protected Observable<ViewModel> getViews() {
        return binding;
    }

    @LayoutRes
    protected abstract int inflate(@NonNull AttributeSet attributes);

    @Override
    public View apply(Context context, ViewGroup parent, AttributeSet attributes) throws Exception {
        ViewModel view = DataBindingUtil.inflate(from(context), inflate(attributes), parent, false);
        binding.onNext(view);
        return view.getRoot();
    }
}