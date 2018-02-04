package stream.meme.app.util.components.test;

import android.content.Context;
import android.support.annotation.NonNull;

import stream.meme.app.controller.alpha.ListView;
import stream.meme.app.databinding.TestSecondLayoutBinding;
import stream.meme.app.util.components.components.StatefulViewComponent;

import static stream.meme.app.R.layout.test_second_layout;

public class TestListViewComponent extends StatefulViewComponent<ListView.State<String>, TestSecondLayoutBinding> {

    public TestListViewComponent(@NonNull Context context) {
        super(context, test_second_layout);

     /*   Observable<List<String>> data = interval(0, 2, SECONDS)
                .map(tick -> "Number " + tick)
                .scanWith(ArrayList::new, (list, value) -> {
                    list.add(value);
                    return list;
                });

        final ListAdapter<String> adapter = new ListAdapter<>(data);

        getComponents(components -> {
            components.listView.attach(this).adapter(adapter);
            adapter.<StringItemBinding>bind(R.layout.string_item, (view, values) ->
                    values.subscribe(text(view.textView)));
        });*/
    }
}