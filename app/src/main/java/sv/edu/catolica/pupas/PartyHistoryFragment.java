package sv.edu.catolica.pupas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import helpers.Helper;
import helpers.PersistentData;
import models.Participant;
import responses.HistoryResponseItem;


public class PartyHistoryFragment extends Fragment {
    private HistoryResponseItem party;
    private EditText etHistoryCode;
    private TextView tvHistoryTotal, tvPartyName;
    private Button btnBack;
    private LinearLayout participantsContainer;
    private PersistentData persistentData;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_party_history, container, false);

        this.persistentData = new PersistentData(getActivity());
        this.loadControllers(view);

        try {
            this.party = this.persistentData.getObject("currentHistoryParty", HistoryResponseItem.class);
            this.fillScreen();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error trying to fill data for this screen", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void fillScreen() {
        this.tvPartyName.setText(this.party.name);
        this.etHistoryCode.setText(this.party.code);
        this.tvHistoryTotal.setText(String.format("$%.2f", this.party.total));

        for (int i = 0; i < this.party.participants.size(); i++) {
            Participant p = this.party.participants.get(i);
            ParticipantCard participantCard =
                    new ParticipantCard(getContext(), p.getFullName(), p.total);
            if (i == 0)
                participantCard.makeHostCard();
            participantCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle args = new Bundle();
                    args.putInt(persistentData.getResourcesString(R.string.participant_details_id_arg), p.id);
                    args.putInt("partyId", party.id);
                    Helper.replaceFragment(
                            getActivity(),
                            PartyDetailFragment.class,
                            args
                    );
                }
            });
            this.participantsContainer.addView(participantCard);
        }
    }

    private void loadControllers(View view) {
        this.btnBack = view.findViewById(R.id.btnBack);
        this.etHistoryCode = view.findViewById(R.id.etHistoryCode);
        this.tvHistoryTotal = view.findViewById(R.id.tvHistoryTotal);
        this.tvPartyName = view.findViewById(R.id.tvHistoryPartyName);
        this.participantsContainer = view.findViewById(R.id.historyParticipantsContainer);

        this.loadListeners();
    }

    public void loadListeners() {
        this.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.replaceFragment(getActivity(), ProfileFragment.class, null);
            }
        });
    }
}