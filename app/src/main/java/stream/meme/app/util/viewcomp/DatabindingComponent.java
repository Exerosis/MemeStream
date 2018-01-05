package stream.meme.app.util.viewcomp;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * Created by Exerosis on 1/2/2018.
 */
public interface DatabindingComponent<ViewModel extends ViewDataBinding> extends ViewComponent<ViewModel> {
    @Override
    default ViewModel inflate(Context context, ViewGroup parent, AttributeSet attributes) {
        return DataBindingUtil.inflate(LayoutInflater.from(context), getLayout(attributes), parent, false);
    }

    @LayoutRes
    int getLayout(AttributeSet attributes);
}
