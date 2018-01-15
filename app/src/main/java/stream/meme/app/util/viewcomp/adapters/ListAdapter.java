package stream.meme.app.util.viewcomp.adapters;


import android.databinding.ViewDataBinding;
import android.support.v7.util.DiffUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiPredicate;
import io.reactivex.subjects.BehaviorSubject;

import static android.support.v7.util.DiffUtil.calculateDiff;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.computation;

public class ListAdapter<Data> extends Adapter {
    private List<Data> data = new ArrayList<>();

    public ListAdapter(Observable<List<Data>> lists) {
        this(lists, Object::equals, Object::equals);
    }

    public ListAdapter(Observable<List<Data>> lists,
                       BiPredicate<Data, Data> itemsSame,
                       BiPredicate<Data, Data> contentsSame) {
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

    public <View extends ViewDataBinding> int bind(BiConsumer<View, BehaviorSubject<Data>> binding) {
        BehaviorSubject<Data> subject = BehaviorSubject.create();
        return super.<View>bind(position -> true, (view, positions) -> {
            positions.map(data::get).subscribe(subject);
            binding.accept(view, subject);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}