package stream.meme.app.util.rxadapter;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class RxFooter {
    private RxAdapterAlpha<?> adapter;
    private int footerPosition;

    public RxFooter(int footerType, RxAdapterAlpha<?> adapter) {
        this.adapter = adapter;
        Function<Integer, Integer> typer = adapter.getType();
        adapter.type(position -> {
            if (adapter.getList().size() - 1 == position)
                return footerType;
            return typer.apply(position);
        });
    }

    public RxFooter showFooter(boolean visible) {
        if (visible) {
            adapter.getList().add(footerPosition = adapter.getList().size(), null);
            adapter.getAdapter().notifyItemInserted(footerPosition = adapter.getList().size());
        } else {
            if (adapter.getList().size() > footerPosition)
                adapter.getList().remove(footerPosition);
            adapter.getAdapter().notifyItemRemoved(footerPosition);
        }
        return this;
    }

    public RxFooter showFooter(Observable<Boolean> visibility) {
        visibility.subscribe(this::showFooter);
        return this;
    }
}
