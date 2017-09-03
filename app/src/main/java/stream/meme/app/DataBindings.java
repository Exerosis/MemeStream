package stream.meme.app;

import android.app.Activity;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import static android.view.Gravity.END;
import static android.view.Gravity.START;

public class DataBindings {

    @BindingAdapter({"homeAsUp", "supportActionBar"})
    public static void setAction(Toolbar toolbar, boolean homeAsUp, AppCompatActivity activity) {
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(homeAsUp);
    }

    @BindingAdapter({"activity", "toolbar", "contentDescriptionOpen", "contentDescriptionClosed"})
    public static void setToggle(DrawerLayout drawerLayout, Activity activity, @IdRes int toolbarId, String open, String closed) {
        ViewParent parent = drawerLayout.getParent();
        do {
            View toolbar = ((ViewGroup) parent).findViewById(toolbarId);
            if (toolbar != null) {
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawerLayout, ((Toolbar) toolbar), R.string.description_open, R.string.description_close);
                toggle.setToolbarNavigationClickListener(view -> {
                    System.out.println("test");
                });
                drawerLayout.addDrawerListener(toggle);
                toggle.syncState();
                break;
            }
            parent = parent.getParent();
        } while (parent != null && parent instanceof ViewGroup);
    }

    @BindingAdapter("startOpen")
    public static void startOpen(DrawerLayout drawerLayout, boolean open) {
        if (open)
            drawerLayout.openDrawer(START);
        else
            drawerLayout.closeDrawer(START);
    }

    @BindingAdapter("endOpen")
    public static void endOpen(DrawerLayout drawerLayout, boolean open) {
        if (open)
            drawerLayout.openDrawer(END);
        else
            drawerLayout.closeDrawer(END);
    }


    @BindingAdapter({"headerLayout", "controller"})
    public static void headerLayout(NavigationView navigationView, @LayoutRes int layout, Controller controller) {
        ViewDataBinding inflate = DataBindingUtil.inflate(LayoutInflater.from(navigationView.getContext()), layout, navigationView, false);
        inflate.setVariable(BR.controller, controller);
        navigationView.addHeaderView(inflate.getRoot());
    }

    @BindingAdapter("image")
    public static void image(ImageView imageView, String url) {
        Picasso.with(imageView.getContext()).load(url).into(imageView);
    }
}