package stream.meme.app.util.viewcomp;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Supplier;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.subjects.Subject;

import static io.reactivex.subjects.BehaviorSubject.createDefault;

/**
 * Created by Exerosis on 1/1/2018.
 */
public abstract class ComponentInflater<ViewModel> implements LayoutInflater.Factory2 {
    private final Map<String, Supplier<ViewComponent<ViewModel>>> components = new HashMap<>();
    //Static?
    private final Map<View, Pair<ViewComponent<ViewModel>, Subject<ViewModel>>> cache = new HashMap<>();

    public void registerComponent(String name, Supplier<ViewComponent<ViewModel>> component) {
        components.put(name, component);
    }

    abstract View toView(ViewModel model);

    @Override
    public View onCreateView(@Nullable View parent, String name, Context context, AttributeSet attributes) {
        Pair<ViewComponent<ViewModel>, Subject<ViewModel>> pair = cache.get(parent);
        if (pair != null) {
            ViewModel model = pair.first.inflate(context, (ViewGroup) parent, attributes);
            pair.second.onNext(model);
            return toView(model);
        }
        //Null?
        if (!components.containsKey(name))
            return null;

        ViewComponent<ViewModel> component = components.get(name).get();
        ViewModel model = component.inflate(context, (ViewGroup) parent, attributes);
        Subject<ViewModel> binder = createDefault(model);
        try {
            component.bind(binder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //Maybe?
        if (parent != null)
            cache.put(parent, new Pair<>(component, binder));
        return toView(model);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attributes) {
        return onCreateView(null, name, context, attributes);
    }
}