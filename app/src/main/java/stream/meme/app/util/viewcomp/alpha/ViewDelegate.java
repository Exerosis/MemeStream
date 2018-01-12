package stream.meme.app.util.viewcomp.alpha;

import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Display;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewOverlay;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.view.ViewStructure;
import android.view.ViewTreeObserver;
import android.view.WindowId;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.animation.Animation;
import android.view.autofill.AutofillValue;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;


@SuppressLint("MissingSuperCall")
public abstract class ViewDelegate extends View {

    public ViewDelegate(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    protected abstract View getView();

    @SuppressWarnings("unchecked")
    private <T> T invoke(Object... params) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        Class[] classes = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            classes[i] = params[i].getClass();
        }
        try {
            String name = stackTrace[3].getMethodName();
            outer:
            for (Method method : View.class.getDeclaredMethods()) {
                if (!method.getName().equals(name))
                    continue;

                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == classes.length) {
                    for (int i = 0; i < parameterTypes.length; i++)
                        if (classes[i].isAssignableFrom(parameterTypes[i]))
                            continue outer;
                    method.setAccessible(true);
                    return (T) method.invoke(getView(), params);
                }
            }
            throw new NoSuchMethodException(name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return getView().toString();
    }

    @Override
    public int getVerticalFadingEdgeLength() {
        return getView().getVerticalFadingEdgeLength();
    }

    @Override
    public void setFadingEdgeLength(int length) {
        getView().setFadingEdgeLength(length);
    }

    @Override
    public int getHorizontalFadingEdgeLength() {
        return getView().getHorizontalFadingEdgeLength();
    }

    @Override
    public int getVerticalScrollbarWidth() {
        return getView().getVerticalScrollbarWidth();
    }

    @Override
    public int getHorizontalScrollbarHeight() {
        return invoke();
    }

    @Override
    public void setVerticalScrollbarPosition(int position) {
        getView().setVerticalScrollbarPosition(position);
    }

    @Override
    public int getVerticalScrollbarPosition() {
        return getView().getVerticalScrollbarPosition();
    }

    @Override
    public void setScrollIndicators(int indicators) {
        invoke(indicators);
    }

    @Override
    public void setScrollIndicators(int indicators, int mask) {
        invoke(indicators, mask);
    }

    @Override
    public int getScrollIndicators() {
        return invoke();
    }

    @Override
    public void setOnScrollChangeListener(OnScrollChangeListener l) {
        invoke(l);
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        getView().setOnFocusChangeListener(l);
    }

    @Override
    public void addOnLayoutChangeListener(OnLayoutChangeListener listener) {
        getView().addOnLayoutChangeListener(listener);
    }

    @Override
    public void removeOnLayoutChangeListener(OnLayoutChangeListener listener) {
        getView().removeOnLayoutChangeListener(listener);
    }

    @Override
    public void addOnAttachStateChangeListener(OnAttachStateChangeListener listener) {
        getView().addOnAttachStateChangeListener(listener);
    }

    @Override
    public void removeOnAttachStateChangeListener(OnAttachStateChangeListener listener) {
        getView().removeOnAttachStateChangeListener(listener);
    }

    @Override
    public OnFocusChangeListener getOnFocusChangeListener() {
        return getView().getOnFocusChangeListener();
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        getView().setOnClickListener(l);
    }

    @Override
    public boolean hasOnClickListeners() {
        return getView().hasOnClickListeners();
    }

    @Override
    public void setOnLongClickListener(@Nullable OnLongClickListener l) {
        getView().setOnLongClickListener(l);
    }

    @Override
    public void setOnContextClickListener(@Nullable OnContextClickListener l) {
        invoke(l);
    }

    @Override
    public void setOnCreateContextMenuListener(OnCreateContextMenuListener l) {
        getView().setOnCreateContextMenuListener(l);
    }

    @Override
    public boolean performClick() {
        return getView().performClick();
    }

    @Override
    public boolean callOnClick() {
        return getView().callOnClick();
    }

