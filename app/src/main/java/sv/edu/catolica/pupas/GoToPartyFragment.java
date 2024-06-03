package sv.edu.catolica.pupas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import helpers.Helper;


public class GoToPartyFragment extends Fragment {
    private Button btnGoToParty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_go_to_party, container, false);

        this.btnGoToParty = view.findViewById(R.id.btnGoToParty);
        this.btnGoToParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.changeSelectedNav(getActivity(), R.id.navParty);
            }
        });

        return view;
    }
}