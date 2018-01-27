package stream.meme.app.util.viewcomp.test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import stream.meme.app.databinding.DivItemBinding;
import stream.meme.app.databinding.StringItemBinding;
import stream.meme.app.databinding.TestSecondLayoutBinding;
import stream.meme.app.util.viewcomp.ViewComponent;
import stream.meme.app.util.viewcomp.adapters.ListAdapter;

import static com.jakewharton.rxbinding2.view.RxView.clicks;
import static java.lang.String.valueOf;
import static java.lang.Thread.sleep;
import static java.util.concurrent.ThreadLocalRandom.current;
import static stream.meme.app.R.layout.div_item;
import static stream.meme.app.R.layout.string_item;
import static stream.meme.app.R.layout.test_second_layout;
import static stream.meme.app.util.Operators.always;
import static stream.meme.app.util.viewcomp.adapters.Pagination.paginate;

public class TestListViewComponent extends ViewComponent<TestSecondLayoutBinding> {
    private final Subject<String> itemClicked = PublishSubject.create();

    public TestListViewComponent(@NonNull Context context) {
        super(context);

        final List<String> data = new ArrayList<>();
        final ListAdapter<String> adapter = new ListAdapter<>();

        adapter.<StringItemBinding>bind(string_item, (view, values) -> {
            values.subscribe(view.textView::setText);

            //Whenever an item gets clicked, emit the clicked data.
            clicks(view.getRoot())
                    .compose(always(values::getValue))
                    .subscribe(itemClicked);
        });

        adapter.<DivItemBinding>addBind(pos -> pos % 2 == 0, div_item, (view, positions) ->
                positions.map(pos -> "div for " + pos).subscribe(view.textView::setText)
        );

        getViews().subscribe(view -> {
            view.recyclerView.setAdapter(adapter);
            view.recyclerView.setLayoutManager(paginate(context, adapter).loading(() -> {
                sleep(2000);
                for (int i = 0; i < 5; i++) {
                    data.add(valueOf(current().nextInt(99999)));
                }
                adapter.getData().onNext(data);
            }).load());
        });
    }

    public Observable<String> onItemClicked() {
        return itemClicked;
    }

    @Override
    public int inflate(@NonNull AttributeSet attributes) {
        return test_second_layout;
    }
}