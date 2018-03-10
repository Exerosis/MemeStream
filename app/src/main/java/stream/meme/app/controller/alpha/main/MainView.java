package stream.meme.app.controller.alpha.main;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import stream.meme.app.R;
import stream.meme.app.application.MemeStream;
import stream.meme.app.controller.alpha.app.CommentsActivity;
import stream.meme.app.databinding.MainViewBinding;
import stream.meme.app.databinding.PostsFragmentBinding;
import stream.meme.app.util.components.components.ComponentFragment;
import stream.meme.app.util.components.components.StatefulViewComponent;

import static com.jakewharton.rxbinding2.support.design.widget.RxNavigationView.itemSelections;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static stream.meme.app.R.string.description_close;
import static stream.meme.app.R.string.description_open;

public class MainView extends StatefulViewComponent<MainView.State, MainViewBinding> {
    public static final String EXTRA_POST = "POST";
    private final Subject<AppCompatActivity> activity = BehaviorSubject.create();
    private final SparseArray<Fragment> fragments = new SparseArray<>();

    public MainView(@NonNull Context context) {
        super(context, R.layout.main_view);

        Consumer<PostsFragmentBinding> posts = components -> {
            components.posts.onClick().subscribe(post -> {
                Intent intent = new Intent(getContext(), CommentsActivity.class);
                intent.putExtra(EXTRA_POST, post.getId());
                getContext().startActivity(intent);
            });
        };
        fragments.put(R.id.navigation_home, new PostFragment().getComponents(posts));
        fragments.put(R.id.navigation_top, new PostFragment().getComponents(posts));


        getViews().subscribe(view -> {
            activity.subscribe(activity -> {
                activity.setSupportActionBar(view.toolbar);
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, view.drawerLayout, view.toolbar, description_open, description_close);
                view.drawerLayout.addDrawerListener(toggle);
                view.drawerLayout.post(toggle::syncState);

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

            ViewGroup header = (ViewGroup) view.navigationView.getHeaderView(0);
            ImageView backgroundImage = header.findViewById(R.id.background_image);
            ImageView profileImage = header.findViewById(R.id.profile_image);
            TextView name = header.findViewById(R.id.name);
            TextView email = header.findViewById(R.id.email);
            email.setText("exerosis@gmail.com");
            name.setText("Exerosis");

            MemeStream memestream = (MemeStream) view.getRoot().getContext().getApplicationContext();
            memestream.getProfile().observeOn(mainThread()).subscribe(profile -> {
                backgroundImage.setImageBitmap(profile.getImage());
                profileImage.setImageBitmap(profile.getImage());
            });
        });
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity.onNext(activity);
    }

    public static class PostFragment extends ComponentFragment<PostsFragmentBinding> {
        public PostFragment() {
            super(R.layout.posts_fragment);
        }
    }

    class State {
        Fragment fragment = null;

        Fragment fragment() {
            return fragment;
        }
    }
}
