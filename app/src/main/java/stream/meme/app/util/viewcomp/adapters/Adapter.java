package stream.meme.app.util.viewcomp.adapters;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Method;
import java.util.Map;

import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import static android.view.LayoutInflater.from;
import static net.jodah.typetools.TypeResolver.resolveRawArguments;

@SuppressWarnings("all")
public abstract class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private final SparseArray<Function<ViewGroup, ViewHolder>> bindings = new SparseArray<>();
    private final Map<Predicate<Integer>, Integer> types = new ArrayMap<>();
    private int currentType = 0;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        try {
            return bindings.get(type).apply(parent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemViewType(int position) {
        try {
            for (Map.Entry<Predicate<Integer>, Integer> entry : types.entrySet())
                if (entry.getKey().test(position))
                    return entry.getValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.subject.onNext(position);
    }

    public <View extends ViewDataBinding> int addElement(Predicate<Integer> position, BiConsumer<View, BehaviorSubject<Integer>> binding) {
        throw new UnsupportedOperationException("Coming soon.");
    }

    public <View extends ViewDataBinding> int bind(Predicate<Integer> position, BiConsumer<View, BehaviorSubject<Integer>> binding) {
        Method inflate;
        try {
            inflate = resolveRawArguments(BiConsumer.class, binding.getClass())[0]
                    .getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        types.put(position, currentType);
        bindings.put(currentType, parent -> {
            BehaviorSubject<Integer> data = BehaviorSubject.create();
            View view = (View) inflate.invoke(null, from(parent.getContext()), parent, false);
            binding.accept(view, data);
            return new ViewHolder(view.getRoot(), data);
        });
        return currentType++;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private final Subject<Integer> subject;

        public ViewHolder(View view, Subject<Integer> subject) {
            super(view);
            this.subject = subject;
        }
    }
}