    @Override
    public boolean performLongClick() {
        return getView().performLongClick();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean performLongClick(float x, float y) {
        return getView().performLongClick(x, y);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean performContextClick(float x, float y) {
        return getView().performContextClick(x, y);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean performContextClick() {
        return getView().performContextClick();
    }

    @Override
    public boolean showContextMenu() {
        return getView().showContextMenu();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean showContextMenu(float x, float y) {
        return getView().showContextMenu(x, y);
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {
        return getView().startActionMode(callback);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public ActionMode startActionMode(ActionMode.Callback callback, int type) {
        return getView().startActionMode(callback, type);
    }

    @Override
    public void setOnKeyListener(OnKeyListener l) {
        getView().setOnKeyListener(l);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        getView().setOnTouchListener(l);
    }

    @Override
    public void setOnGenericMotionListener(OnGenericMotionListener l) {
        getView().setOnGenericMotionListener(l);
    }

    @Override
    public void setOnHoverListener(OnHoverListener l) {
        getView().setOnHoverListener(l);
    }

    @Override
    public void setOnDragListener(OnDragListener l) {
        getView().setOnDragListener(l);
    }

    @Override
    public boolean requestRectangleOnScreen(Rect rectangle) {
        return getView().requestRectangleOnScreen(rectangle);
    }

    @Override
    public boolean requestRectangleOnScreen(Rect rectangle, boolean immediate) {
        return getView().requestRectangleOnScreen(rectangle, immediate);
    }

    @Override
    public void clearFocus() {
        getView().clearFocus();
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "focus"
    )
    public boolean hasFocus() {
        return getView().hasFocus();
    }

    @Override
    public boolean hasFocusable() {
        return getView().hasFocusable();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean hasExplicitFocusable() {
        return getView().hasExplicitFocusable();
    }

    @Override
    public void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        invoke(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
    public void sendAccessibilityEvent(int eventType) {
        getView().sendAccessibilityEvent(eventType);
    }

    @Override
    public void announceForAccessibility(CharSequence text) {
        getView().announceForAccessibility(text);
    }

    @Override
    public void sendAccessibilityEventUnchecked(AccessibilityEvent event) {
        getView().sendAccessibilityEventUnchecked(event);
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return getView().dispatchPopulateAccessibilityEvent(event);
    }

    @Override
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        getView().onPopulateAccessibilityEvent(event);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        getView().onInitializeAccessibilityEvent(event);
    }

    @Override
    public AccessibilityNodeInfo createAccessibilityNodeInfo() {
        return getView().createAccessibilityNodeInfo();
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        getView().onInitializeAccessibilityNodeInfo(info);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public CharSequence getAccessibilityClassName() {
        return getView().getAccessibilityClassName();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onProvideStructure(ViewStructure structure) {
        getView().onProvideStructure(structure);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onProvideAutofillStructure(ViewStructure structure, int flags) {
        getView().onProvideAutofillStructure(structure, flags);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onProvideVirtualStructure(ViewStructure structure) {
        getView().onProvideVirtualStructure(structure);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onProvideAutofillVirtualStructure(ViewStructure structure, int flags) {
        getView().onProvideAutofillVirtualStructure(structure, flags);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void autofill(AutofillValue value) {
        getView().autofill(value);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void autofill(@NonNull SparseArray<AutofillValue> values) {
        getView().autofill(values);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int getAutofillType() {
        return getView().getAutofillType();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    @ViewDebug.ExportedProperty
    public String[] getAutofillHints() {
        return getView().getAutofillHints();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public AutofillValue getAutofillValue() {
        return getView().getAutofillValue();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    @ViewDebug.ExportedProperty(
            mapping = {@ViewDebug.IntToString(
                    from = 0,
                    to = "auto"
            ), @ViewDebug.IntToString(
                    from = 1,
                    to = "yes"
            ), @ViewDebug.IntToString(
                    from = 2,
                    to = "no"
            ), @ViewDebug.IntToString(
                    from = 4,
                    to = "yesExcludeDescendants"
            ), @ViewDebug.IntToString(
                    from = 8,
                    to = "noExcludeDescendants"
            )}
    )
    public int getImportantForAutofill() {
        return getView().getImportantForAutofill();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void setImportantForAutofill(int mode) {
        getView().setImportantForAutofill(mode);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void dispatchProvideStructure(ViewStructure structure) {
        getView().dispatchProvideStructure(structure);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void dispatchProvideAutofillStructure(@NonNull ViewStructure structure, int flags) {
        getView().dispatchProvideAutofillStructure(structure, flags);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void addExtraDataToAccessibilityNodeInfo(@NonNull AccessibilityNodeInfo info, @NonNull String extraDataKey, @Nullable Bundle arguments) {
        getView().addExtraDataToAccessibilityNodeInfo(info, extraDataKey, arguments);
    }

    @Override
    public void setAccessibilityDelegate(@Nullable AccessibilityDelegate delegate) {
        getView().setAccessibilityDelegate(delegate);
    }

    @Override
    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        return getView().getAccessibilityNodeProvider();
    }

    @SuppressLint("GetContentDescriptionOverride")
    @Override
    @ViewDebug.ExportedProperty(
            category = "accessibility"
    )
    public CharSequence getContentDescription() {
        return getView().getContentDescription();
    }

    @Override
    public void setContentDescription(CharSequence contentDescription) {
        getView().setContentDescription(contentDescription);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public void setAccessibilityTraversalBefore(int beforeId) {
        getView().setAccessibilityTraversalBefore(beforeId);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public int getAccessibilityTraversalBefore() {
        return getView().getAccessibilityTraversalBefore();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public void setAccessibilityTraversalAfter(int afterId) {
        getView().setAccessibilityTraversalAfter(afterId);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public int getAccessibilityTraversalAfter() {
        return getView().getAccessibilityTraversalAfter();
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "accessibility"
    )
    public int getLabelFor() {
        return getView().getLabelFor();
    }

    @Override
    public void setLabelFor(int id) {
        getView().setLabelFor(id);
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "focus"
    )
    public boolean isFocused() {
        return getView().isFocused();
    }

    @Override
    public View findFocus() {
        return getView().findFocus();
    }

    @Override
    public boolean isScrollContainer() {
        return getView().isScrollContainer();
    }

    @Override
    public void setScrollContainer(boolean isScrollContainer) {
        getView().setScrollContainer(isScrollContainer);
    }

    @Override
    public int getDrawingCacheQuality() {
        return getView().getDrawingCacheQuality();
    }

    @Override
    public void setDrawingCacheQuality(int quality) {
        getView().setDrawingCacheQuality(quality);
    }

    @Override
    public boolean getKeepScreenOn() {
        return getView().getKeepScreenOn();
    }

    @Override
    public void setKeepScreenOn(boolean keepScreenOn) {
        getView().setKeepScreenOn(keepScreenOn);
    }

    @Override
    public int getNextFocusLeftId() {
        return getView().getNextFocusLeftId();
    }

    @Override
    public void setNextFocusLeftId(int nextFocusLeftId) {
        getView().setNextFocusLeftId(nextFocusLeftId);
    }

    @Override
    public int getNextFocusRightId() {
        return getView().getNextFocusRightId();
    }

    @Override
    public void setNextFocusRightId(int nextFocusRightId) {
        getView().setNextFocusRightId(nextFocusRightId);
    }

    @Override
    public int getNextFocusUpId() {
        return getView().getNextFocusUpId();
    }

    @Override
    public void setNextFocusUpId(int nextFocusUpId) {
        getView().setNextFocusUpId(nextFocusUpId);
    }

    @Override
    public int getNextFocusDownId() {
        return getView().getNextFocusDownId();
    }

    @Override
    public void setNextFocusDownId(int nextFocusDownId) {
        getView().setNextFocusDownId(nextFocusDownId);
    }

    @Override
    public int getNextFocusForwardId() {
        return getView().getNextFocusForwardId();
    }

    @Override
    public void setNextFocusForwardId(int nextFocusForwardId) {
        getView().setNextFocusForwardId(nextFocusForwardId);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int getNextClusterForwardId() {
        return getView().getNextClusterForwardId();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void setNextClusterForwardId(int nextClusterForwardId) {
        getView().setNextClusterForwardId(nextClusterForwardId);
    }

    @Override
    public boolean isShown() {
        return getView().isShown();
    }

    @Override
    @Deprecated
    public boolean fitSystemWindows(Rect insets) {
        return invoke(insets);
    }

    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        return getView().onApplyWindowInsets(insets);
    }

    @Override
    public void setOnApplyWindowInsetsListener(OnApplyWindowInsetsListener listener) {
        getView().setOnApplyWindowInsetsListener(listener);
    }

    @Override
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        return getView().dispatchApplyWindowInsets(insets);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public WindowInsets getRootWindowInsets() {
        return getView().getRootWindowInsets();
    }

    @Override
    public WindowInsets computeSystemWindowInsets(WindowInsets in, Rect outLocalInsets) {
        return getView().computeSystemWindowInsets(in, outLocalInsets);
    }

    @Override
    public void setFitsSystemWindows(boolean fitSystemWindows) {
        getView().setFitsSystemWindows(fitSystemWindows);
    }

    @Override
    @ViewDebug.ExportedProperty
    public boolean getFitsSystemWindows() {
        return getView().getFitsSystemWindows();
    }

    @Override
    @Deprecated
    public void requestFitSystemWindows() {
        getView().requestFitSystemWindows();
    }

    @Override
    public void requestApplyInsets() {
        getView().requestApplyInsets();
    }

    @Override
    @ViewDebug.ExportedProperty(
            mapping = {@ViewDebug.IntToString(
                    from = 0,
                    to = "VISIBLE"
            ), @ViewDebug.IntToString(
                    from = 4,
                    to = "INVISIBLE"
            ), @ViewDebug.IntToString(
                    from = 8,
                    to = "GONE"
            )}
    )
    public int getVisibility() {
        return getView().getVisibility();
    }

    @Override
    public void setVisibility(int visibility) {
        getView().setVisibility(visibility);
    }

    @Override
    @ViewDebug.ExportedProperty
    public boolean isEnabled() {
        return getView().isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        getView().setEnabled(enabled);
    }

    @Override
    public void setFocusable(boolean focusable) {
        getView().setFocusable(focusable);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void setFocusable(int focusable) {
        getView().setFocusable(focusable);
    }

    @Override
    public void setFocusableInTouchMode(boolean focusableInTouchMode) {
        getView().setFocusableInTouchMode(focusableInTouchMode);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void setAutofillHints(@Nullable String... autofillHints) {
        getView().setAutofillHints(autofillHints);
    }

    @Override
    public void setSoundEffectsEnabled(boolean soundEffectsEnabled) {
        getView().setSoundEffectsEnabled(soundEffectsEnabled);
    }

    @Override
    @ViewDebug.ExportedProperty
    public boolean isSoundEffectsEnabled() {
        return getView().isSoundEffectsEnabled();
    }

    @Override
    public void setHapticFeedbackEnabled(boolean hapticFeedbackEnabled) {
        getView().setHapticFeedbackEnabled(hapticFeedbackEnabled);
    }

    @Override
    @ViewDebug.ExportedProperty
    public boolean isHapticFeedbackEnabled() {
        return getView().isHapticFeedbackEnabled();
    }

    @Override
    public void setLayoutDirection(int layoutDirection) {
        getView().setLayoutDirection(layoutDirection);
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "layout",
            mapping = {@ViewDebug.IntToString(
                    from = 0,
                    to = "RESOLVED_DIRECTION_LTR"
            ), @ViewDebug.IntToString(
                    from = 1,
                    to = "RESOLVED_DIRECTION_RTL"
            )}
    )
    public int getLayoutDirection() {
        return getView().getLayoutDirection();
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "layout"
    )
    public boolean hasTransientState() {
        return getView().hasTransientState();
    }

    @Override
    public void setHasTransientState(boolean hasTransientState) {
        getView().setHasTransientState(hasTransientState);
    }

    @Override
    public boolean isAttachedToWindow() {
        return getView().isAttachedToWindow();
    }

    @Override
    public boolean isLaidOut() {
        return getView().isLaidOut();
    }

    @Override
    public void setWillNotDraw(boolean willNotDraw) {
        getView().setWillNotDraw(willNotDraw);
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "drawing"
    )
    public boolean willNotDraw() {
        return getView().willNotDraw();
    }

    @Override
    public void setWillNotCacheDrawing(boolean willNotCacheDrawing) {
        getView().setWillNotCacheDrawing(willNotCacheDrawing);
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "drawing"
    )
    public boolean willNotCacheDrawing() {
        return getView().willNotCacheDrawing();
    }

    @Override
    @ViewDebug.ExportedProperty
    public boolean isClickable() {
        return getView().isClickable();
    }

    @Override
    public void setClickable(boolean clickable) {
        getView().setClickable(clickable);
    }

    @Override
    public boolean isLongClickable() {
        return getView().isLongClickable();
    }

    @Override
    public void setLongClickable(boolean longClickable) {
        getView().setLongClickable(longClickable);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean isContextClickable() {
        return getView().isContextClickable();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void setContextClickable(boolean contextClickable) {
        getView().setContextClickable(contextClickable);
    }

    @Override
    public void setPressed(boolean pressed) {
        getView().setPressed(pressed);
    }

    @Override
    public void dispatchSetPressed(boolean pressed) {
        invoke(pressed);
    }

    @Override
    @ViewDebug.ExportedProperty
    public boolean isPressed() {
        return getView().isPressed();
    }

    @Override
    public boolean isSaveEnabled() {
        return getView().isSaveEnabled();
    }

    @Override
    public void setSaveEnabled(boolean enabled) {
        getView().setSaveEnabled(enabled);
    }

    @Override
    @ViewDebug.ExportedProperty
    public boolean getFilterTouchesWhenObscured() {
        return getView().getFilterTouchesWhenObscured();
    }

    @Override
    public void setFilterTouchesWhenObscured(boolean enabled) {
        getView().setFilterTouchesWhenObscured(enabled);
    }

    @Override
    public boolean isSaveFromParentEnabled() {
        return getView().isSaveFromParentEnabled();
    }

    @Override
    public void setSaveFromParentEnabled(boolean enabled) {
        getView().setSaveFromParentEnabled(enabled);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    @ViewDebug.ExportedProperty(
            mapping = {@ViewDebug.IntToString(
                    from = 0,
                    to = "NOT_FOCUSABLE"
            ), @ViewDebug.IntToString(
                    from = 1,
                    to = "FOCUSABLE"
            ), @ViewDebug.IntToString(
                    from = 16,
                    to = "FOCUSABLE_AUTO"
            )},
            category = "focus"
    )
    public int getFocusable() {
        return getView().getFocusable();
    }

    @Override
    public View focusSearch(int direction) {
        return getView().focusSearch(direction);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void setKeyboardNavigationCluster(boolean isCluster) {
        getView().setKeyboardNavigationCluster(isCluster);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void setFocusedByDefault(boolean isFocusedByDefault) {
        getView().setFocusedByDefault(isFocusedByDefault);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View keyboardNavigationClusterSearch(View currentCluster, int direction) {
        return getView().keyboardNavigationClusterSearch(currentCluster, direction);
    }

    @Override
    public boolean dispatchUnhandledMove(View focused, int direction) {
        return getView().dispatchUnhandledMove(focused, direction);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void setDefaultFocusHighlightEnabled(boolean defaultFocusHighlightEnabled) {
        getView().setDefaultFocusHighlightEnabled(defaultFocusHighlightEnabled);
    }

    @Override
    public ArrayList<View> getFocusables(int direction) {
        return getView().getFocusables(direction);
    }

    @Override
    public void addFocusables(ArrayList<View> views, int direction) {
        getView().addFocusables(views, direction);
    }

    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        getView().addFocusables(views, direction, focusableMode);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void addKeyboardNavigationClusters(@NonNull Collection<View> views, int direction) {
        getView().addKeyboardNavigationClusters(views, direction);
    }

    @Override
    public void findViewsWithText(ArrayList<View> outViews, CharSequence searched, int flags) {
        getView().findViewsWithText(outViews, searched, flags);
    }

    @Override
    public ArrayList<View> getTouchables() {
        return getView().getTouchables();
    }

    @Override
    public void addTouchables(ArrayList<View> views) {
        getView().addTouchables(views);
    }

    @Override
    public boolean isAccessibilityFocused() {
        return getView().isAccessibilityFocused();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean restoreDefaultFocus() {
        return getView().restoreDefaultFocus();
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        return getView().requestFocus(direction, previouslyFocusedRect);
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "accessibility",
            mapping = {@ViewDebug.IntToString(
                    from = 0,
                    to = "auto"
            ), @ViewDebug.IntToString(
                    from = 1,
                    to = "yes"
            ), @ViewDebug.IntToString(
                    from = 2,
                    to = "no"
            ), @ViewDebug.IntToString(
                    from = 4,
                    to = "noHideDescendants"
            )}
    )
    public int getImportantForAccessibility() {
        return getView().getImportantForAccessibility();
    }

    @Override
    public void setAccessibilityLiveRegion(int mode) {
        getView().setAccessibilityLiveRegion(mode);
    }

    @Override
    public int getAccessibilityLiveRegion() {
        return getView().getAccessibilityLiveRegion();
    }

    @Override
    public void setImportantForAccessibility(int mode) {
        getView().setImportantForAccessibility(mode);
    }

    @Override
    public boolean isImportantForAccessibility() {
        return getView().isImportantForAccessibility();
    }

    @Override
    public ViewParent getParentForAccessibility() {
        return getView().getParentForAccessibility();
    }

    @Override
    public void addChildrenForAccessibility(ArrayList<View> outChildren) {
        getView().addChildrenForAccessibility(outChildren);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public boolean dispatchNestedPrePerformAccessibilityAction(int action, Bundle arguments) {
        return getView().dispatchNestedPrePerformAccessibilityAction(action, arguments);
    }

    @Override
    public boolean performAccessibilityAction(int action, Bundle arguments) {
        return getView().performAccessibilityAction(action, arguments);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void dispatchStartTemporaryDetach() {
        getView().dispatchStartTemporaryDetach();
    }

    @Override
    public void onStartTemporaryDetach() {
        getView().onStartTemporaryDetach();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void dispatchFinishTemporaryDetach() {
        getView().dispatchFinishTemporaryDetach();
    }

    @Override
    public void onFinishTemporaryDetach() {
        getView().onFinishTemporaryDetach();
    }

    @Override
    public KeyEvent.DispatcherState getKeyDispatcherState() {
        return getView().getKeyDispatcherState();
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        return getView().dispatchKeyEventPreIme(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return getView().dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        return getView().dispatchKeyShortcutEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return getView().dispatchTouchEvent(event);
    }

    @Override
    public boolean onFilterTouchEventForSecurity(MotionEvent event) {
        return getView().onFilterTouchEventForSecurity(event);
    }

    @Override
    public boolean dispatchTrackballEvent(MotionEvent event) {
        return getView().dispatchTrackballEvent(event);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean dispatchCapturedPointerEvent(MotionEvent event) {
        return getView().dispatchCapturedPointerEvent(event);
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        return getView().dispatchGenericMotionEvent(event);
    }

    @Override
    public boolean dispatchHoverEvent(MotionEvent event) {
        return invoke(event);
    }

    @Override
    public boolean dispatchGenericPointerEvent(MotionEvent event) {
        return invoke(event);
    }

    @Override
    public boolean dispatchGenericFocusedEvent(MotionEvent event) {
        return invoke(event);
    }

    @Override
    public void dispatchWindowFocusChanged(boolean hasFocus) {
        getView().dispatchWindowFocusChanged(hasFocus);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        getView().onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    public boolean hasWindowFocus() {
        return getView().hasWindowFocus();
    }

    @Override
    public void dispatchVisibilityChanged(@NonNull View changedView, int visibility) {
        invoke(changedView, visibility);
    }

    @Override
    public void onVisibilityChanged(@NonNull View changedView, int visibility) {
        invoke(changedView, visibility);
    }

    @Override
    public void dispatchDisplayHint(int hint) {
        getView().dispatchDisplayHint(hint);
    }

    @Override
    public void onDisplayHint(int hint) {
        invoke(hint);
    }

    @Override
    public void dispatchWindowVisibilityChanged(int visibility) {
        getView().dispatchWindowVisibilityChanged(visibility);
    }

    @Override
    public void onWindowVisibilityChanged(int visibility) {
        invoke(visibility);
    }

    @Override
    public void onVisibilityAggregated(boolean isVisible) {
        invoke(isVisible);
    }

    @Override
    public int getWindowVisibility() {
        return getView().getWindowVisibility();
    }

    @Override
    public void getWindowVisibleDisplayFrame(Rect outRect) {
        getView().getWindowVisibleDisplayFrame(outRect);
    }

    @Override
    public void dispatchConfigurationChanged(Configuration newConfig) {
        getView().dispatchConfigurationChanged(newConfig);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        invoke(newConfig);
    }

    @Override
    @ViewDebug.ExportedProperty
    public boolean isInTouchMode() {
        return getView().isInTouchMode();
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        return getView().onKeyPreIme(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return getView().onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return getView().onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return getView().onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return getView().onKeyMultiple(keyCode, repeatCount, event);
    }

    @Override
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        return getView().onKeyShortcut(keyCode, event);
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return getView().onCheckIsTextEditor();
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return getView().onCreateInputConnection(outAttrs);
    }

    @Override
    public boolean checkInputConnectionProxy(View view) {
        return this.getView().checkInputConnectionProxy(view);
    }

    @Override
    public void createContextMenu(ContextMenu menu) {
        getView().createContextMenu(menu);
    }

    @Override
    public ContextMenu.ContextMenuInfo getContextMenuInfo() {
        return invoke();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu) {
        invoke(menu);
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        return getView().onTrackballEvent(event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return getView().onGenericMotionEvent(event);
    }

    @Override
    public boolean onHoverEvent(MotionEvent event) {
        return getView().onHoverEvent(event);
    }

    @Override
    @ViewDebug.ExportedProperty
    public boolean isHovered() {
        return getView().isHovered();
    }

    @Override
    public void setHovered(boolean hovered) {
        getView().setHovered(hovered);
    }

    @Override
    public void onHoverChanged(boolean hovered) {
        getView().onHoverChanged(hovered);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return getView().onTouchEvent(event);
    }

    @Override
    public void cancelLongPress() {
        getView().cancelLongPress();
    }

    @Override
    public void setTouchDelegate(TouchDelegate delegate) {
        getView().setTouchDelegate(delegate);
    }

    @Override
    public TouchDelegate getTouchDelegate() {
        return getView().getTouchDelegate();
    }

    @Override
    public void bringToFront() {
        getView().bringToFront();
    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        invoke(l, t, oldl, oldt);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        invoke(w, h, oldw, oldh);
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        invoke(canvas);
    }

    @Override
    public void setScrollX(int value) {
        getView().setScrollX(value);
    }

    @Override
    public void setScrollY(int value) {
        getView().setScrollY(value);
    }

    @Override
    public void getDrawingRect(Rect outRect) {
        getView().getDrawingRect(outRect);
    }

    @Override
    public Matrix getMatrix() {
        return getView().getMatrix();
    }

    @Override
    public float getCameraDistance() {
        return getView().getCameraDistance();
    }

    @Override
    public void setCameraDistance(float distance) {
        getView().setCameraDistance(distance);
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "drawing"
    )
    public float getRotation() {
        return getView().getRotation();
    }

    @Override
    public void setRotation(float rotation) {
        getView().setRotation(rotation);
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "drawing"
    )
    public float getRotationY() {
        return getView().getRotationY();
    }

    @Override
    public void setRotationY(float rotationY) {
        getView().setRotationY(rotationY);
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "drawing"
    )
    public float getRotationX() {
        return getView().getRotationX();
    }

    @Override
    public void setRotationX(float rotationX) {
        getView().setRotationX(rotationX);
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "drawing"
    )
    public float getScaleX() {
        return getView().getScaleX();
    }

    @Override
    public void setScaleX(float scaleX) {
        getView().setScaleX(scaleX);
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "drawing"
    )
    public float getScaleY() {
        return getView().getScaleY();
    }

    @Override
    public void setScaleY(float scaleY) {
        getView().setScaleY(scaleY);
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "drawing"
    )
    public float getPivotX() {
        return getView().getPivotX();
    }

    @Override
    public void setPivotX(float pivotX) {
        getView().setPivotX(pivotX);
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "drawing"
    )
    public float getPivotY() {
        return getView().getPivotY();
    }

    @Override
    public void setPivotY(float pivotY) {
        getView().setPivotY(pivotY);
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "drawing"
    )
    public float getAlpha() {
        return getView().getAlpha();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void forceHasOverlappingRendering(boolean hasOverlappingRendering) {
        getView().forceHasOverlappingRendering(hasOverlappingRendering);
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "drawing"
    )
    public boolean hasOverlappingRendering() {
        return getView().hasOverlappingRendering();
    }

    @Override
    public void setAlpha(float alpha) {
        getView().setAlpha(alpha);
    }

    @Override
    public boolean isDirty() {
        return getView().isDirty();
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "drawing"
    )
    public float getX() {
        return getView().getX();
    }

    @Override
    public void setX(float x) {
        getView().setX(x);
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "drawing"
    )
    public float getY() {
        return getView().getY();
    }

    @Override
    public void setY(float y) {
        getView().setY(y);
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "drawing"
    )
    public float getZ() {
        return getView().getZ();
    }

    @Override
    public void setZ(float z) {
        getView().setZ(z);
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "drawing"
    )
    public float getElevation() {
        return getView().getElevation();
    }

    @Override
    public void setElevation(float elevation) {
        getView().setElevation(elevation);
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "drawing"
    )
    public float getTranslationX() {
        return getView().getTranslationX();
    }

    @Override
    public void setTranslationX(float translationX) {
        getView().setTranslationX(translationX);
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "drawing"
    )
    public float getTranslationY() {
        return getView().getTranslationY();
    }

    @Override
    public void setTranslationY(float translationY) {
        getView().setTranslationY(translationY);
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "drawing"
    )
    public float getTranslationZ() {
        return getView().getTranslationZ();
    }

    @Override
    public void setTranslationZ(float translationZ) {
        getView().setTranslationZ(translationZ);
    }

    @Override
    public StateListAnimator getStateListAnimator() {
        return getView().getStateListAnimator();
    }

    @Override
    public void setStateListAnimator(StateListAnimator stateListAnimator) {
        getView().setStateListAnimator(stateListAnimator);
    }

    @Override
    public void setClipToOutline(boolean clipToOutline) {
        getView().setClipToOutline(clipToOutline);
    }

    @Override
    public void setOutlineProvider(ViewOutlineProvider provider) {
        getView().setOutlineProvider(provider);
    }

    @Override
    public ViewOutlineProvider getOutlineProvider() {
        return getView().getOutlineProvider();
    }

    @Override
    public void invalidateOutline() {
        getView().invalidateOutline();
    }

    @Override
    public void getHitRect(Rect outRect) {
        getView().getHitRect(outRect);
    }

    @Override
    public void getFocusedRect(Rect r) {
        getView().getFocusedRect(r);
    }

    @Override
    public boolean getGlobalVisibleRect(Rect r, Point globalOffset) {
        return getView().getGlobalVisibleRect(r, globalOffset);
    }

    @Override
    public void offsetTopAndBottom(int offset) {
        getView().offsetTopAndBottom(offset);
    }

    @Override
    public void offsetLeftAndRight(int offset) {
        getView().offsetLeftAndRight(offset);
    }

    @Override
    @ViewDebug.ExportedProperty(
            deepExport = true,
            prefix = "layout_"
    )
    public ViewGroup.LayoutParams getLayoutParams() {
        return getView().getLayoutParams();
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        getView().setLayoutParams(params);
    }

    @Override
    public void scrollTo(int x, int y) {
        getView().scrollTo(x, y);
    }

    @Override
    public void scrollBy(int x, int y) {
        getView().scrollBy(x, y);
    }

    @Override
    public boolean awakenScrollBars() {
        return invoke();
    }

    @Override
    public boolean awakenScrollBars(int startDelay) {
        return invoke(startDelay);
    }

    @Override
    public boolean awakenScrollBars(int startDelay, boolean invalidate) {
        return invoke(startDelay, invalidate);
    }

    @Override
    public void invalidate(Rect dirty) {
        getView().invalidate(dirty);
    }

    @Override
    public void invalidate(int l, int t, int r, int b) {
        getView().invalidate(l, t, r, b);
    }

    @Override
    public void invalidate() {
        getView().invalidate();
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "drawing"
    )
    public boolean isOpaque() {
        return getView().isOpaque();
    }

    @Override
    public Handler getHandler() {
        return getView().getHandler();
    }

    @Override
    public boolean post(Runnable action) {
        return getView().post(action);
    }

    @Override
    public boolean postDelayed(Runnable action, long delayMillis) {
        return getView().postDelayed(action, delayMillis);
    }

    @Override
    public void postOnAnimation(Runnable action) {
        getView().postOnAnimation(action);
    }

    @Override
    public void postOnAnimationDelayed(Runnable action, long delayMillis) {
        getView().postOnAnimationDelayed(action, delayMillis);
    }

    @Override
    public boolean removeCallbacks(Runnable action) {
        return getView().removeCallbacks(action);
    }

    @Override
    public void postInvalidate() {
        getView().postInvalidate();
    }

    @Override
    public void postInvalidate(int left, int top, int right, int bottom) {
        getView().postInvalidate(left, top, right, bottom);
    }

    @Override
    public void postInvalidateDelayed(long delayMilliseconds) {
        getView().postInvalidateDelayed(delayMilliseconds);
    }

    @Override
    public void postInvalidateDelayed(long delayMilliseconds, int left, int top, int right, int bottom) {
        getView().postInvalidateDelayed(delayMilliseconds, left, top, right, bottom);
    }

    @Override
    public void postInvalidateOnAnimation() {
        getView().postInvalidateOnAnimation();
    }

    @Override
    public void postInvalidateOnAnimation(int left, int top, int right, int bottom) {
        getView().postInvalidateOnAnimation(left, top, right, bottom);
    }

    @Override
    public void computeScroll() {
        getView().computeScroll();
    }

    @Override
    public boolean isHorizontalFadingEdgeEnabled() {
        return getView().isHorizontalFadingEdgeEnabled();
    }

    @Override
    public void setHorizontalFadingEdgeEnabled(boolean horizontalFadingEdgeEnabled) {
        getView().setHorizontalFadingEdgeEnabled(horizontalFadingEdgeEnabled);
    }

    @Override
    public boolean isVerticalFadingEdgeEnabled() {
        return getView().isVerticalFadingEdgeEnabled();
    }

    @Override
    public void setVerticalFadingEdgeEnabled(boolean verticalFadingEdgeEnabled) {
        getView().setVerticalFadingEdgeEnabled(verticalFadingEdgeEnabled);
    }

    @Override
    public float getTopFadingEdgeStrength() {
        return invoke();
    }

    @Override
    public float getBottomFadingEdgeStrength() {
        return invoke();
    }

    @Override
    public float getLeftFadingEdgeStrength() {
        return invoke();
    }

    @Override
    public float getRightFadingEdgeStrength() {
        return invoke();
    }

    @Override
    public boolean isHorizontalScrollBarEnabled() {
        return getView().isHorizontalScrollBarEnabled();
    }

    @Override
    public void setHorizontalScrollBarEnabled(boolean horizontalScrollBarEnabled) {
        getView().setHorizontalScrollBarEnabled(horizontalScrollBarEnabled);
    }

    @Override
    public boolean isVerticalScrollBarEnabled() {
        return getView().isVerticalScrollBarEnabled();
    }

    @Override
    public void setVerticalScrollBarEnabled(boolean verticalScrollBarEnabled) {
        getView().setVerticalScrollBarEnabled(verticalScrollBarEnabled);
    }

    @Override
    public void setScrollbarFadingEnabled(boolean fadeScrollbars) {
        getView().setScrollbarFadingEnabled(fadeScrollbars);
    }

    @Override
    public boolean isScrollbarFadingEnabled() {
        return getView().isScrollbarFadingEnabled();
    }

    @Override
    public int getScrollBarDefaultDelayBeforeFade() {
        return getView().getScrollBarDefaultDelayBeforeFade();
    }

    @Override
    public void setScrollBarDefaultDelayBeforeFade(int scrollBarDefaultDelayBeforeFade) {
        getView().setScrollBarDefaultDelayBeforeFade(scrollBarDefaultDelayBeforeFade);
    }

    @Override
    public int getScrollBarFadeDuration() {
        return getView().getScrollBarFadeDuration();
    }

    @Override
    public void setScrollBarFadeDuration(int scrollBarFadeDuration) {
        getView().setScrollBarFadeDuration(scrollBarFadeDuration);
    }

    @Override
    public int getScrollBarSize() {
        return getView().getScrollBarSize();
    }

    @Override
    public void setScrollBarSize(int scrollBarSize) {
        getView().setScrollBarSize(scrollBarSize);
    }

    @Override
    public void setScrollBarStyle(int style) {
        getView().setScrollBarStyle(style);
    }

    @Override
    @ViewDebug.ExportedProperty(
            mapping = {@ViewDebug.IntToString(
                    from = 0,
                    to = "INSIDE_OVERLAY"
            ), @ViewDebug.IntToString(
                    from = 16777216,
                    to = "INSIDE_INSET"
            ), @ViewDebug.IntToString(
                    from = 33554432,
                    to = "OUTSIDE_OVERLAY"
            ), @ViewDebug.IntToString(
                    from = 50331648,
                    to = "OUTSIDE_INSET"
            )}
    )
    public int getScrollBarStyle() {
        return getView().getScrollBarStyle();
    }

    @Override
    public int computeHorizontalScrollRange() {
        return invoke();
    }

    @Override
    public int computeHorizontalScrollOffset() {
        return invoke();
    }

    @Override
    public int computeHorizontalScrollExtent() {
        return invoke();
    }

    @Override
    public int computeVerticalScrollRange() {
        return invoke();
    }

    @Override
    public int computeVerticalScrollOffset() {
        return invoke();
    }

    @Override
    public int computeVerticalScrollExtent() {
        return invoke();
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return getView().canScrollHorizontally(direction);
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return getView().canScrollVertically(direction);
    }

    @Override
    public void onDraw(Canvas canvas) {
        invoke(canvas);
    }

    @Override
    public void onAttachedToWindow() {
        invoke();
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        getView().onScreenStateChanged(screenState);
    }

    @Override
    public void onRtlPropertiesChanged(int layoutDirection) {
        getView().onRtlPropertiesChanged(layoutDirection);
    }

    @Override
    public boolean canResolveLayoutDirection() {
        return getView().canResolveLayoutDirection();
    }

    @Override
    public boolean isLayoutDirectionResolved() {
        return getView().isLayoutDirectionResolved();
    }

    @Override
    public void onDetachedFromWindow() {
        invoke();
    }

    @Override
    public int getWindowAttachCount() {
        return invoke();
    }

    @Override
    public IBinder getWindowToken() {
        return getView().getWindowToken();
    }

    @Override
    public WindowId getWindowId() {
        return getView().getWindowId();
    }

    @Override
    public IBinder getApplicationWindowToken() {
        return getView().getApplicationWindowToken();
    }

    @Override
    public Display getDisplay() {
        return getView().getDisplay();
    }

    @Override
    public void onCancelPendingInputEvents() {
        getView().onCancelPendingInputEvents();
    }

    @Override
    public void saveHierarchyState(SparseArray<Parcelable> container) {
        getView().saveHierarchyState(container);
    }

    @Override
    public void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        invoke(container);
    }

    @Nullable
    @Override
    public Parcelable onSaveInstanceState() {
        return invoke();
    }

    @Override
    public void restoreHierarchyState(SparseArray<Parcelable> container) {
        getView().restoreHierarchyState(container);
    }

    @Override
    public void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        invoke(container);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        invoke(state);
    }

    @Override
    public long getDrawingTime() {
        return getView().getDrawingTime();
    }

    @Override
    public void setDuplicateParentStateEnabled(boolean enabled) {
        getView().setDuplicateParentStateEnabled(enabled);
    }

    @Override
    public boolean isDuplicateParentStateEnabled() {
        return getView().isDuplicateParentStateEnabled();
    }

    @Override
    public void setLayerType(int layerType, @Nullable Paint paint) {
        getView().setLayerType(layerType, paint);
    }

    @Override
    public void setLayerPaint(@Nullable Paint paint) {
        getView().setLayerPaint(paint);
    }

    @Override
    public int getLayerType() {
        return getView().getLayerType();
    }

    @Override
    public void buildLayer() {
        getView().buildLayer();
    }

    @Override
    public void setDrawingCacheEnabled(boolean enabled) {
        getView().setDrawingCacheEnabled(enabled);
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "drawing"
    )
    public boolean isDrawingCacheEnabled() {
        return getView().isDrawingCacheEnabled();
    }

    @Override
    public Bitmap getDrawingCache() {
        return getView().getDrawingCache();
    }

    @Override
    public Bitmap getDrawingCache(boolean autoScale) {
        return getView().getDrawingCache(autoScale);
    }

    @Override
    public void destroyDrawingCache() {
        getView().destroyDrawingCache();
    }

    @Override
    public void setDrawingCacheBackgroundColor(int color) {
        getView().setDrawingCacheBackgroundColor(color);
    }

    @Override
    public int getDrawingCacheBackgroundColor() {
        return getView().getDrawingCacheBackgroundColor();
    }

    @Override
    public void buildDrawingCache() {
        getView().buildDrawingCache();
    }

    @Override
    public void buildDrawingCache(boolean autoScale) {
        getView().buildDrawingCache(autoScale);
    }

    @Override
    public boolean isInEditMode() {
        return getView().isInEditMode();
    }

    @Override
    public boolean isPaddingOffsetRequired() {
        return invoke();
    }

    @Override
    public int getLeftPaddingOffset() {
        return invoke();
    }

    @Override
    public int getRightPaddingOffset() {
        return invoke();
    }

    @Override
    public int getTopPaddingOffset() {
        return invoke();
    }

    @Override
    public int getBottomPaddingOffset() {
        return invoke();
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "drawing"
    )
    public boolean isHardwareAccelerated() {
        return getView().isHardwareAccelerated();
    }

    @Override
    public void setClipBounds(Rect clipBounds) {
        getView().setClipBounds(clipBounds);
    }

    @Override
    public Rect getClipBounds() {
        return getView().getClipBounds();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean getClipBounds(Rect outRect) {
        return getView().getClipBounds(outRect);
    }

    @Override
    public void draw(Canvas canvas) {
        getView().draw(canvas);
    }

    @Override
    public ViewOverlay getOverlay() {
        return getView().getOverlay();
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "drawing"
    )
    public int getSolidColor() {
        return getView().getSolidColor();
    }

    @Override
    public boolean isLayoutRequested() {
        return getView().isLayoutRequested();
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        getView().layout(l, t, r, b);
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        invoke(changed, left, top, right, bottom);
    }

    @Override
    public void onFinishInflate() {
        invoke();
    }

    @Override
    public Resources getResources() {
        return getView().getResources();
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        getView().invalidateDrawable(drawable);
    }

    @Override
    public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {
        getView().scheduleDrawable(who, what, when);
    }

    @Override
    public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {
        getView().unscheduleDrawable(who, what);
    }

    @Override
    public void unscheduleDrawable(Drawable who) {
        getView().unscheduleDrawable(who);
    }

    @Override
    public boolean verifyDrawable(@NonNull Drawable who) {
        return invoke(who);
    }

    @Override
    public void drawableStateChanged() {
        invoke();
    }

    @Override
    public void drawableHotspotChanged(float x, float y) {
        getView().drawableHotspotChanged(x, y);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public void dispatchDrawableHotspotChanged(float x, float y) {
        getView().dispatchDrawableHotspotChanged(x, y);
    }

    @Override
    public void refreshDrawableState() {
        getView().refreshDrawableState();
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        return invoke(extraSpace);
    }

    public static int[] mergeDrawableStates(int[] baseState, int[] additionalState) {
        return View.mergeDrawableStates(baseState, additionalState);
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        getView().jumpDrawablesToCurrentState();
    }

    @Override
    public void setBackgroundColor(int color) {
        getView().setBackgroundColor(color);
    }

    @Override
    public void setBackgroundResource(int resid) {
        getView().setBackgroundResource(resid);
    }

    @Override
    public void setBackground(Drawable background) {
        getView().setBackground(background);
    }

    @Override
    @Deprecated
    public void setBackgroundDrawable(Drawable background) {
        getView().setBackgroundDrawable(background);
    }

    @Override
    public Drawable getBackground() {
        return getView().getBackground();
    }

    @Override
    public void setBackgroundTintList(@Nullable ColorStateList tint) {
        getView().setBackgroundTintList(tint);
    }

    @Nullable
    @Override
    public ColorStateList getBackgroundTintList() {
        return getView().getBackgroundTintList();
    }

    @Override
    public void setBackgroundTintMode(@Nullable PorterDuff.Mode tintMode) {
        getView().setBackgroundTintMode(tintMode);
    }

    @Nullable
    @Override
    public PorterDuff.Mode getBackgroundTintMode() {
        return getView().getBackgroundTintMode();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public Drawable getForeground() {
        return getView().getForeground();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void setForeground(Drawable foreground) {
        getView().setForeground(foreground);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int getForegroundGravity() {
        return getView().getForegroundGravity();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void setForegroundGravity(int gravity) {
        getView().setForegroundGravity(gravity);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void setForegroundTintList(@Nullable ColorStateList tint) {
        getView().setForegroundTintList(tint);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public ColorStateList getForegroundTintList() {
        return getView().getForegroundTintList();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void setForegroundTintMode(@Nullable PorterDuff.Mode tintMode) {
        getView().setForegroundTintMode(tintMode);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public PorterDuff.Mode getForegroundTintMode() {
        return getView().getForegroundTintMode();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onDrawForeground(Canvas canvas) {
        getView().onDrawForeground(canvas);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        getView().setPadding(left, top, right, bottom);
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        getView().setPaddingRelative(start, top, end, bottom);
    }

    @Override
    public int getPaddingTop() {
        return getView().getPaddingTop();
    }

    @Override
    public int getPaddingBottom() {
        return getView().getPaddingBottom();
    }

    @Override
    public int getPaddingLeft() {
        return getView().getPaddingLeft();
    }

    @Override
    public int getPaddingStart() {
        return getView().getPaddingStart();
    }

    @Override
    public int getPaddingRight() {
        return getView().getPaddingRight();
    }

    @Override
    public int getPaddingEnd() {
        return getView().getPaddingEnd();
    }

    @Override
    public boolean isPaddingRelative() {
        return getView().isPaddingRelative();
    }

    @Override
    public void setSelected(boolean selected) {
        getView().setSelected(selected);
    }

    @Override
    public void dispatchSetSelected(boolean selected) {
        invoke(selected);
    }

    @Override
    @ViewDebug.ExportedProperty
    public boolean isSelected() {
        return getView().isSelected();
    }

    @Override
    public void setActivated(boolean activated) {
        getView().setActivated(activated);
    }

    @Override
    public void dispatchSetActivated(boolean activated) {
        invoke(activated);
    }

    @Override
    @ViewDebug.ExportedProperty
    public boolean isActivated() {
        return getView().isActivated();
    }

    @Override
    public ViewTreeObserver getViewTreeObserver() {
        return getView().getViewTreeObserver();
    }

    @Override
    public View getRootView() {
        return getView().getRootView();
    }

    @Override
    public void getLocationOnScreen(int[] outLocation) {
        getView().getLocationOnScreen(outLocation);
    }

    @Override
    public void getLocationInWindow(int[] outLocation) {
        getView().getLocationInWindow(outLocation);
    }

    @Override
    public void setId(int id) {
        getView().setId(id);
    }

    @Override
    @ViewDebug.CapturedViewProperty
    public int getId() {
        return getView().getId();
    }

    @Override
    @ViewDebug.ExportedProperty
    public Object getTag() {
        return getView().getTag();
    }

    @Override
    public void setTag(Object tag) {
        getView().setTag(tag);
    }

    @Override
    public Object getTag(int key) {
        return getView().getTag(key);
    }

    @Override
    public void setTag(int key, Object tag) {
        getView().setTag(key, tag);
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "layout"
    )
    public int getBaseline() {
        return getView().getBaseline();
    }

    @Override
    public boolean isInLayout() {
        return getView().isInLayout();
    }

    @Override
    public void requestLayout() {
        getView().requestLayout();
    }

    @Override
    public void forceLayout() {
        getView().forceLayout();
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        invoke(widthMeasureSpec, heightMeasureSpec);
    }

    public static int combineMeasuredStates(int curState, int newState) {
        return View.combineMeasuredStates(curState, newState);
    }

    public static int resolveSize(int size, int measureSpec) {
        return View.resolveSize(size, measureSpec);
    }

    public static int resolveSizeAndState(int size, int measureSpec, int childMeasuredState) {
        return View.resolveSizeAndState(size, measureSpec, childMeasuredState);
    }

    public static int getDefaultSize(int size, int measureSpec) {
        return View.getDefaultSize(size, measureSpec);
    }

    @Override
    public int getSuggestedMinimumHeight() {
        return invoke();
    }

    @Override
    public int getSuggestedMinimumWidth() {
        return invoke();
    }

    @Override
    public int getMinimumHeight() {
        return getView().getMinimumHeight();
    }

    @Override
    public void setMinimumHeight(int minHeight) {
        getView().setMinimumHeight(minHeight);
    }

    @Override
    public int getMinimumWidth() {
        return getView().getMinimumWidth();
    }

    @Override
    public void setMinimumWidth(int minWidth) {
        getView().setMinimumWidth(minWidth);
    }

    @Override
    public Animation getAnimation() {
        return getView().getAnimation();
    }

    @Override
    public void startAnimation(Animation animation) {
        getView().startAnimation(animation);
    }

    @Override
    public void clearAnimation() {
        getView().clearAnimation();
    }

    @Override
    public void setAnimation(Animation animation) {
        getView().setAnimation(animation);
    }

    @Override
    public void onAnimationStart() {
        invoke();
    }

    @Override
    public void onAnimationEnd() {
        invoke();
    }

    @Override
    public boolean onSetAlpha(int alpha) {
        return invoke(alpha);
    }

    @Override
    public void playSoundEffect(int soundConstant) {
        getView().playSoundEffect(soundConstant);
    }

    @Override
    public boolean performHapticFeedback(int feedbackConstant) {
        return getView().performHapticFeedback(feedbackConstant);
    }

    @Override
    public boolean performHapticFeedback(int feedbackConstant, int flags) {
        return getView().performHapticFeedback(feedbackConstant, flags);
    }

    @Override
    public void setSystemUiVisibility(int visibility) {
        getView().setSystemUiVisibility(visibility);
    }

    @Override
    public int getSystemUiVisibility() {
        return getView().getSystemUiVisibility();
    }

    @Override
    public int getWindowSystemUiVisibility() {
        return getView().getWindowSystemUiVisibility();
    }

    @Override
    public void onWindowSystemUiVisibilityChanged(int visible) {
        getView().onWindowSystemUiVisibilityChanged(visible);
    }

    @Override
    public void dispatchWindowSystemUiVisiblityChanged(int visible) {
        getView().dispatchWindowSystemUiVisiblityChanged(visible);
    }

    @Override
    public void setOnSystemUiVisibilityChangeListener(OnSystemUiVisibilityChangeListener l) {
        getView().setOnSystemUiVisibilityChangeListener(l);
    }

    @Override
    public void dispatchSystemUiVisibilityChanged(int visibility) {
        getView().dispatchSystemUiVisibilityChanged(visibility);
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        return getView().onDragEvent(event);
    }

    @Override
    public boolean dispatchDragEvent(DragEvent event) {
        return getView().dispatchDragEvent(event);
    }

    public static View inflate(Context context, int resource, ViewGroup root) {
        return View.inflate(context, resource, root);
    }

    @Override
    public boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        return invoke(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    public void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        invoke(scrollX, scrollY, clampedX, clampedY);
    }

    @Override
    public int getOverScrollMode() {
        return getView().getOverScrollMode();
    }

    @Override
    public void setOverScrollMode(int overScrollMode) {
        getView().setOverScrollMode(overScrollMode);
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        getView().setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return getView().isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return getView().startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        getView().stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return getView().hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow) {
        return getView().dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow) {
        return getView().dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return getView().dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return getView().dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public void setTextDirection(int textDirection) {
        getView().setTextDirection(textDirection);
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "text",
            mapping = {@ViewDebug.IntToString(
                    from = 0,
                    to = "INHERIT"
            ), @ViewDebug.IntToString(
                    from = 1,
                    to = "FIRST_STRONG"
            ), @ViewDebug.IntToString(
                    from = 2,
                    to = "ANY_RTL"
            ), @ViewDebug.IntToString(
                    from = 3,
                    to = "LTR"
            ), @ViewDebug.IntToString(
                    from = 4,
                    to = "RTL"
            ), @ViewDebug.IntToString(
                    from = 5,
                    to = "LOCALE"
            ), @ViewDebug.IntToString(
                    from = 6,
                    to = "FIRST_STRONG_LTR"
            ), @ViewDebug.IntToString(
                    from = 7,
                    to = "FIRST_STRONG_RTL"
            )}
    )
    public int getTextDirection() {
        return getView().getTextDirection();
    }

    @Override
    public boolean canResolveTextDirection() {
        return getView().canResolveTextDirection();
    }

    @Override
    public boolean isTextDirectionResolved() {
        return getView().isTextDirectionResolved();
    }

    @Override
    public void setTextAlignment(int textAlignment) {
        getView().setTextAlignment(textAlignment);
    }

    @Override
    @ViewDebug.ExportedProperty(
            category = "text",
            mapping = {@ViewDebug.IntToString(
                    from = 0,
                    to = "INHERIT"
            ), @ViewDebug.IntToString(
                    from = 1,
                    to = "GRAVITY"
            ), @ViewDebug.IntToString(
                    from = 2,
                    to = "TEXT_START"
            ), @ViewDebug.IntToString(
                    from = 3,
                    to = "TEXT_END"
            ), @ViewDebug.IntToString(
                    from = 4,
                    to = "CENTER"
            ), @ViewDebug.IntToString(
                    from = 5,
                    to = "VIEW_START"
            ), @ViewDebug.IntToString(
                    from = 6,
                    to = "VIEW_END"
            )}
    )
    public int getTextAlignment() {
        return getView().getTextAlignment();
    }

    @Override
    public boolean canResolveTextAlignment() {
        return getView().canResolveTextAlignment();
    }

    @Override
    public boolean isTextAlignmentResolved() {
        return getView().isTextAlignmentResolved();
    }

    public static int generateViewId() {
        return View.generateViewId();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public PointerIcon onResolvePointerIcon(MotionEvent event, int pointerIndex) {
        return getView().onResolvePointerIcon(event, pointerIndex);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void setPointerIcon(PointerIcon pointerIcon) {
        getView().setPointerIcon(pointerIcon);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public PointerIcon getPointerIcon() {
        return getView().getPointerIcon();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean hasPointerCapture() {
        return getView().hasPointerCapture();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void requestPointerCapture() {
        getView().requestPointerCapture();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void releasePointerCapture() {
        getView().releasePointerCapture();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPointerCaptureChange(boolean hasCapture) {
        getView().onPointerCaptureChange(hasCapture);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void dispatchPointerCaptureChanged(boolean hasCapture) {
        getView().dispatchPointerCaptureChanged(hasCapture);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onCapturedPointerEvent(MotionEvent event) {
        return getView().onCapturedPointerEvent(event);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void setOnCapturedPointerListener(OnCapturedPointerListener l) {
        getView().setOnCapturedPointerListener(l);
    }

    @Override
    public ViewPropertyAnimator animate() {
        return getView().animate();
    }

    @Override
    @ViewDebug.ExportedProperty
    public String getTransitionName() {
        return getView().getTransitionName();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void setTooltipText(@Nullable CharSequence tooltipText) {
        getView().setTooltipText(tooltipText);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public CharSequence getTooltipText() {
        return getView().getTooltipText();
    }
}
