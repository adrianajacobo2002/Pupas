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

import helpers.Helper;
import helpers.LoaderDialog;
import helpers.PersistentData;
import interfaces.APICallback;
import models.Participant;
import models.ParticipantDetail;
import models.Pupusa;
import models.User;
import okhttp3.Call;
import responses.ParticipantDetailResponse;
import responses.Response;

public class PartyDetailFragment extends Fragment {
    private int participantId, partyIdForHistory;
    private TableLayout tblDetails;
    private Button btnNombreDisplay, btnDetailBack, btnAddBebida, btnDeleteBebida;
    private FloatingActionButton btnAddPupusas;
    private EditText etDrinkPrice;
    private PersistentData persistentData;
    private LoaderDialog loaderDialog;
    private ParticipantDetailResponse participantDetailResponse;
    private Pupusa addPupusaDialogItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_party_detail, container, false);
        Activity activity = getActivity();

        this.participantId = getArguments().getInt("id");
        this.partyIdForHistory = getArguments().getInt("partyId");
        this.persistentData = new PersistentData(activity);
        this.loaderDialog = new LoaderDialog(activity);

        this.loadControllers(view);
        this.loadListeners();
        this.loadDetails();

        return view;
    }

    public void loadDetails() {
        this.loaderDialog.start();
        int partyIdArg = getArguments().getInt("partyId");
        boolean detailsRequested = partyIdArg > 0;
        int partyId = detailsRequested ? partyIdArg : this.persistentData.getCurrentPartyId();
        ParticipantDetail.fetch(partyId, this.participantId, new APICallback<Response<ParticipantDetailResponse>>() {
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
            public void onResponse(Response<ParticipantDetailResponse> ResponseObject, @NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        participantDetailResponse = ResponseObject.body;
                        fillScreen();
                        loadPermissions();
                        loaderDialog.dismiss();
                    }
                });
            }
        });
    }

    private void fillScreen() {
        this.btnNombreDisplay.setText(this.participantDetailResponse.name);
        this.etDrinkPrice.setText(this.participantDetailResponse.drink.toString());

        int currentDetailsCount = this.tblDetails.getChildCount() - 2;
        for (int i = currentDetailsCount; i > 0; i--)
            this.tblDetails.removeViewAt(i);

        for (ParticipantDetail pupusa : this.participantDetailResponse.pupusas) {
            List<View> columns = new ArrayList<>();
            columns.add(makeColumn(pupusa.type));
            columns.add(makeColumn(String.valueOf(pupusa.amount)));
            columns.add(makeColumn(String.format("$%.2f", pupusa.total)));
            EditDetailBtn btnEdit = new EditDetailBtn(getActivity());
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditPupusas(pupusa);
                }
            });
            if (!this.hasPermissions())
                btnEdit.setVisibility(View.INVISIBLE);
            columns.add(btnEdit);

            TableRow tableRow = makeTableRow(columns);

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

    private TableRow makeTableRow(List<View> columns) {
        TableRow tableRow = new TableRow(getActivity());
        tableRow.setPadding(10, 10, 10, 10);
        tableRow.setGravity(Gravity.CENTER);

        for (View column: columns)
            tableRow.addView(column);

        return tableRow;
    }

    private void loadControllers(View view) {
        this.btnAddPupusas = view.findViewById(R.id.btnAddPupusas);
        this.btnNombreDisplay = view.findViewById(R.id.btnNombreDisplay);
        this.tblDetails = view.findViewById(R.id.tblDetailsContainer);
        this.btnDetailBack = view.findViewById(R.id.btnDetailBack);
        this.etDrinkPrice = view.findViewById(R.id.etDrinkPrice);
        this.btnAddBebida = view.findViewById(R.id.btnAddBebidas);
        this.btnDeleteBebida = view.findViewById(R.id.btnDeleteBebidas);
    }

    private void loadListeners() {
        this.btnAddPupusas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
        this.btnDetailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (partyIdForHistory > 0)
                    Helper.replaceFragment(getActivity(), PartyHistoryFragment.class, null);
                else
                    Helper.changeSelectedNav(getActivity(), R.id.navParty);
            }
        });
        this.btnAddBebida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String etDrinkPriveValue = etDrinkPrice.getText().toString();
                Double price = Double.valueOf(
                        etDrinkPriveValue.isEmpty()
                        ? "0"
                        : etDrinkPriveValue);
                updateDrink(price);
            }
        });
        this.btnDeleteBebida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double price = Double.valueOf(0);
                updateDrink(price);
            }
        });
    }

    private void showAlertDialog() {
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

    private void showEditPupusas(ParticipantDetail pupusa) {
        EditPupusasForm editPupusasForm = new EditPupusasForm(
                getActivity(),
                pupusa,
                this.participantId,
                new Runnable() {
                    @Override
                    public void run() {
                        loadDetails();
                    }
                }
        );
        editPupusasForm.show();
    }

    private boolean hasPermissions() {
        if (this.partyIdForHistory > 0) return false;

        User loggedUser = null;
        try {
            loggedUser = this.persistentData.getObject("user", User.class);
            String loggedUserName = loggedUser.getFullName();
            String participantName = this.participantDetailResponse.name;
            return loggedUserName.equals(participantName);
        } catch (Exception e) {
            return false;
        }
    }

    private void loadPermissions() {
        try {
            if (!this.hasPermissions()) {
                this.btnAddPupusas.setVisibility(View.GONE);
                this.btnAddBebida.setVisibility(View.GONE);
                this.btnDeleteBebida.setVisibility(View.GONE);
                this.etDrinkPrice.setEnabled(false);
            }

            loaderDialog.dismiss();
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDrink(Double price) {
        Double priceFixed = Double.valueOf(String.format("%.2f", price));
        if (priceFixed >= 100) {
            String priceExceededMessage = this.persistentData.getResourcesString(R.string.participant_details_drink_price_exceeded);
            Toast.makeText(getContext(), priceExceededMessage, Toast.LENGTH_SHORT).show();
            return;
        }

        this.loaderDialog.start();
        Helper.hideKeyboard(getActivity());
        this.etDrinkPrice.clearFocus();
        int partyId = this.persistentData.getCurrentPartyId();
        Participant.updateDrink(
                partyId,
                this.participantId,
                priceFixed,
                new APICallback<Response>() {
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
                                if (!ResponseObject.success) {
                                    String message = persistentData.getResourcesString(R.string.participant_details_drink_error);
                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (!price.toString().equals(priceFixed.toString())) {
                                    etDrinkPrice.setText(priceFixed.toString());
                                }
                            }
                        });
                    }
                }
        );
    }
}