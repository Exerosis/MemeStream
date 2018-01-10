package stream.meme.app.util.viewcomp.test;

import android.support.annotation.NonNull;
import android.util.AttributeSet;

import io.reactivex.Observable;
import stream.meme.app.R;
import stream.meme.app.databinding.TestSecondLayoutBinding;
import stream.meme.app.util.Nothing;
import stream.meme.app.util.viewcomp.Component;

public class SecondComponent extends Component<TestSecondLayoutBinding> {

    public SecondComponent() {
        getViews().subscribe(view -> {
            System.out.println("View came");
        });
    }

    public Observable<Nothing> test() {
        return Observable.just(Nothing.NONE);
    }

    @Override
    public int inflate(@NonNull AttributeSet attributes) {
        return R.layout.test_second_layout;
    }
}
