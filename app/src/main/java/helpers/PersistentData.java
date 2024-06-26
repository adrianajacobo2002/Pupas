package helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import models.CustomPrices;
import models.Pupusa;
import sv.edu.catolica.pupas.R;

public class PersistentData {
    private SharedPreferences sp;
    private CustomPrices customPrices;
    private Activity context;

    public PersistentData(Activity context) {
        this.sp = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences_file_name), Context.MODE_PRIVATE);
        this.context = context;
        this.customPrices = new CustomPrices(context);
    }

    public String getResourcesString(int id) {
        return this.context.getResources().getString(id);
    }

    public void setString(String name, String value) {
        SharedPreferences.Editor editor = this.sp.edit();
        editor.putString(name, value);
        editor.apply();
    }

    public String getString(String name) {
        return this.sp.getString(name, "");
    }

    public void setInt(String name, int value) {
        SharedPreferences.Editor editor = this.sp.edit();
        editor.putInt(name, value);
        editor.apply();
    }

    public int getInt(String name) {
        return this.sp.getInt(name, 0);
    }

    public void remove(String name) {
        SharedPreferences.Editor editor = this.sp.edit();
        editor.remove(name);
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

    public String getAuthToken() {
        return this.getString(this.getResourcesString(R.string.auth_token_sp_key));
    }

    public void setAuthToken(String token) {
        this.setString(this.getResourcesString(R.string.auth_token_sp_key), token);
    }

    public int getCurrentPartyId() {
        return this.getInt(this.getResourcesString(R.string.current_party_id_sp_key));
    }

    public void setCurrentPartyId(int id) {
        this.setInt(this.getResourcesString(R.string.current_party_id_sp_key), id);
    }

    public Double getPupusaPrice(int id) {
        try {
            Pupusa[] pupusasArr = this.getObject("defaultPupusas", Pupusa[].class);
            List<Pupusa> pupusas = Arrays.asList(pupusasArr);
            Pupusa pupusa = pupusas.stream()
                    .filter(p -> p.id == id)
                    .findAny()
                    .orElse(null);
            return pupusa.price;
        } catch (Exception e) {
            return Double.valueOf(0);
        }
    }

    public Pupusa getPupusaByName(String name) {
        try {
            Pupusa[] pupusasArr = this.getObject("defaultPupusas", Pupusa[].class);
            List<Pupusa> pupusas = Arrays.asList(pupusasArr);
            Pupusa pupusa = pupusas.stream()
                    .filter(p -> p.name.equals(name))
                    .findAny()
                    .orElse(null);
            return pupusa;
        } catch (Exception e) {
            return null;
        }
    }

    public double getPupusaPriceInParty(int pupusaId, int partyId) {
        double customPrice = this.customPrices.getCustomPrice(pupusaId, partyId);

        if (customPrice == 0)
            customPrice = this.getPupusaPrice(pupusaId);

        return customPrice;
    }

    public boolean setPupusaCustomPrice(int pupusaId, int partyId, double price) {
        return this.customPrices.savePrice(pupusaId, partyId, price);
    }
}
