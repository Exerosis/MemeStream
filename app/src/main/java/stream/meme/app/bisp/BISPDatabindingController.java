package stream.meme.app.bisp;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public abstract class BISPDatabindingController<Intents, View extends ViewDataBinding, State> extends BISPController<Intents, View, State> {
    private View binding;

    public BISPDatabindingController(@LayoutRes int layout) {
        super(layout);
    }

    @NonNull
    @Override
    protected android.view.View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, layout, container, false);
        return binding.getRoot();
    }

    @Override
    public View getViewHolder() {
        return binding;
    }
}
