package sv.edu.catolica.pupas;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import helpers.Helper;

public class EmptyPartyFragment extends Fragment {
    private Button btnCreateParty, btnJoinParty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_empty_party, container, false);

        this.btnCreateParty = view.findViewById(R.id.btnEmptyPartyCreateParty);
        this.btnJoinParty = view.findViewById(R.id.btnEmptyPartyJoinParty);

        this.btnCreateParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.replaceFragment(
                        getActivity(),
                        view.findViewById(R.id.emptyPartyLayout),
                        R.id.emptyPartyLayoutContainer,
                        CreatePartyFragment.class,
                        null
                );
            }
        });

        this.btnJoinParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.replaceFragment(
                        getActivity(),
                        view.findViewById(R.id.emptyPartyLayout),
                        R.id.emptyPartyLayoutContainer,
                        JoinPartyFragment.class,
                        null
                );
            }
        });

        return view;
    }
}
