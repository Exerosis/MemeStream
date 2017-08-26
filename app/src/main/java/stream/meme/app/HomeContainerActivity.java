package stream.meme.app;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.MenuItem;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;

import rx.Observable;
import rx.subjects.PublishSubject;
import stream.meme.app.databinding.HomeContainerViewBinding;
import stream.meme.app.home.HomeContainer;

import static com.bluelinelabs.conductor.Conductor.attachRouter;
import static com.bluelinelabs.conductor.RouterTransaction.with;


public class HomeContainerActivity extends AppCompatActivity implements HomeContainer {
    private Router router;
    private final SparseArray<Controller> fragments = new SparseArray<>();
    public ObservableField<Boolean> drawerOpen = new ObservableField<>();

    public HomeContainerController(Activity activity) {
        super(activity);

        fragments.put(R.id.navigation_home, HomeView.newInstance("test"));
        fragments.put(R.id.navigation_top, HomeView.newInstance("test2"));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = fragments.get(item.getItemId());
        if (fragment != null)
            ((AppCompatActivity) getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        drawerOpen.set(false);
        drawerOpen.notifyChange();
        return fragment != null;
    }

    @Override
    public ObservableField<Boolean> getDrawerOpen() {
        return drawerOpen;
    }

    @Override
    public Account getAccount() {
        return new Account("exerosis@gmail.com", "Exerosis", "http://www.ruralagriventures.com/wp-content/uploads/2017/05/man-team.jpg");
    }

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        HomeContainerViewBinding binding = DataBindingUtil.setContentView(this, R.layout.home_container_view);

        router = attachRouter(this, binding.container, inState);
        if (!router.hasRootController()) {
            router.setRoot(with(new HomeController()));
        }
    }

    @Override
    public void onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed();
        }
    }
}
