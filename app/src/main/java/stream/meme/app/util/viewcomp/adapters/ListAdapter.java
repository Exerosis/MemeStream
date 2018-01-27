package stream.meme.app.util.viewcomp.adapters;


import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Predicate;
import io.reactivex.functions.unsafe.BiFunction;
import io.reactivex.functions.unsafe.Function;
import io.reactivex.subjects.BehaviorSubject;

import static android.databinding.DataBindingUtil.inflate;
import static android.support.v7.util.DiffUtil.Callback;
import static android.support.v7.util.DiffUtil.calculateDiff;
import static android.view.LayoutInflater.from;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.computation;

public class ListAdapter<Data> extends Adapter {
    public static final Boolean NOTHING = null;
    public static final Boolean ITEM = false;
    public static final Boolean CONTENT = true;

    private List<Entry> entries = new ArrayList<>();
    private Function<Integer, Integer> fixedTypes = position -> DEFAULT_TYPE;
    private int size = 0;
    private BehaviorSubject<List<Data>> lists = BehaviorSubject.create();

    public ListAdapter() {
        this(null);
    }

    public ListAdapter(Observable<List<Data>> lists) {
        this(lists, (first, second) -> first.equals(second) ? CONTENT : NOTHING);
    }

    public ListAdapter(Observable<List<Data>> lists, BiFunction<Data, Data, Boolean> relation) {
        if (lists != null)
            lists.subscribe(this.lists);
        this.lists.filter(list -> !list.isEmpty())
                .observeOn(computation())
                .map(data -> {
                    size = data.size();
                    List<Entry> newEntries = new ArrayList<>();
                    int offset = 0;
                    for (int i = 0; i < data.size(); i++) {
                        int type = fixedTypes.apply(i);
                        newEntries.add(new Entry(data.get(i), i - offset));
                        if (type != DEFAULT_TYPE) {
                            newEntries.add(new Entry(type, i));
                            offset++;
                        }
                    }
                    try {
                        return calculateDiff(new Callback() {
                            @Override
                            public int getOldListSize() {
                                return entries.size();
                            }

                            @Override
                            public int getNewListSize() {
                                return newEntries.size();
                            }

                            @Override
                            public boolean areItemsTheSame(int oldItem, int newItem) {
                                Entry first = entries.get(oldItem);
                                Entry second = newEntries.get(newItem);
                                if (first.isData)
                                    return second.isData && relation.applyUnsafe(first.data(), second.data()) != NOTHING;
                                return !second.isData && first.attachment.equals(second.attachment);
                            }

                            @Override
                            public boolean areContentsTheSame(int oldItem, int newItem) {
                                Entry first = entries.get(oldItem);
                                Entry second = newEntries.get(newItem);
                                if (!first.isData)
                                    return true;
                                return relation.applyUnsafe(first.data(), second.data());
                            }
                        });
                    } finally {
                        entries = newEntries;
                    }
                })
                .observeOn(mainThread())
                .subscribe(results -> results.dispatchUpdatesTo(this));
    }

    public BehaviorSubject<List<Data>> getData() {
        return lists;
    }

    public void reinjectBindings() {
        if (lists.getValue() != null)
            lists.onNext(lists.getValue());
    }

    public int addBind(Predicate<Integer> positions, @LayoutRes int layout) {
        return addBind(positions, layout, ($, $_) -> {
        });
    }

    public <View extends ViewDataBinding> int addBind(Predicate<Integer> positions, @LayoutRes int layout, BiConsumer<View, BehaviorSubject<Integer>> binding) {
        int type = bindInternal(position -> {
            Entry entry = entries.get(position);
            return !entry.isData && positions.test(entry.dataIndex);
        }, layout, binding);
        fixedTypes = fixedTypes.returnWhen(type, positions);
        return type;
    }

    public <View extends ViewDataBinding> int bind(@LayoutRes int layout, BiConsumer<View, BehaviorSubject<Data>> binding) {
        return bind(position -> true, layout, binding);
    }

    public <View extends ViewDataBinding> int bind(Predicate<Integer> positions, @LayoutRes int layout, BiConsumer<View, BehaviorSubject<Data>> binding) {
        return bindInternal(position -> {
            Entry entry = entries.get(position);
            return entry.isData && positions.test(entry.dataIndex);
        }, layout, binding);
    }

    @SuppressWarnings("all")
    private <View, Data> int bindInternal(Predicate<Integer> positions, @LayoutRes int layout, BiConsumer<View, BehaviorSubject<Data>> binding) {
        return bind(positions, parent -> {
            ViewDataBinding view = inflate(from(parent.getContext()), layout, parent, false);
            BehaviorSubject<Data> subject = BehaviorSubject.create();
            binding.accept((View) view, subject);
            return new ViewHolder(view.getRoot()) {
                @Override
                void bind(int position) {
                    try {
                        subject.onNext(entries.get(position).data());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        });
    }

    /**
     * Warning! This size reflects the size of the last entry build.
     * Use {@link #size()} to get the current data size. Most of the
     * time this should only be used internally.
     *
     * @return The number of entries currently displayed on in the RecyclerView.
     */
    @Override
    public int getItemCount() {
        return entries.size();
    }

    public int size() {
        return size;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public static class Entry {
        private final int dataIndex;
        private final boolean isData;
        private final Object attachment;

        public Entry(int type, int dataIndex) {
            this.dataIndex = dataIndex;
            isData = false;
            attachment = type;
        }

        public Entry(Object attachment, int dataIndex) {
            this.dataIndex = dataIndex;
            this.attachment = attachment;
            isData = true;
        }

        @SuppressWarnings("all")
        <Data> Data data() {
            return (Data) (isData ? attachment : dataIndex);
        }
    }
}