package stream.meme.app.util.rxadapter;

import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static android.databinding.DataBindingUtil.inflate;
import static android.view.LayoutInflater.from;
import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static io.reactivex.Observable.just;
import static io.reactivex.internal.functions.Functions.justFunction;

/**
 * Created by Home on 10/22/2017.
 */
public class RxAdapterAlpha<Type> {
    private final Map<Function<Integer, Optional<Integer>>, BiConsumer<ViewDataBinding, Observable<Type>>> bindings = new HashMap<>();
    private List<Type> list = new ArrayList<>();
    private Function<Integer, Integer> type = justFunction(0);
    private RecyclerView.Adapter<RxViewHolder> adapter;

    public <Data extends Supplier<List<Type>> & Consumer<ListUpdateCallback>> RxAdapterAlpha(RecyclerView view, Data data) {
        this(just(view), data);
    }

    public <Data extends Supplier<List<Type>> & Consumer<ListUpdateCallback>> RxAdapterAlpha(Observable<RecyclerView> views, Data data) {
        RecyclerView.Adapter<RxViewHolder> adapter = getAdapter();
        views.subscribe(view -> view.setAdapter(adapter));
        try {
            data.accept(new ListUpdateCallback() {
                @Override
                public void onInserted(int position, int count) {
                    setData(data.get());
                    adapter.notifyItemRangeInserted(position, count);
                }

                @Override
                public void onRemoved(int position, int count) {
                    setData(data.get());
                    adapter.notifyItemRangeRemoved(position, count);
                }

                @Override
                public void onMoved(int fromPosition, int toPosition) {
                    setData(data.get());
                    adapter.notifyItemMoved(fromPosition, toPosition);
                }

                @Override
                public void onChanged(int position, int count, Object payload) {
                    setData(data.get());
                    adapter.notifyItemRangeChanged(position, count, payload);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setData(List<Type> data) {
        list.clear();
        list.addAll(data);
    }

    public <View extends ViewDataBinding> RxAdapterAlpha<Type> bind(@LayoutRes int layout, BiConsumer<View, Observable<Type>> binding) {
        return bind(0, layout, binding);
    }

    public <View extends ViewDataBinding> RxAdapterAlpha<Type> bind(int type, @LayoutRes int layout, BiConsumer<View, Observable<Type>> binding) {
        return bind(t -> t == type ? of(layout) : absent(), binding);
    }

    @SuppressWarnings("unchecked")
    public <View extends ViewDataBinding> RxAdapterAlpha<Type> bind(Function<Integer, Optional<Integer>> layout, BiConsumer<View, Observable<Type>> binding) {
        bindings.put(layout, (BiConsumer<ViewDataBinding, Observable<Type>>) binding);
        return this;
    }

    public RxAdapterAlpha<Type> type(Function<Integer, Integer> type) {
        this.type = type;
        return this;
    }

    public Function<Integer, Integer> getType() {
        return type;
    }

    public List<Type> getList() {
        return list;
    }

    public RecyclerView.Adapter<RxViewHolder> getAdapter() {
        if (adapter == null)
            adapter = new RecyclerView.Adapter<RxViewHolder>() {
                @Override
                public RxViewHolder onCreateViewHolder(ViewGroup parent, int type) {
                    try {
                        for (Map.Entry<Function<Integer, Optional<Integer>>, BiConsumer<ViewDataBinding, Observable<Type>>> entry : bindings.entrySet())
                            if (entry.getKey().apply(type).isPresent()) {
                                ViewDataBinding view = inflate(from(parent.getContext()), entry.getKey().apply(type).get(), parent, false);
                                RxViewHolder viewHolder = new RxViewHolder(view.getRoot());
                                entry.getValue().accept(view, viewHolder.getObservable());
                                return viewHolder;
                            }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    throw new IllegalStateException("Could not find a layout for type " + type);
                }

                @Override
                public void onBindViewHolder(RxViewHolder holder, int position) {
                    holder.subject.onNext(list.get(position));
                }

                @Override
                public void onViewRecycled(RxViewHolder holder) {
                    for (Disposable disposable : holder.disposables)
                        disposable.dispose();
                }

                @Override
                public int getItemViewType(int position) {
                    try {
                        return type.apply(position);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public int getItemCount() {
                    return list.size();
                }
            };
        return adapter;
    }


    class RxViewHolder extends RecyclerView.ViewHolder {
        private final List<Disposable> disposables = new ArrayList<>();
        private final Subject<Type> subject;

        RxViewHolder(View root) {
            super(root);
            subject = PublishSubject.create();
        }

        Observable<Type> getObservable() {
            ConnectableObservable<Type> observable = subject.publish();
            observable.connect(disposables::add);
            return observable;
        }
    }

}