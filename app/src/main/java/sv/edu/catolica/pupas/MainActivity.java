package sv.edu.catolica.pupas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import helpers.PersistentData;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.bottomNavigationView = findViewById(R.id.bottomNavView);

        PersistentData persistentData = new PersistentData(MainActivity.this);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int currentPartyId = persistentData.getCurrentPartyId();

                Map<Integer, Fragment> mainFragments = new HashMap<>();
                mainFragments.put(R.id.navHome, currentPartyId == 0 ? new HomeFragment() : new GoToPartyFragment());
                mainFragments.put(R.id.navParty, currentPartyId == 0 ? new EmptyPartyFragment() : new PartyFragment());
                mainFragments.put(R.id.navProfile, new ProfileFragment());

                int itemId = item.getItemId();
                loadFragment(mainFragments.get(itemId), false);
                return true;
            }
        });

        int currentPartyId = persistentData.getCurrentPartyId();
        Fragment fragment = currentPartyId == 0 ? new HomeFragment() : new GoToPartyFragment();
        loadFragment(fragment, true);
    }

    private void  loadFragment(Fragment fragment, boolean isAppInitialized){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppInitialized){
            fragmentTransaction.add(R.id.screenBody, fragment);
        }else {
            fragmentTransaction.replace(R.id.screenBody, fragment);
        }
        fragmentTransaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿Deseas salir de Pupas?").setPositiveButton("Si", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}