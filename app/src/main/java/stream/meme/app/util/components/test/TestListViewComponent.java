package stream.meme.app.util.components.test;

import android.content.Context;
import android.support.annotation.NonNull;

import stream.meme.app.controller.alpha.ListView;
import stream.meme.app.databinding.ListViewBinding;
import stream.meme.app.util.components.components.StatefulViewComponent;

import static stream.meme.app.R.layout.list_view;

public class TestListViewComponent extends StatefulViewComponent<ListView.State<String>, ListViewBinding> {

    public TestListViewComponent(@NonNull Context context) {
        super(context, list_view);


    }
}