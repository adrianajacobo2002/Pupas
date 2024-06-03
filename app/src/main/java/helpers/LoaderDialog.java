package helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;

import sv.edu.catolica.pupas.R;

public class LoaderDialog {

    private Activity activity;
    private AlertDialog ad;

    public LoaderDialog(Activity context) {
        this.activity = context;
    }

    public void start() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);

        LayoutInflater inflater = this.activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loader, null));
        builder.setCancelable(false);

        this.ad = builder.create();
        this.ad.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.ad.show();
    }

    public void dismiss() {
        this.ad.dismiss();
    }
}
