package sv.edu.catolica.pupas;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import helpers.LoaderDialog;
import helpers.PersistentData;
import interfaces.APICallback;
import models.ParticipantDetail;
import models.Pupusa;
import models.UpdateDetailBody;
import okhttp3.Call;
import responses.Response;

public class EditPupusasForm extends LinearLayout {
    private int participantId;
    private Activity activity;
    private View view;
    private TextView tvPupusaType;
    private DetailCounter detailCounter;
    private Button btnUpdate, btnDelete;
    private AlertDialog alertDialog;
    private LoaderDialog loaderDialog;
    private PersistentData persistentData;
    private ParticipantDetail participantDetail;

    public EditPupusasForm(Activity context, ParticipantDetail participantDetail, int participantId, Runnable onUpdateFinish) {
        super(context);
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.view = inflater.inflate(R.layout.dialog_edit_pupusas, this, false);

        this.participantId = participantId;
        this.activity = context;
        this.tvPupusaType = this.view.findViewById(R.id.tvPupusaType);
        this.detailCounter = this.view.findViewById(R.id.updateDetailCounter);
        this.btnUpdate = this.view.findViewById(R.id.btnSaveChanges);
        this.btnDelete = this.view.findViewById(R.id.btnDeletePupusas);
        this.loaderDialog = new LoaderDialog(context);
        this.persistentData = new PersistentData(context);
        this.participantDetail = participantDetail;
        this.alertDialog = new MaterialAlertDialogBuilder(getContext())
                .setView(this.view)
                .create();

        this.tvPupusaType.setText(participantDetail.type);
        this.detailCounter.setCounterValue(participantDetail.amount);
        this.btnUpdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handleOnClickListener(onUpdateFinish);
            }
        });
        this.btnDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handleOnDeleteClick(onUpdateFinish);
            }
        });
    }

    public View getView() {
        return this.view;
    }

    public void show() {
        this.alertDialog.show();
    }

    private void handleOnClickListener(Runnable onUpdateFinish) {
        this.updatePupusaAmount(this.detailCounter.getCounterValue(), onUpdateFinish);
    }

    private void handleOnDeleteClick(Runnable onUpdateFinish) {
        this.updatePupusaAmount(0, onUpdateFinish);
    }

    private void updatePupusaAmount(int amount, Runnable onUpdateFinish) {
        this.loaderDialog.start();
        Pupusa pupusa = this.persistentData.getPupusaByName(this.participantDetail.type);
        int partyId = this.persistentData.getCurrentPartyId();
        ParticipantDetail.updatePupusa(
                partyId,
                this.participantId,
                new UpdateDetailBody(pupusa.id, amount),
                new APICallback<Response>() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loaderDialog.dismiss();
                                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Response ResponseObject, @NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loaderDialog.dismiss();
                                if (!ResponseObject.success) {
                                    Toast.makeText(activity, ResponseObject.message, Toast.LENGTH_LONG).show();
                                    return;
                                }
                                alertDialog.dismiss();
                                onUpdateFinish.run();
                            }
                        });
                    }
                }
        );
    }
}
