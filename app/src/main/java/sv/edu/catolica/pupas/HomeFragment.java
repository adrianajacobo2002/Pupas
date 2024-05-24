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


public class HomeFragment extends Fragment {

    Button btngenerar, btnjoin;
    LinearLayout pruebita;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_home, container, false);



        btngenerar = vista.findViewById(R.id.btnGenerar);
        pruebita = vista.findViewById(R.id.layoutSecundario);

        btnjoin = vista.findViewById(R.id.btnJoin);



        btngenerar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new fragment and transaction
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setReorderingAllowed(true);

                // Replace whatever is in the fragment_container view with this fragment
                transaction.replace(R.id.layoutPrincipal, CreatePartyFragment.class, null);

                // Commit the transaction
                transaction.commit();

                pruebita.setVisibility(View.GONE);


            }
        });

        btnjoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setReorderingAllowed(true);

                // Replace whatever is in the fragment_container view with this fragment
                transaction.replace(R.id.layoutPrincipal, JoinPartyFragment.class, null);

                // Commit the transaction
                transaction.commit();

                pruebita.setVisibility(View.GONE);
            }
        });


        return vista;



    }
}