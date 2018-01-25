package stream.meme.app.util.viewcomp.adapters;

import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.functions.Supplier;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import static android.support.v7.util.DiffUtil.Callback;
import static android.support.v7.util.DiffUtil.DiffResult;
import static android.support.v7.util.DiffUtil.calculateDiff;

public abstract class Adapter implements ListUpdateCallback {
    private final SparseArray<Supplier<ViewHolder>> creators = new SparseArray<>();
    private final Map<Predicate<Integer>, Integer> addedBindings = new ArrayMap<>();
    private final Map<Predicate<Integer>, Integer> bindings = new ArrayMap<>();
    private final RecyclerView.Adapter<ViewHolder> adapter;
    private ArrayMap<Integer, Integer> fixedTypes = new ArrayMap<>();
    private int currentType = 0;

    public Adapter() {
        adapter = new RecyclerView.Adapter<ViewHolder>() {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
                try {
                    Supplier<ViewHolder> supplier = creators.get(type);
                    if (supplier != null)
                        return supplier.apply();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                throw new IllegalStateException("Could not find a creator for type: " + type);
            }

            @Override
            public int getItemViewType(int position) {
                try {
                    for (Map.Entry<Integer, Integer> entry : fixedTypes.entrySet())
                        if (entry.getKey() == position)
                            return entry.getValue();
                    int dataIndex = toDataIndex(position);
                    for (Map.Entry<Predicate<Integer>, Integer> entry : bindings.entrySet())
                        if (entry.getKey().test(dataIndex))
                            return entry.getValue();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return -1;
            }

            @Override
            public int getItemCount() {
                return size() + fixedTypes.size();
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                holder.subject.onNext(position);
            }
        };
    }

    public abstract int size();

    public int addElement(Predicate<Integer> position, Function<BehaviorSubject<Integer>, View> binding) {
        addedBindings.put(position, currentType++);
        creators.put(currentType, () -> {
            BehaviorSubject<Integer> data = BehaviorSubject.create();
            return new ViewHolder(binding.apply(data), data);
        });
        return currentType++;
    }

    public int bind(Predicate<Integer> position, Function<BehaviorSubject<Integer>, View> binding) {
        bindings.put(position, currentType);
        creators.put(currentType, () -> {
            BehaviorSubject<Integer> data = BehaviorSubject.create();
            return new ViewHolder(binding.apply(data), data);
        });
        return currentType++;
    }

    private int offset(int index) {
        int offset = 0;
        for (Map.Entry<Integer, Integer> entry : fixedTypes.entrySet())
            if (entry.getKey() < index)
                offset++;
        return offset;
    }

    private int fromDataIndex(int index) {
        return index + offset(index);
    }

    private int toDataIndex(int index) {
        return index - offset(index);
    }

    private void walkTypes() {
        try {
            ArrayMap<Integer, Integer> types = new ArrayMap<>();
            for (int dataIndex = 0; dataIndex < size(); dataIndex++)
                for (Map.Entry<Predicate<Integer>, Integer> entry : addedBindings.entrySet())
                    if (entry.getKey().test(dataIndex))
                        types.put(dataIndex + types.size(), entry.getValue());

            DiffResult differences = calculateDiff(new Callback() {
                @Override
                public int getOldListSize() {
                    return fixedTypes.size();
                }

                @Override
                public int getNewListSize() {
                    return types.size();
                }

                @Override
                public boolean areItemsTheSame(int oldIndex, int newIndex) {
                    return fixedTypes.valueAt(oldIndex).equals(types.valueAt(newIndex));
                }

                @Override
                public boolean areContentsTheSame(int oldIndex, int newIndex) {
                    return true;
                }
            });
            differences.dispatchUpdatesTo(new ListUpdateCallback() {
                @Override
                public void onInserted(int position, int count) {
                    adapter.notifyItemRangeInserted(fixedTypes.get(position), count);
                }

                @Override
                public void onRemoved(int position, int count) {
                    adapter.notifyItemRangeRemoved(fixedTypes.get(position), count);
                }

                @Override
                public void onMoved(int from, int to) {
                    adapter.notifyItemRangeInserted(fixedTypes.get(from), fixedTypes.get(to));
                }

                @Override
                public void onChanged(int position, int count, Object payload) {
                    adapter.notifyItemRangeChanged(fixedTypes.get(position), count, payload);
                }
            });
        } catch (Exception e) {

        }
    }


    @Override
    public void onInserted(int position, int count) {
        adapter.notifyItemRangeInserted(fromDataIndex(position), count);
        walkTypes();
    }

    @Override
    public void onRemoved(int position, int count) {
        adapter.notifyItemRangeRemoved(fromDataIndex(position), count);
        walkTypes();
    }

    @Override
    public void onMoved(int from, int to) {
        adapter.notifyItemMoved(fromDataIndex(from), fromDataIndex(to));
        walkTypes();
    }

    @Override
    public void onChanged(int position, int count, Object payload) {
        adapter.notifyItemRangeChanged(fromDataIndex(position), count, payload);
    }

    public RecyclerView.Adapter<ViewHolder> getAdapter() {
        return adapter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final Subject<Integer> subject;

        public ViewHolder(View view, Subject<Integer> subject) {
            super(view);
            this.subject = subject;
        }
    }
}