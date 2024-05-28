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

import helpers.Helper;


public class HomeFragment extends Fragment {

    private Button btngenerar, btnjoin;
    private LinearLayout layoutSecundario;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_home, container, false);

        this.btngenerar = vista.findViewById(R.id.btnGenerar);
        this.btnjoin = vista.findViewById(R.id.btnJoin);
        this.layoutSecundario = vista.findViewById(R.id.layoutSecundario);

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

        return vista;
    }
}