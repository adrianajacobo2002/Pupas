package helpers;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import sv.edu.catolica.pupas.R;

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

    public static void  changeMainPage(Activity activity, int itemId){
        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottomNavView);
        bottomNavigationView.setSelectedItemId(itemId);
    }
}
