package sv.edu.catolica.pupas;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class DetailCounter extends LinearLayout {
    private EditText counter;
    public DetailCounter(Context context) {
        super(context);
        init(context);
    }

    public DetailCounter(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.component_detail_counter, this);

        this.counter = view.findViewById(R.id.counter);
        Button pupusaPlus = view.findViewById(R.id.btnPupusasCounterPlus),
                pupusaMinus = view.findViewById(R.id.btnPupusasCounterMinus);
        pupusaMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int counterAmount = Integer.parseInt(counter.getText().toString());
                if (counterAmount > 1)
                    counterAmount--;
                counter.setText(String.valueOf(counterAmount));
            }
        });
        pupusaPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int counterAmount = Integer.parseInt(counter.getText().toString());
                if (counterAmount < 50)
                    counterAmount++;
                counter.setText(String.valueOf(counterAmount));
            }
        });
    }

    public int getCounterValue () {
        return Integer.parseInt(this.counter.getText().toString());
    }

    public void setCounterValue (int value) {
        this.counter.setText(String.valueOf(value));
    }
}
