package stream.meme.app.util.viewcomp.test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import stream.meme.app.R;
import stream.meme.app.databinding.StringItemBinding;
import stream.meme.app.databinding.TestSecondLayoutBinding;
import stream.meme.app.util.Nothing;
import stream.meme.app.util.viewcomp.ViewComponent;
import stream.meme.app.util.viewcomp.adapters.ListAdapter;

import static com.jakewharton.rxbinding2.view.RxView.clicks;
import static java.util.concurrent.TimeUnit.SECONDS;
import static stream.meme.app.util.Operators.always;

public class TestListViewComponent extends ViewComponent<TestSecondLayoutBinding> {
    private final Subject<String> itemClicked = PublishSubject.create();

    public TestListViewComponent(@NonNull Context context) {
        super(context);

        //Represents data from a database, this data might change over time thus Observable.
        final Observable<List<String>> data = Observable.interval(0, 3, SECONDS)
                .map(ticks -> "Number " + ticks)
                .scanWith(ArrayList::new, (list, value) -> {
                    list.add(value);
                    return list;
                });

        final ListAdapter<String> adapter = new ListAdapter<>(context, data);
        adapter.addElement(pos -> pos == 0, "Top");
        adapter.addElement(pos -> pos == adapter.size() - 1, "Bottom");
        adapter.addElement(pos -> pos == (adapter.size() / 2) - 1, "Middle");


        //Notice the lack of R.layout.<value>, this is not needed, it's drawn from the generic.
        adapter.bind(R.layout.string_item, (StringItemBinding view, BehaviorSubject<String> values) -> {
            values.subscribe(view.textView::setText);

            //Whenever an item gets clicked, emit the clicked data.
            clicks(view.getRoot())
                    .compose(always(values::getValue))
                    .subscribe(itemClicked);
        });

        //Populate our RecyclerView with data.
        getViews().subscribe(view -> {
            view.recyclerView.setLayoutManager(new LinearLayoutManager(context));
            view.recyclerView.setAdapter(adapter.getAdapter());
        });
    }

    public Observable<String> onItemClicked() {
        return itemClicked;
    }

    @Override
    public int inflate(@NonNull AttributeSet attributes) {
        return R.layout.test_second_layout;
    }
}