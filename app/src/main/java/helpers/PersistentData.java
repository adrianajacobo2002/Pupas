package helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

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

    public void saveObject(String name, Object object) {
        Gson gson = new Gson();
        SharedPreferences.Editor editor = this.sp.edit();
        editor.putString(name, gson.toJson(object));
        editor.apply();
    }

    public <T> T getObject(String name, Class<T> typeOfT) throws Exception {
        String json = this.sp.getString(name, "");
        if (json.isEmpty()) throw new Exception("Object name does not exists");

        Gson gson = new Gson();
        return gson.fromJson(json, typeOfT);
    }
}
