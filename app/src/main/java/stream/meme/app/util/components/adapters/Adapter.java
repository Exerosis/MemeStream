package stream.meme.app.util.components.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import io.reactivex.functions.Predicate;
import io.reactivex.functions.unsafe.BiFunction;
import io.reactivex.functions.unsafe.Function;

public abstract class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    public static final int DEFAULT_TYPE = 0;
    private int currentType = DEFAULT_TYPE;
    private Function<Integer, Integer> types = position -> DEFAULT_TYPE;
    private BiFunction<ViewGroup, Integer, ViewHolder> bindings = (parent, type) -> {
       return null;
    };

    public int bind(Predicate<Integer> positions, Function<ViewGroup, ViewHolder> binding) {
        int nextType = currentType++;
        types = types.returnWhen(nextType, positions);
        bindings = bindings.whenNotNull((parent, type) -> {
            if (type == nextType)
                return binding.apply(parent);
            return null;
        });
        return nextType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        return bindings.applyUnsafe(parent, type);
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