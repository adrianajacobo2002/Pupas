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

public class PartyFragment extends Fragment {

    Button btnshow;
    LinearLayout pruebita;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_party, container, false);

        btnshow = vista.findViewById(R.id.btnShowDetail);
        pruebita = vista.findViewById(R.id.LayoutSecundario);

        btnshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setReorderingAllowed(true);

                // Replace whatever is in the fragment_container view with this fragment
                transaction.replace(R.id.LayoutPrincipal, PartyDetailFragment.class, null);

                // Commit the transaction
                transaction.commit();

                pruebita.setVisibility(View.GONE);
            }
        });

        return vista;
    }
}