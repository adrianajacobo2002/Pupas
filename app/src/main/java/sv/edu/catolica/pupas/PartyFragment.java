package sv.edu.catolica.pupas;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import enums.PARTY_STATES;
import helpers.Helper;
import helpers.LoaderDialog;
import helpers.PersistentData;
import interfaces.APICallback;
import models.Participant;
import models.Party;
import models.User;
import okhttp3.Call;
import responses.Response;

public class PartyFragment extends Fragment {
    private EditText etPartyCode;
    private Button btnShowMenu, btnClipboard, btnCloseParty;
    private LinearLayout participantsCardsLayout;
    private List<Participant> participants;
    private PersistentData persistentData;
    private Participant me;
    private LoaderDialog loaderDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_party, container, false);

        this.persistentData = new PersistentData(getActivity());
        this.loaderDialog = new LoaderDialog(getActivity());

        this.loadControls(view);
        this.loadListeners();
        this.loadPartyInformation();

        return view;
    }

    public void loadPartyInformation() {
        final Activity activity = getActivity();

        this.loaderDialog.start();

        try {
            PersistentData persistentData = new PersistentData(activity);
            int id = persistentData.getCurrentPartyId();

            Party.fetchParty(id, new APICallback<Response<Party>>() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loaderDialog.dismiss();
                            Toast.makeText(activity, "Failed getting party information", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Response<Party> ResponseObject, @NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loaderDialog.dismiss();
                            Party party = ResponseObject.body;
                            if (party.state == PARTY_STATES.CLOSED) {
                                closeLocalParty();
                                return;
                            }

                            etPartyCode.setText(party.code);
                            participants = ResponseObject.body.participants;
                            loadParticipants();
                        }
                    });
                }
            });
        } catch (Exception e) {
            loaderDialog.dismiss();
            Toast.makeText(getActivity(), String.format("Error %s", e.getMessage()), Toast.LENGTH_SHORT).show();
        }
    }

    public void loadParticipants() {
        try {
            User loggedUser = this.persistentData.getObject("user", User.class);
            for (int i = 0; i < this.participants.size(); i++) {
                Participant participant = this.participants.get(i);
                String name = loggedUser.id == participant.userId
                        ? this.persistentData.getResourcesString(R.string.you)
                        : String.format("%s %s", participant.names, participant.lastNames);
                ParticipantCard participantCard = getParticipantCard(name, participant);
                if (participant.isHost)
                    participantCard.makeHostCard();

                if (participant.userId == loggedUser.id)
                    this.me = participant;

                this.participantsCardsLayout.addView(participantCard);
            }
            this.permissions();
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private ParticipantCard getParticipantCard(String name, Participant participant) {
        ParticipantCard participantCard = new ParticipantCard(
                getContext(),
                name,
                participant.total
        );
        participantCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putInt(persistentData.getResourcesString(R.string.participant_details_id_arg), participant.id);
                Helper.replaceFragment(
                        getActivity(),
                        PartyDetailFragment.class,
                        args
                );
            }
        });
        return participantCard;
    }

    public void loadControls(View view) {
        this.etPartyCode = view.findViewById(R.id.etPartyCode);
        this.btnShowMenu = view.findViewById(R.id.btnMenu);
        this.btnClipboard = view.findViewById(R.id.btnClipboard);
        this.participantsCardsLayout = view.findViewById(R.id.participantsCardsLayout);
        this.btnCloseParty = view.findViewById(R.id.btnCloseParty);
    }

    public void loadListeners() {
        this.btnShowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.replaceFragment(
                        getActivity(),
                        PupusasListFragment.class,
                        null
                );
            }
        });

        this.btnClipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                String clipboardMessage = getString(R.string.copy_code_message)
                        .replace("${code}", etPartyCode.getText());
                ClipData clipData = ClipData.newPlainText(getString(R.string.party_code_clipboard_label), clipboardMessage);
                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(getContext(), getString(R.string.party_code_clipboard_success_message), Toast.LENGTH_SHORT).show();
            }
        });

        this.btnCloseParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loaderDialog.start();
                Party party = new Party(persistentData.getCurrentPartyId());
                party.finish(new APICallback<Response>() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loaderDialog.dismiss();
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Response ResponseObject, @NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    loaderDialog.dismiss();
                                    boolean closed = ResponseObject.success;
                                    if (!closed) {
                                        Toast.makeText(getContext(), "Error trying to close a party", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            closeLocalParty();
                                            Helper.changeSelectedNav(getActivity(), R.id.navHome);
                                        }
                                    });
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void permissions() {
        if (!this.me.isHost)
            this.btnCloseParty.setVisibility(View.GONE);
    }

    public void closeLocalParty() {
        this.persistentData.setCurrentPartyId(0);
        Helper.changeSelectedNav(getActivity(), R.id.navParty);
    }
}