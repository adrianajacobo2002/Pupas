package sv.edu.catolica.pupas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;


public class ProfileFragment extends Fragment {

    Button btnEditInfo, btnShowParties;

    LinearLayout layoutReemplazar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_profile, container, false);

        btnEditInfo = vista.findViewById(R.id.btnEditData);

        btnShowParties = vista.findViewById(R.id.btnResume);

        layoutReemplazar = vista.findViewById(R.id.layoutSecundario);

        btnEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditInfo();
            }
        });

        btnShowParties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setReorderingAllowed(true);

                // Replace whatever is in the fragment_container view with this fragment
                transaction.replace(R.id.layoutPrincipal, PartyHistoryFragment.class, null);

                // Commit the transaction
                transaction.commit();

                layoutReemplazar.setVisibility(View.GONE);

            }
        });

        return vista;
    }

    private void showEditInfo(){
        LayoutInflater inflater = requireActivity().getLayoutInflater();


        View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);

        new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .show();
    }

    private void showResumeParties(){
        LayoutInflater inflater = requireActivity().getLayoutInflater();


        View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);

        new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .show();
    }


}