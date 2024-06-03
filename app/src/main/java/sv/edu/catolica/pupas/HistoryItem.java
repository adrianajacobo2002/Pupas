package sv.edu.catolica.pupas;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import helpers.Helper;
import helpers.PersistentData;
import models.Participant;
import models.User;
import responses.HistoryResponseItem;

public class HistoryItem extends FrameLayout {
    private View view;
    private TextView tvPartyName, tvHostName;
    private Button btnResume;
    private PersistentData persistentData;
    private HistoryResponseItem party;
    public HistoryItem(Activity context, HistoryResponseItem party) {
        super(context);
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.view = inflater.inflate(R.layout.component_history_item, this);

        this.party = party;
        this.persistentData = new PersistentData(context);

        this.tvPartyName = this.view.findViewById(R.id.tvPartyName);
        this.tvHostName = this.view.findViewById(R.id.tvHostName);
        this.btnResume = this.view.findViewById(R.id.btnResume);

        this.fillData();
    }

    public View getView() {
        return this.view;
    }

    public void setOnSeeHistoryClickListener(View.OnClickListener l) {
        if (this.btnResume == null) return;

        this.btnResume.setOnClickListener(l);
    }

    private void fillData() {
        try {
            this.tvPartyName.setText(this.party.name);
            User loggedUser = this.persistentData.getObject("user", User.class);
            Participant host = this.party.participants.get(0);
            String hostName = loggedUser.id == host.userId
                    ? this.persistentData.getResourcesString(R.string.you)
                    : host.getFullName();
            this.tvHostName.setText(hostName);
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
