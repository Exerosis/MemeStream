package stream.meme.app.controller.alpha.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import stream.meme.app.R;
import stream.meme.app.databinding.NavigationViewBinding;
import stream.meme.app.util.components.components.ViewComponent;

import static com.jakewharton.rxbinding2.support.design.widget.RxNavigationView.itemSelections;

public class NavigationView extends ViewComponent<NavigationViewBinding> {
    public NavigationView(@NonNull Context context) {
        super(context, R.layout.navigation_view);

        getViews().subscribe(view -> {
            itemSelections(view.navigationView)
                    .map(MenuItem::getItemId)
                    .doAfterNext(id -> view.drawerLayout.closeDrawers())
                    .subscribe(id ->
                            activity.getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(view.posts.getId(), fragments.get(id))
                                    .commit()
                    );
        });
    }
}
