package stream.meme.app.util.components.superalpha;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class TestInjectedView extends FrameLayout {
    public TestInjectedView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TestInjectedView(@NonNull Context context) {
        super(context);
    }

    public String callSpecialMethod() {
        return "Ok so this could be something useful lol";
    }
}