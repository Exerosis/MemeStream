package stream.meme.app.util;

import android.animation.Animator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.changehandler.AnimatorChangeHandler;

public class CircularRevealChangeHandler extends AnimatorChangeHandler {
    @NonNull
    @Override
    protected Animator getAnimator(@NonNull ViewGroup container, @Nullable View from, @Nullable View to, boolean isPush, boolean toAddedToContainer) {
        int x = container.getLeft();
        int y = container.getTop();
        int startRadius = 0;
        int endRadius = (int) Math.hypot(container.getWidth(), container.getHeight());
        if (from != null)
            from.setVisibility(View.INVISIBLE);
        if (isPush)
            return ViewAnimationUtils.createCircularReveal(to, x, y, startRadius, endRadius).setDuration(500);
        else
            return ViewAnimationUtils.createCircularReveal(from, x, y, endRadius, startRadius).setDuration(500);
    }

    @Override
    protected void resetFromView(@NonNull View from) {
    }
}
