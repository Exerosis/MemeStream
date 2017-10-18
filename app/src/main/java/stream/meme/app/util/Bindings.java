package stream.meme.app.util;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import stream.meme.app.BR;

/**
 * Created by Exerosis on 10/17/2017.
 */

public class Bindings {

    @BindingAdapter({"entries", "layout"})
    public static <T> void setEntries(ViewGroup viewGroup, List<T> entries, int layoutId) {
        viewGroup.removeAllViews();
        if (entries != null) {
            LayoutInflater inflater = (LayoutInflater)
                    viewGroup.getContext()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0; i < entries.size(); i++) {
                T entry = entries.get(i);
                ViewDataBinding binding = DataBindingUtil
                        .inflate(inflater, layoutId, viewGroup, true);
                binding.setVariable(BR.data, entry);
            }
        }
    }
}
