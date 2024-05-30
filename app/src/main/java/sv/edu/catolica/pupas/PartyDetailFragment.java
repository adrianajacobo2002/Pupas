package sv.edu.catolica.pupas;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import helpers.LoaderDialog;
import helpers.PersistentData;
import interfaces.APICallback;
import models.ParticipantDetail;
import models.Pupusa;
import models.User;
import okhttp3.Call;
import responses.ParticipantDetailResponse;
import responses.Response;

public class PartyDetailFragment extends Fragment {
    private TableLayout tblDetails;
    private Button btnNombreDisplay;
    private FloatingActionButton btnAddPupusas;
    private PersistentData persistentData;
    private LoaderDialog loaderDialog;
    private ParticipantDetailResponse participantDetailResponse;
    private Pupusa addPupusaDialogItem;
    private boolean permissionsEstablished = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_party_detail, container, false);
        Activity activity = getActivity();

        this.persistentData = new PersistentData(activity);
        this.loaderDialog = new LoaderDialog(activity);

        this.loadControllers(view);
        this.loadListeners();
        this.loadDetails();

        return view;
    }

    public void loadDetails() {
        this.loaderDialog.start();
        int partyId = this.persistentData.getCurrentPartyId();
        int participantId = getArguments().getInt("id");
        ParticipantDetail.fetch(partyId, participantId, new APICallback<Response<ParticipantDetailResponse>>() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(Response<ParticipantDetailResponse> ResponseObject, @NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        participantDetailResponse = ResponseObject.body;
                        fillScreen();
                        if (!permissionsEstablished)
                            loadPermissions();
                        else
                            loaderDialog.dismiss();
                    }
                });
            }
        });
    }

    private void fillScreen() {
        this.btnNombreDisplay.setText(this.participantDetailResponse.name);

        int currentDetailsCount = this.tblDetails.getChildCount() - 2;
        for (int i = currentDetailsCount; i > 0; i--)
            this.tblDetails.removeViewAt(i);

        for (ParticipantDetail pupusa : this.participantDetailResponse.pupusas) {
            TableRow tableRow = new TableRow(getActivity());
            tableRow.setPadding(10, 10, 10, 10);
            tableRow.setGravity(Gravity.CENTER);

            TextView tipo = makeColumn(pupusa.type);
            TextView cantidad = makeColumn(String.valueOf(pupusa.amount));
            TextView total = makeColumn(String.format("$%s", String.valueOf(pupusa.total)));
            EditDetailBtn btnEdit = new EditDetailBtn(getActivity());
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditPupusas();
                }
            });

            tableRow.addView(tipo);
            tableRow.addView(cantidad);
            tableRow.addView(total);
            tableRow.addView(btnEdit);

            ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 5);
            View divider = new View(getActivity());
            divider.setLayoutParams(layoutParams);
            divider.setBackgroundColor(Color.parseColor("#FF909090"));

            this.tblDetails.addView(tableRow);
            this.tblDetails.addView(divider);
        }
    }

    private TextView makeColumn(String text) {
        TextView column = new TextView(getActivity());
        column.setText(text);
        column.setTextSize(12);
        column.setTextColor(getResources().getColor(R.color.black, null));
        column.setGravity(Gravity.CENTER);

        return column;
    }

    private void loadControllers(View view) {
        this.btnAddPupusas = view.findViewById(R.id.btnAddPupusas);
        this.btnNombreDisplay = view.findViewById(R.id.btnNombreDisplay);
        this.tblDetails = view.findViewById(R.id.tblDetailsContainer);
    }

    private void loadListeners() {
        this.btnAddPupusas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
    }

    private void showAlertDialog() {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        DialogWithDropdownLayout dialogView = new DialogWithDropdownLayout(getContext());

        try {
            // Creating dialog
            AlertDialog alertDialog = new MaterialAlertDialogBuilder(requireContext())
                    .setView(dialogView.getView())
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            addPupusaDialogItem = null;
                        }
                    }).create();

            // Populating dropdown
            Pupusa[] pupusasArr = this.persistentData.getObject("defaultPupusas", Pupusa[].class);
            List<String> pupusas = new ArrayList<>();
            for (Pupusa p : pupusasArr)
                pupusas.add(p.name);
            dialogView.setDropdownValues(pupusas);
            dialogView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String item = (String) parent.getItemAtPosition(position);
                    int i = pupusas.indexOf(item);
                    addPupusaDialogItem = pupusasArr[i];
                }
            });

            // Firing addition handler
            dialogView.setOnAddButtonClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int counterAmount = dialogView.getCounterValue();
                    addPupusa(counterAmount, alertDialog);
                }
            });

            alertDialog.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addPupusa(int amount, AlertDialog dialog) {
        loaderDialog.start();

        int pupusaId = this.addPupusaDialogItem != null ? this.addPupusaDialogItem.id : 0;
        int partyId = this.persistentData.getCurrentPartyId();
        int participantId = getArguments().getInt("id");
        Double price = persistentData.getPupusaPrice(pupusaId);

        ParticipantDetail.addPupusa(amount, partyId, pupusaId, participantId, price, new APICallback<Response>() {
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
                        loaderDialog.dismiss();
                        dialog.dismiss();
                        if (ResponseObject.success)
                            loadDetails();
                        else
                            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showEditPupusas() {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_pupusas, null);
        new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .show();
    }

    private void loadPermissions() {
        try {
            User loggedUser = this.persistentData.getObject("user", User.class);
            String loggedUserName = loggedUser.getFullName();
            String participantName = this.participantDetailResponse.name;
            if (!loggedUserName.equals(participantName)) {
                this.btnAddPupusas.setVisibility(View.GONE);
            }

            loaderDialog.dismiss();
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}