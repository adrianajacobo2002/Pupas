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


public class PartyHistoryFragment extends Fragment {

    Button btnBack, btnShow;
    LinearLayout layoutOcultar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_party_history, container, false);

        btnBack = vista.findViewById(R.id.btnBack);

        btnShow = vista.findViewById(R.id.btnShowDetail);

        layoutOcultar = vista.findViewById(R.id.Secundario);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setReorderingAllowed(true);

                // Replace whatever is in the fragment_container view with this fragment
                transaction.replace(R.id.Principal, DetailHistoryFragment.class, null);

                // Commit the transaction
                transaction.commit();

                layoutOcultar.setVisibility(View.GONE);
            }
        });

        return vista;
    }
}