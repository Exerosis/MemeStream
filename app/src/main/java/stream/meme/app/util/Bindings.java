package stream.meme.app.util;

import android.databinding.BindingAdapter;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import stream.meme.app.BR;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.databinding.DataBindingUtil.inflate;

/**
 * Created by Exerosis on 10/17/2017.
 */

public class Bindings {

    @BindingAdapter({"entries", "layout"})
    public static <T> void setEntries(ViewGroup viewGroup, List<T> entries, int layoutId) {
        viewGroup.removeAllViews();
        if (entries != null) {
            LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            for (int i = 0; i < entries.size(); i++) {
                T entry = entries.get(i);
                ViewDataBinding binding = inflate(inflater, layoutId, viewGroup, true);
                binding.setVariable(BR.data, entry);
            }
        }
    }

    @BindingAdapter({"src"})
    public static void loadImage(ImageView imageView, String url) {
        Picasso.with(imageView.getContext()).load(url).into(imageView);
    }

    @BindingAdapter({"src"})
    public static void loadImage(ImageView imageView, Bitmap image) {
        imageView.setImageBitmap(image);
    }
}
