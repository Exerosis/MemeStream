package stream.meme.app;

import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.v4.util.Pair;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.ViewGroup;

import com.google.common.base.Supplier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiPredicate;
import io.reactivex.functions.Consumer;

import static android.databinding.DataBindingUtil.inflate;
import static android.view.LayoutInflater.from;

public class RxAdapter<Type> {
    private final Map<BiPredicate<Type, Integer>, Pair<Integer, BiConsumer<Type, ViewDataBinding>>> binders = new HashMap<>();
    private List<Type> list = new ArrayList<>();

    public static <Type, Data extends Supplier<List<Type>> & Consumer<ListUpdateCallback>> RxAdapter<Type> on(RecyclerView view, Data data) {
        return new RxAdapter<>(view, data);
    }

    private <Data extends Supplier<List<Type>> & Consumer<ListUpdateCallback>> RxAdapter(RecyclerView view, Data data) {

        try {
            data.accept(new ListUpdateCallback() {
                @Override
                public void onInserted(int position, int count) {
                    list = data.get();
                    view.getAdapter().notifyItemRangeInserted(position, count);
                }

                @Override
                public void onRemoved(int position, int count) {
                    list = data.get();
                    view.getAdapter().notifyItemRangeRemoved(position, count);
                }

                @Override
                public void onMoved(int fromPosition, int toPosition) {
                    list = data.get();
                    view.getAdapter().notifyItemMoved(fromPosition, toPosition);
                }

                @Override
                public void onChanged(int position, int count, Object payload) {
                    list = data.get();
                    view.getAdapter().notifyItemRangeChanged(position, count, payload);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        view.setAdapter(new Adapter<RxViewHolder>() {
            @Override
            public RxViewHolder onCreateViewHolder(ViewGroup parent, int position) {
                try {
                    for (Map.Entry<BiPredicate<Type, Integer>, Pair<Integer, BiConsumer<Type, ViewDataBinding>>> entry : binders.entrySet())
                        if (entry.getKey().test(list.get(position), position))
                            return new RxViewHolder(inflate(from(parent.getContext()), entry.getValue().first, parent, false), entry.getValue().second);
                    return null;
                } catch (Exception exception) {
                    throw new RuntimeException(exception);
                }
            }

            @Override
            public void onBindViewHolder(RxViewHolder holder, int position) {
                try {
                    holder.binder.accept(list.get(position), holder.binding);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public int getItemViewType(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                Type item = list.get(position);
                if (item instanceof StableId)
                    return ((StableId) item).getStableId();
                return super.getItemId(position);
            }

            @Override
            public int getItemCount() {
                return list.size();
            }
        });
    }


    public <View extends ViewDataBinding, Item extends Type> void bind(@LayoutRes int layout, BiConsumer<Item, View> binder) {
        bind((item, position) -> true, layout, binder);
    }

    public <View extends ViewDataBinding, Item extends Type> void bind(Class<Item> type, @LayoutRes int layout, BiConsumer<Item, View> binder) {
        bind((item, position) -> item.getClass().isAssignableFrom(type), layout, binder);
    }

    @SuppressWarnings("unchecked")
    public <View extends ViewDataBinding, Item extends Type> void bind(BiPredicate<Item, Integer> filter, @LayoutRes int layout, BiConsumer<Item, View> binder) {
        binders.put((BiPredicate<Type, Integer>) filter, Pair.create(layout, (BiConsumer<Type, ViewDataBinding>) binder));
    }

    private class RxViewHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding binding;
        private final BiConsumer<Type, ViewDataBinding> binder;

        public RxViewHolder(ViewDataBinding binding, BiConsumer<Type, ViewDataBinding> binder) {
            super(binding.getRoot());
            this.binding = binding;
            this.binder = binder;
        }
    }
}