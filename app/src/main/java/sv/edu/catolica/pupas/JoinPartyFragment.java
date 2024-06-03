package sv.edu.catolica.pupas;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import helpers.Helper;
import helpers.LoaderDialog;
import helpers.PersistentData;
import interfaces.APICallback;
import models.Party;
import models.User;
import okhttp3.Call;
import responses.JoinPartyResponse;
import responses.Response;

public class JoinPartyFragment extends Fragment {
    private TextInputEditText etPartyCode;
    private Button btnJoinParty;
    private PersistentData persistentData;
    private LoaderDialog loaderDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_join_party, container, false);

        this.persistentData = new PersistentData(getActivity());
        this.loaderDialog = new LoaderDialog(getActivity());

        this.etPartyCode = view.findViewById(R.id.etPartyCode);
        this.btnJoinParty = view.findViewById(R.id.btnJoinParty);
        this.btnJoinParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleJoinPartyClick();
            }
        });

        return view;
    }

    public void handleJoinPartyClick() {
        try {
            String partyCode = this.etPartyCode.getText().toString();
            if (partyCode.isEmpty()) {
                Toast.makeText(getContext(), this.persistentData.getResourcesString(R.string.all_inputs_required), Toast.LENGTH_SHORT).show();
                return;
            }

            this.loaderDialog.start();
            Helper.hideKeyboard(getActivity());
            int userId = this.persistentData.getObject("user", User.class).id;
            Party.join(partyCode, userId, new APICallback<Response<JoinPartyResponse>>() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    loaderDialog.dismiss();
                    Toast.makeText(getContext(), String.format("Error on API call: %s", e.getMessage()), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Response<JoinPartyResponse> ResponseObject, @NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loaderDialog.dismiss();
                            boolean joined = ResponseObject.success;
                            if (!joined) {
                                try {
                                    String message = response.code() == 404
                                            ? persistentData.getResourcesString(R.string.join_party_error)
                                            : new JSONObject(response.body().string()).getString("message");
                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                    return;
                                } catch (JSONException | IOException e) {
                                    Toast.makeText(getContext(), String.format("Error on API call: %s", e.getMessage()), Toast.LENGTH_SHORT).show();
                                }
                            }

                            int partyId = ResponseObject.body.partyId;
                            persistentData.setCurrentPartyId(partyId);
                            Helper.changeSelectedNav(getActivity(), R.id.navParty);
                        }
                    });
                }
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), String.format("Error: %s", e.getMessage()), Toast.LENGTH_SHORT).show();
        }
    }
}