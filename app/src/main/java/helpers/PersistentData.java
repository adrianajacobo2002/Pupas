package helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import sv.edu.catolica.pupas.R;

public class PersistentData {
    private SharedPreferences sp;
    private Activity context;

    public PersistentData(Activity context) {
        this.sp = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences_file_name), Context.MODE_PRIVATE);
        this.context = context;
    }

    public String getAuthToken() {
        return this.sp.getString(this.context.getResources().getString(R.string.auth_token_sp_key), "");
    }

    public void setAuthToken(String token) {
        SharedPreferences.Editor editor = this.sp.edit();
        editor.putString(this.context.getResources().getString(R.string.auth_token_sp_key), token);
        editor.apply();
    }
}
