package stream.meme.app.util.bivsc;

import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import static android.databinding.DataBindingUtil.getBinding;
import static android.databinding.DataBindingUtil.inflate;

public abstract class DatabindingBIVSCModule<View extends ViewDataBinding, State> extends BIVSCModule<View, State> {
    @LayoutRes
    private final int layout;

    public DatabindingBIVSCModule(@LayoutRes int layout) {
        this.layout = layout;
    }

    @NonNull
    @Override
    protected android.view.View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflate(inflater, layout, container, false).getRoot();
    }

    @Override
    protected void onAttach(@NonNull android.view.View view) {
        super.onAttach(view);
        viewModel.onNext(getBinding(view));
    }
}