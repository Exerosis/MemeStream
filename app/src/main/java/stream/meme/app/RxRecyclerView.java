package stream.meme.app;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.MutableInt;
import android.view.ViewGroup;

public class RxRecyclerView extends RecyclerView {
    public RxRecyclerView(Context context) {
        super(context);
        setAdapter(new Adapter() {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 0;

            }
        });
    }

    public RxRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RxRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    class DataBindingViewHolder extends ViewHolder {
        ViewDataBinding binding;
        public DataBindingViewHolder(@LayoutRes int layout) {
            super(binding = DataBindingUtil.inflate());
        }

    }
}
