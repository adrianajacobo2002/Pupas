package sv.edu.catolica.pupas;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import helpers.Helper;
import helpers.LoaderDialog;
import helpers.PersistentData;
import interfaces.APICallback;
import models.Party;
import models.Pupusa;
import models.UpdatePrice;
import okhttp3.Call;
import responses.Response;


public class PupusasListFragment extends Fragment {
    private boolean hasPermissions;
    private LoaderDialog loaderDialog;
    private PersistentData persistentData;
    private TableLayout tblPupusas;
    private Button btnUpdatePrices, btnBack;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pupusas_list, container, false);

        this.btnUpdatePrices = view.findViewById(R.id.btnUpdatePrices);
        this.tblPupusas = view.findViewById(R.id.tblPupusas);
        this.persistentData = new PersistentData(getActivity());
        this.loaderDialog = new LoaderDialog(getActivity());
        this.hasPermissions = getArguments().getBoolean("permitted");

        this.btnBack = view.findViewById(R.id.btnBack);
        this.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.replaceFragment(
                        getActivity(),
                        PartyFragment.class,
                        null
                );
            }
        });

        try {
            Pupusa[] pupusas = this.persistentData.getObject("defaultPupusas", Pupusa[].class);
            for (int i = 0; i < pupusas.length; i++) {
                Pupusa p = pupusas[i];
                int tblRows = this.tblPupusas.getChildCount();

                TableRow row = getTableRow(p);
                this.tblPupusas.addView(row, tblRows);
                this.tblPupusas.addView(getDivider(), tblRows + 1);
            }

            loadPermissions();
        } catch (Exception e) {
            String message = e.getMessage();
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void handleBtnUpdatePricesClick() {
        this.btnUpdatePrices.setEnabled(false);
        loaderDialog.start();

        List<Pupusa> prices = this.getTablePrices();
        List<UpdatePrice> pricesToDB = new ArrayList<>();
        int partyId = this.persistentData.getCurrentPartyId();
        for (Pupusa p : prices) {
            UpdatePrice up = new UpdatePrice(p.id, p.price);
            pricesToDB.add(up);
        }

        Party.updatePrices(partyId, pricesToDB, new APICallback<Response>() {
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
            public void onResponse(Response ResponseObject, @NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!ResponseObject.success) {
                            Toast.makeText(getContext(), ResponseObject.message, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (Pupusa p : prices) {
                            persistentData.setPupusaCustomPrice(p.id, partyId, p.price);
                        }

                        loaderDialog.dismiss();
                        btnUpdatePrices.setEnabled(true);
                        Toast.makeText(getActivity(), "Prices saved correctly", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private TableRow getTableRow(Pupusa pupusa) {
        TableRow.LayoutParams layoutParams =
                new TableRow.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
        layoutParams.weight = 1;
        TableRow tableRow = new TableRow(getActivity());
        tableRow.setGravity(Gravity.CENTER);
        tableRow.setPadding(10,10,10,10);
        tableRow.setLayoutParams(layoutParams);

        tableRow.addView(getPupusaTypeTV(pupusa.name));
        tableRow.addView(getPupusaPriceET(pupusa.id));

        return tableRow;
    }

    private TextView getPupusaTypeTV(String type) {
        TextView textView = new TextView(getActivity());
        textView.setGravity(Gravity.CENTER);
        textView.setText(type);
        textView.setTextColor(getResources().getColor(R.color.black, null));
        textView.setTextSize(14);

        return textView;
    }

    private EditText getPupusaPriceET(int pupusaId) {
        int partyId = this.persistentData.getCurrentPartyId();
        double price = this.persistentData.getPupusaPriceInParty(pupusaId, partyId);

        EditText editText = new EditText(getActivity());
        editText.setGravity(Gravity.CENTER);
        editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        editText.setTextColor(getResources().getColor(R.color.black, null));
        editText.setTextSize(14);
        editText.setText(String.valueOf(price));

        if (!this.hasPermissions)
            editText.setEnabled(false);

        return editText;
    }

    private View getDivider() {
        ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                3
        );
        View divider = new View(getActivity());
        divider.setLayoutParams(layoutParams);
        divider.setBackgroundColor(Color.parseColor("#FF909090"));

        return divider;
    }

    private List<Pupusa> getTablePrices() {
        List<Pupusa> prices = new ArrayList<>();

        int childCount = this.tblPupusas.getChildCount();
        for (int i = 2; i < childCount - 1; i += 2) {
            TableRow tblRow = (TableRow) this.tblPupusas.getChildAt(i);
            TextView name = (TextView) tblRow.getChildAt(0);
            EditText price = (EditText) tblRow.getChildAt(1);

            Pupusa p = this.persistentData.getPupusaByName(name.getText().toString());
            p.price = Double.valueOf(price.getText().toString());
            prices.add(p);
        }

        return prices;
    }

    private void loadPermissions() {
        if (!this.hasPermissions) {
            this.btnUpdatePrices.setVisibility(View.GONE);
            return;
        }

        this.btnUpdatePrices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBtnUpdatePricesClick();
            }
        });
    }
}
