package sv.edu.catolica.pupas;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.List;

public class DialogWithDropdownLayout extends LinearLayout {
    private DetailCounter detailCounter;
    private AutoCompleteTextView dropdown;
    private Button btnAdd;
    private View view;
    public DialogWithDropdownLayout(Context context) {
        super(context);
        init(context);
    }

    public DialogWithDropdownLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.view = inflater.inflate(R.layout.dialog_with_dropdown_layout, this, false);

        this.detailCounter = this.view.findViewById(R.id.detailCounter);
        this.dropdown = this.view.findViewById(R.id.dropdown);
        this.btnAdd = this.view.findViewById(R.id.btnAdd);
    }

    public int getCounterValue() {
        return this.detailCounter.getCounterValue();
    }

    public void setDropdownValues(List<String> values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, values);
        this.dropdown.setAdapter(adapter);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener l) {
        this.dropdown.setOnItemClickListener(l);
    }

    public void setOnAddButtonClick(View.OnClickListener l) {
        this.btnAdd.setOnClickListener(l);
    }

    public View getView() {
        return this.view;
    }
}
