package sv.edu.catolica.pupas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class EditDetailBtn extends LinearLayout {
    private Button btnEditPupusas;

    public EditDetailBtn(Context context) {
        super(context);
        init(context);
    }

    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.edit_detail_btn, this);

        this.btnEditPupusas = findViewById(R.id.btnEditPupusas);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        this.btnEditPupusas.setOnClickListener(l);
    }
}
