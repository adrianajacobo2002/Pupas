package sv.edu.catolica.pupas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import helpers.PersistentData;
import models.User;


public class ProfileFragment extends Fragment {

    private TextView tvFullName, tvEmail;

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

        return view;
    }
}