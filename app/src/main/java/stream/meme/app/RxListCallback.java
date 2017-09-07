package stream.meme.app;

import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;

import com.google.common.base.Supplier;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiPredicate;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RxListCallback<Type> implements Supplier<List<Type>>, Consumer<ListUpdateCallback> {
    private List<Type> list = new ArrayList<>();
    private final Observable<List<Type>> data;
    private final BiPredicate<Type, Type> contentsSame;
    private final BiPredicate<Type, Type> itemsSame;
    private boolean detectMoves;

    public RxListCallback(Observable<List<Type>> data, BiPredicate<Type, Type> contentsSame, BiPredicate<Type, Type> itemsSame) {
        this(data, contentsSame, itemsSame, false);
    }

    public RxListCallback(Observable<List<Type>> data, boolean detectMoves) {
        this(data, Object::equals, Object::equals, detectMoves);
    }

    public RxListCallback(Observable<List<Type>> data) {
        this(data, Object::equals, Object::equals, false);
    }

    public RxListCallback(Observable<List<Type>> data, BiPredicate<Type, Type> contentsSame, BiPredicate<Type, Type> itemsSame, boolean detectMoves) {
        this.data = data;
        this.contentsSame = contentsSame;
        this.itemsSame = itemsSame;
        this.detectMoves = detectMoves;
    }


    @Override
    public List<Type> get() {
        return list;
    }

    @Override
    public void accept(ListUpdateCallback callback) throws Exception {
        data.observeOn(Schedulers.computation()).subscribe(newList -> {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return list.size();
                }

                @Override
                public int getNewListSize() {
                    return newList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    try {
                        return itemsSame.test(list.get(oldItemPosition), newList.get(newItemPosition));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    try {
                        return contentsSame.test(list.get(oldItemPosition), newList.get(newItemPosition));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            }, detectMoves);
            list = newList;
            result.dispatchUpdatesTo(callback);
        });
    }
}
