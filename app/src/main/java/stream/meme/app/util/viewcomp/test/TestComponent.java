package stream.meme.app.util.viewcomp.test;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.jakewharton.rxbinding2.widget.RxTextView;

import io.reactivex.Observable;
import stream.meme.app.R;
import stream.meme.app.databinding.TestLayoutBinding;
import stream.meme.app.util.viewcomp.Component;

public class TestComponent extends Component<TestLayoutBinding> {
    private final Observable<CharSequence> search;
    //Whatever data you want here, it won't go anywhere when the configuration changes.
    //Though the entire class will be garbage collected when the application is no longer in use.
    private int configChanges = 0;

    public TestComponent() {
        //Map changes in view to fields to be accessed from elsewhere.
        //If the view in question is another ViewComponent no SwitchMap is required.
        search = getViews().switchMap(view -> RxTextView.textChanges(view.editText));

        //Make view changes here, emits whenever the configuration changes.
        //Completes when the class needs to be garbage collected.
        getViews().subscribe(view -> {
            view.editText.setText("Config change counter: " + ++configChanges);
            //Further view work here!
        });
    }

    public Observable<CharSequence> onSearch() {
        return search;
    }

    @LayoutRes
    @Override
    protected int inflate(@NonNull AttributeSet attributes) {
        //Handle any attribs this view has and return the 'content'.
        //Keeping in mind that after returning this value,
        //the next view to be emitted will have these attribs,
        //storing them in a field might be required.
        return R.layout.test_layout;
    }
}