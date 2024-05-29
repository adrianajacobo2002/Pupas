package sv.edu.catolica.pupas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import helpers.Helper;
import helpers.PersistentData;
import models.User;


public class HomeFragment extends Fragment {
    private TextView tvNameHome;
    private Button btngenerar, btnjoin;
    private LinearLayout layoutSecundario;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        this.tvNameHome = view.findViewById(R.id.tvNameHome);
        this.btngenerar = view.findViewById(R.id.btnGenerar);
        this.btnjoin = view.findViewById(R.id.btnJoin);
        this.layoutSecundario = view.findViewById(R.id.layoutSecundario);

        PersistentData persistentData = new PersistentData(getActivity());
        try {
            User user = persistentData.getObject("user", User.class);
            this.tvNameHome.setText(user.getFullName());
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        btngenerar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.replaceFragment(
                        getActivity(),
                        layoutSecundario,
                        R.id.layoutPrincipal,
                        CreatePartyFragment.class,
                        null
                );
            }
        });

        btnjoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.replaceFragment(
                        getActivity(),
                        layoutSecundario,
                        R.id.layoutPrincipal,
                        JoinPartyFragment.class,
                        null
                );
            }
        });

        return view;
    }
}