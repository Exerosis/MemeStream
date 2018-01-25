package stream.meme.app.util.viewcomp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import io.reactivex.functions.unsafe.Function;
import io.reactivex.functions.unsafe.Predicate;
import io.reactivex.functions.unsafe.Supplier;

public abstract class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    public static final int DEFAULT_TYPE = -1;
    private int currentType = 0;
    private Function<Integer, Integer> types = position -> DEFAULT_TYPE;
    private Function<Integer, ViewHolder> bindings = type -> {
        throw new IllegalStateException("Could not find a binding for type: " + type);
    };

    public void bind(Predicate<Integer> positions, Supplier<ViewHolder> binding) {
        int nextType = currentType++;
        types = positions.map(nextType, types);
        bindings = type -> {
            if (type == nextType)
                return binding.apply();
            return bindings.apply(type);
        };
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        return bindings.applyUnsafe(type);
    }

    @Override
    public int getItemViewType(int position) {
        return types.applyUnsafe(position);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);
        }

        abstract void bind(int position);
    }
}