package stream.meme.app.util.views;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Supplier;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiPredicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import lombok.NonNull;

import static io.reactivex.Observable.fromCallable;

/**
 * Created by Exerosis on 11/21/2017.
 */
@SuppressWarnings("unchecked")
public class RxListView<Data, Binding> extends RecyclerView {
    private List<Data> data = new ArrayList<>();
    private Supplier<Binding> view;
    private BiConsumer<Binding, Observable<Data>> binding;
    private BiPredicate<Data, Data> areItemsTheSame;
    private BiPredicate<Data, Data> areContentsTheSame;

    public RxListView(@NonNull Context context, @Nullable AttributeSet attributes) {
        super(context, attributes);
        if (attributes != null)
            bind(attributes.getAttributeIntValue("http://meme.stream.com/bivsc", "item", 0), null);
        setAdapter(new RecyclerView.Adapter<ViewHolder>() {

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Binding binding = view.get();
                Subject<Data> data = PublishSubject.create();
                try {
                    RxListView.this.binding.accept(binding, data);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return new ViewHolder(view instanceof ViewDataBinding ? ((ViewDataBinding) view).getRoot() : (View) view, data);
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                holder.data.onNext(data.get(position));
            }

            @Override
            public int getItemCount() {
                return data.size();
            }
        });
    }

    public RxListView<Data, Binding> bind(Supplier<Binding> view, BiConsumer<Binding, Observable<Data>> binding) {
        this.view = view;
        return bind(binding);
    }

    public RxListView<Data, Binding> bind(@LayoutRes int layout, BiConsumer<Binding, Observable<Data>> binding) {
        view = () -> (Binding) LayoutInflater.from(getContext()).inflate(layout, this, false);
        return bind(binding);
    }

    public RxListView<Data, Binding> bind(BiConsumer<Binding, Observable<Data>> binding) {
        this.binding = binding;
        return this;
    }

    public RxListView<Data, Binding> layoutManager(RecyclerView.LayoutManager manager) {
        this.setLayoutManager(manager);
        return this;
    }

    public RxListView<Data, Binding> addDecoration(RecyclerView.ItemDecoration decoration) {
        this.addItemDecoration(decoration);
        return this;
    }

    public RxListView<Data, Binding> areItemsTheSame(BiPredicate<Data, Data> areItemsTheSame) {
        this.areItemsTheSame = areItemsTheSame;
        return this;
    }

    public RxListView<Data, Binding> areContentsTheSame(BiPredicate<Data, Data> areContentsTheSame) {
        this.areContentsTheSame = areContentsTheSame;
        return this;
    }

    public List<Data> getData() {
        return data;
    }

    public void data(Observable<List<Data>> data) {
        data.subscribe(this::data);
    }

    public void data(List<Data> data) {
        fromCallable(() -> DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return RxListView.this.data.size();
            }

            @Override
            public int getNewListSize() {
                return data.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                try {
                    return areItemsTheSame.test(RxListView.this.data.get(oldItemPosition), data.get(newItemPosition));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                try {
                    return areContentsTheSame.test(RxListView.this.data.get(oldItemPosition), data.get(newItemPosition));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    this.data = data;
                    result.dispatchUpdatesTo(getAdapter());
                });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final Subject<Data> data;

        public ViewHolder(View view, Subject<Data> data) {
            super(view);
            this.data = data;
        }
    }
}
