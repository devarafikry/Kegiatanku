package ttc.project.absenonline.utils;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by Fikry-PC on 12/5/2017.
 */

public class SnackbarUtils {
    public static void showSnackbar(View view, Snackbar s, String message, int duration){
        if(s != null){
            s.dismiss();
        }
        Snackbar.make(view, message, duration).show();
    }
}
