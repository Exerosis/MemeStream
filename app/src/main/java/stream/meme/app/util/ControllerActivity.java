package stream.meme.app.util;

import android.content.res.Resources;
import android.os.Bundle;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;

import static android.content.pm.PackageManager.GET_ACTIVITIES;
import static android.content.pm.PackageManager.GET_META_DATA;
import static android.content.pm.PackageManager.NameNotFoundException;

public class ControllerActivity extends RouterActivity {
    public static final String EXTRA_CONTROLLER = "CONTROLLER";
    public static final String EXTRA_THEME = "THEME";

    @Override
    protected RouterTransaction onRouterTransaction() {
        String controller = getIntent().getStringExtra(EXTRA_CONTROLLER);
        int theme = getIntent().getIntExtra(EXTRA_THEME, 0);
        if (controller == null) {
            try {
                Bundle metaData = getPackageManager().getActivityInfo(getComponentName(), GET_ACTIVITIES | GET_META_DATA).metaData;
                controller = metaData.getString(EXTRA_CONTROLLER);
                if (metaData.containsKey("THEME") && theme == 0)
                    setTheme(metaData.getInt(EXTRA_THEME));
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (theme != 0)
            setTheme(theme);
        try {
            return RouterTransaction.with((Controller) Class.forName(controller).newInstance());
        } catch (ClassNotFoundException e) {
            throw new Resources.NotFoundException("Could not find a controller at: " + controller);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}