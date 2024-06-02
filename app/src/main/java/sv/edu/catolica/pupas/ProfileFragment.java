package sv.edu.catolica.pupas;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import helpers.Helper;
import helpers.LoaderDialog;
import helpers.PersistentData;
import interfaces.APICallback;
import models.User;
import okhttp3.Call;
import responses.HistoryResponseItem;
import responses.Response;


public class ProfileFragment extends Fragment {
    private TextView tvFullName, tvEmail;
    private Button btnLogOut;
    private LinearLayout historyContainerLayout;
    private PersistentData persistentData;
    private LoaderDialog loaderDialog;
    private List<HistoryResponseItem> history;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        this.persistentData = new PersistentData(getActivity());
        this.loaderDialog = new LoaderDialog(getActivity());

        this.loadController(view);
        this.loadListeners();

        try {
            User user = this.persistentData.getObject(getString(R.string.user_sp_key), User.class);
            this.tvFullName.setText(String.format("%s %s", user.names, user.lastNames));
            this.tvEmail.setText(user.email);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error trying to get logged user", Toast.LENGTH_SHORT).show();
        }

        this.loadHistory();

        return view;
    }

    private void loadController(View view) {
        this.tvFullName = view.findViewById(R.id.tvProfileName);
        this.tvEmail = view.findViewById(R.id.tvEmail);
        this.btnLogOut = view.findViewById(R.id.btnLogOut);
        this.historyContainerLayout = view.findViewById(R.id.historyContainerLayout);
    }

    private void loadListeners() {
        this.btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persistentData.remove(persistentData.getResourcesString(R.string.auth_token_sp_key));
                persistentData.remove(persistentData.getResourcesString(R.string.user_sp_key));
                persistentData.remove(persistentData.getResourcesString(R.string.current_party_id_sp_key));

                Intent intent = new Intent(getContext(), StartActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    private void loadHistory() {
        this.loaderDialog.start();
        try {
            User user = this.persistentData.getObject("user", User.class);
            user.fetchHistory(new APICallback<Response<List<HistoryResponseItem>>>() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Response<List<HistoryResponseItem>> ResponseObject, @NonNull Call call, @NonNull okhttp3.Response response) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!ResponseObject.success) {
                                Toast.makeText(getContext(), "Error trying to get history", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            history = ResponseObject.body;
                            loadHistoryToUI();
                        }
                    });
                }
            });
        } catch (Exception e) {
            Toast.makeText(getActivity(), "History could not be retrieved", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadHistoryToUI() {
        if (this.history == null) return;

        for (HistoryResponseItem h : this.history) {
            HistoryItem historyItem = new HistoryItem(getActivity(), h);
            historyItem.setOnSeeHistoryClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    persistentData.saveObject("currentHistoryParty", h);
                    Helper.replaceFragment(
                            getActivity(),
                            PartyHistoryFragment.class,
                            null
                    );
                }
            });
            this.historyContainerLayout.addView(historyItem);
        }

        this.loaderDialog.dismiss();
    }
}