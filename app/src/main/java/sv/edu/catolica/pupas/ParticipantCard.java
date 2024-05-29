package sv.edu.catolica.pupas;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ParticipantCard extends FrameLayout {
    private TextView tvName, tvTotal;
    public ParticipantCard(Context context, String name, Double total) {
        super(context);
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sample_participant_card, this);

        this.tvName = findViewById(R.id.tvName);
        this.tvTotal = findViewById(R.id.tvTotal);

        this.tvName.setText(name);
        this.tvTotal.setText(String.format("$%s", total.toString()));
    }
}