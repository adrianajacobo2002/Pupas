package sv.edu.catolica.pupas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import helpers.PersistentData;
import models.User;


public class ProfileFragment extends Fragment {

    private TextView tvFullName, tvEmail;
    private Button btnEditInfo, btnShowParties;
    private LinearLayout secondaryLayout;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        this.tvFullName = view.findViewById(R.id.tvFullName);
        this.tvEmail = view.findViewById(R.id.tvEmail);

        PersistentData persistentData = new PersistentData(getActivity());
        try {
            User user = persistentData.getObject(getString(R.string.user_sp_key), User.class);
            this.tvFullName.setText(String.format("%s %s", user.names, user.lastNames));
            this.tvEmail.setText(user.email);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.btnEditInfo = view.findViewById(R.id.btnEditData);
        this.btnShowParties = view.findViewById(R.id.btnResume);
        this.secondaryLayout = view.findViewById(R.id.layoutSecundario);

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

                secondaryLayout.setVisibility(View.GONE);
            }
        });

        return view;
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