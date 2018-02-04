package stream.meme.app.util.components.test;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import stream.meme.app.R;
import stream.meme.app.databinding.StringItemBinding;

import static android.view.LayoutInflater.from;

/**
 * Created by Exerosis on 2/1/2018.
 */

public class TestCustomView extends FrameLayout {
    public TestCustomView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
//        DataBindingUtil.inflate(from(context), R.layout., parent, false);
        StringItemBinding inflated = DataBindingUtil.inflate(from(context), R.layout.test_layout, this, false);
        inflated.textView.setText("test");
        addView(inflated.getRoot());
    }


}
