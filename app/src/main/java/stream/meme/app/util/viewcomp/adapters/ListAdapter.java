package stream.meme.app.util.viewcomp.adapters;


import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.v7.util.DiffUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiPredicate;
import io.reactivex.functions.unsafe.Predicate;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static android.databinding.DataBindingUtil.inflate;
import static android.support.v7.util.DiffUtil.calculateDiff;
import static android.view.LayoutInflater.from;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.computation;

public class ListAdapter<Data> extends Adapter {
    private final Context context;
    private List<Data> data = new ArrayList<>();

    public ListAdapter(Context context, Observable<List<Data>> lists) {
        this(context, lists, Object::equals, Object::equals);
    }

    public ListAdapter(Context context,
                       Observable<List<Data>> lists,
                       BiPredicate<Data, Data> itemsSame,
                       BiPredicate<Data, Data> contentsSame) {
        this.context = context;
        lists.filter(list -> !list.isEmpty())
                .observeOn(computation())
                .map(list -> {
                    try {
                        return calculateDiff(new DiffUtil.Callback() {
                            @Override
                            public int getOldListSize() {
                                return data.size();
                            }

                            @Override
                            public int getNewListSize() {
                                return list.size();
                            }

                            @Override
                            public boolean areItemsTheSame(int oldItem, int newItem) {
                                try {
                                    return itemsSame.test(data.get(oldItem), list.get(newItem));
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override
                            public boolean areContentsTheSame(int oldItem, int newItem) {
                                try {
                                    return contentsSame.test(data.get(oldItem), list.get(newItem));
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                    } finally {
                        data = list;
                    }
                })
                .observeOn(mainThread())
                .subscribe(results -> results.dispatchUpdatesTo(this));
    }

    public <View extends ViewDataBinding> void bind(@LayoutRes int layout, BiConsumer<View, Observable<Data>> binding) {
        bind(position -> true, layout, binding);
    }

    @SuppressWarnings("unchecked")
    public <View extends ViewDataBinding> void bind(Predicate<Integer> positions, @LayoutRes int layout, BiConsumer<View, Observable<Data>> binding) {
        bind(positions, () -> {
            ViewDataBinding view = inflate(from(context), layout, null, false);
            Subject<Data> subject = PublishSubject.create();
            binding.accept((View) view, subject);
            return new ViewHolder(view.getRoot()) {
                @Override
                void bind(int position) {
                    subject.onNext(data.get(position));
                }
            };
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}