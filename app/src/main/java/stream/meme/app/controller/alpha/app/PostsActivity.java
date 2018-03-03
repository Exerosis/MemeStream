package stream.meme.app.controller.alpha.app;

import android.content.Intent;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import stream.meme.app.R;
import stream.meme.app.application.MemeStream;
import stream.meme.app.controller.alpha.ListView;
import stream.meme.app.controller.alpha.comments.CommentView;
import stream.meme.app.controller.alpha.comments.CommentsView;
import stream.meme.app.controller.alpha.posts.PostView;
import stream.meme.app.controller.alpha.posts.PostsView;
import stream.meme.app.databinding.PostsActivityBinding;
import stream.meme.app.util.components.components.ComponentActivity;
import stream.meme.app.util.components.layertest.ViewFour;
import stream.meme.app.util.components.layertest.ViewOne;
import stream.meme.app.util.components.layertest.ViewThree;
import stream.meme.app.util.components.layertest.ViewTwo;
import stream.meme.app.util.components.test.GlobalTagRegistry;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static stream.meme.app.util.components.test.GlobalTagRegistry.getInstance;

public class PostsActivity extends ComponentActivity<PostsActivityBinding> {
    public static final String EXTRA_POST = "POST";

    static {
        getInstance().register(
                ViewOne.class, ViewTwo.class,
                ViewThree.class, ViewFour.class,
                ListView.class, CommentView.class,
                CommentsView.class, PostsView.class,
                PostView.class
        );
    }

    public PostsActivity() {
        super(R.layout.posts_activity, GlobalTagRegistry.getInstance());
        getComponents(view -> {
            setSupportActionBar(view.toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, view.drawerLayout, view.toolbar, R.string.description_open, R.string.description_close);
            view.drawerLayout.addDrawerListener(toggle);
            view.drawerLayout.post(toggle::syncState);
            ViewGroup header = (ViewGroup) view.navigationView.getHeaderView(0);
            ImageView backgroundImage = header.findViewById(R.id.background_image);
            ImageView profileImage = header.findViewById(R.id.profile_image);
            TextView name = header.findViewById(R.id.name);
            TextView email = header.findViewById(R.id.email);
            email.setText("exerosis@gmail.com");
            name.setText("Exerosis");

            MemeStream memestream = (MemeStream) getApplicationContext();
            memestream.getProfile().observeOn(mainThread()).subscribe(profile -> {
                backgroundImage.setImageBitmap(profile.getImage());
                profileImage.setImageBitmap(profile.getImage());
            });

            view.posts.onClick().subscribe(post -> {
                        Intent intent = new Intent(this, CommentsActivity.class);
                        intent.putExtra(EXTRA_POST, post.getId());
                        startActivity(intent);
                    }
            );
        });
    }
}