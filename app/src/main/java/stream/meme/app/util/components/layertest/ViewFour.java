package stream.meme.app.util.components.layertest;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import stream.meme.app.R;
import stream.meme.app.controller.alpha.ListView;
import stream.meme.app.databinding.ViewFourBinding;
import stream.meme.app.util.components.adapters.ListAdapter;
import stream.meme.app.util.components.components.StatefulViewComponent;

import static io.reactivex.Observable.interval;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by Exerosis on 2/4/2018.
 */

public class ViewFour extends StatefulViewComponent<ViewFour.State, ViewFourBinding> {
    public ViewFour(@NonNull Context context) {
        super(context, R.layout.view_four);

        Observable<List<String>> data = interval(0, 2, SECONDS)
                .map(tick -> "Number " + tick)
                .scanWith(ArrayList::new, (list, value) -> {
                    list.add(value);
                    return list;
                });

        final ListAdapter<String> adapter = new ListAdapter<>(data);

        getViews().subscribe(views -> {
            System.out.println(views.listView);
        });
    }

    public class State extends ListView.State<String> {

    }
}