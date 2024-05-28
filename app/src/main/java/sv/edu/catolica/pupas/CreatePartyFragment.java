package sv.edu.catolica.pupas;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import helpers.Helper;
import helpers.LoaderDialog;
import helpers.PersistentData;
import interfaces.APICallback;
import models.Party;
import models.User;
import okhttp3.Call;
import responses.CreatePartyResponse;
import responses.Response;


public class CreatePartyFragment extends Fragment {
    private EditText etPartyName;
    private Button btnCrearParty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_create_party, container, false);

        this.etPartyName = view.findViewById(R.id.etPartyName);
        this.btnCrearParty = view.findViewById(R.id.btnCreateParty);
        this.btnCrearParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCrearPartyClick();
            }
        });

        return view;
    }

    public void handleCrearPartyClick() {
        try {
            String partyName = this.etPartyName.getText().toString();
            if (partyName.isEmpty()) {
                Toast.makeText(getContext(), getString(R.string.create_party_name_required_alert), Toast.LENGTH_LONG).show();
                return;
            }

            LoaderDialog loaderDialog = new LoaderDialog(getActivity());
            loaderDialog.start();

            PersistentData persistentData = new PersistentData(getActivity());
            User host = persistentData.getObject("user", User.class);
            Party.start(partyName, host.id, new APICallback<Response<CreatePartyResponse>>() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loaderDialog.dismiss();
                            Toast.makeText(getContext(), "Something failed trying to create a party", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Response<CreatePartyResponse> ResponseObject, @NonNull Call call, @NonNull okhttp3.Response response) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loaderDialog.dismiss();
                            persistentData.setCurrentPartyCode(ResponseObject.body.code);
                            Helper.changeMainPage(getActivity(), R.id.navParty);
                        }
                    });
                }
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), String.format("Error: %s", e.getMessage()), Toast.LENGTH_LONG).show();
        }
    }
}