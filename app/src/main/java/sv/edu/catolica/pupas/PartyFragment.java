package sv.edu.catolica.pupas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import helpers.Helper;

public class PartyFragment extends Fragment {
    private Button btnShow, btnShowMenu;
    private LinearLayout secondaryLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_party, container, false);

        this.btnShow = view.findViewById(R.id.btnShowDetail);
        this.btnShowMenu = view.findViewById(R.id.btnMenu);
        this.secondaryLayout = view.findViewById(R.id.LayoutSecundario);

        this.btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.replaceFragment(
                        getActivity(),
                        secondaryLayout,
                        R.id.LayoutPrincipal,
                        PartyDetailFragment.class,
                        null
                );
            }
        });

        this.btnShowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.replaceFragment(
                        getActivity(),
                        secondaryLayout,
                        R.id.LayoutPrincipal,
                        PupusasListFragment.class,
                        null
                );
            }
        });

        return view;
    }
}