package stream.meme.app.util.bivsc;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public abstract class DatabindingBIVSCModule<View extends ViewDataBinding, State> extends BIVSCModule<View, State> {
    @LayoutRes
    private final int layout;

    public DatabindingBIVSCModule(@LayoutRes int layout) {
        this.layout = layout;
    }

    @NonNull
    @Override
    protected android.view.View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = DataBindingUtil.inflate(inflater, layout, container, false);
        viewModel.onNext(view);
        return view.getRoot();
    }
}