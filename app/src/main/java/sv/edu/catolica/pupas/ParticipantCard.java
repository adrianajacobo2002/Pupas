package sv.edu.catolica.pupas;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ParticipantCard extends FrameLayout {
    private TextView tvName, tvTotal;
    private LinearLayout cardContainerLayout;
    public ParticipantCard(Context context, String name, Double total) {
        super(context);
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sample_participant_card, this);

        this.tvName = findViewById(R.id.tvName);
        this.tvTotal = findViewById(R.id.tvTotal);
        this.cardContainerLayout = findViewById(R.id.cardContainerLayout);

        this.tvName.setText(name);
        this.tvTotal.setText(String.format("$%s", total.toString()));
    }

    public void makeHostCard() {
        int color = getResources().getColor(R.color.naranja, null);
        this.cardContainerLayout.setBackgroundColor(color);
    }
}