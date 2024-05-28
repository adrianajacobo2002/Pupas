package helpers;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Helper {
    private Helper() {}

    public static void hideKeyboard(Activity context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = context.getCurrentFocus();
        if (view == null) {
            view = new View(context);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
