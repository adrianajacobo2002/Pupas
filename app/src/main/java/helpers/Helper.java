package helpers;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import sv.edu.catolica.pupas.CreatePartyFragment;
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

    public static void changeSelectedNav(Activity activity, int itemId){
        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottomNavView);
        bottomNavigationView.setSelectedItemId(itemId);
    }

    public static void replaceFragment(FragmentActivity activity, ViewGroup currentLayout, int mainLayoutId, Class<? extends Fragment> fragmentClass, Bundle args) {
        // Create new fragment and transaction
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setReorderingAllowed(true);

        // Replace whatever is in the fragment_container view with this fragment
        transaction.replace(mainLayoutId, fragmentClass, args);

        // Commit the transaction
        transaction.commit();

        currentLayout.setVisibility(View.GONE);
    }
}
