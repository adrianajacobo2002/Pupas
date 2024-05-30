package models;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;

import interfaces.APICallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Pupusa {
    public int id;
    public String abbreviation;
    public String name;
    public Double price;

    public static void fetchDefaults(APICallback<Pupusa[]> cb) {
        API.get("/pupusas", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                cb.onFailure(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Gson gson = new Gson();
                String jsonString = response.body().string();
                Pupusa[] pupusas = gson.fromJson(jsonString, Pupusa[].class);
                cb.onResponse(pupusas, call, response);
            }
        });
    }
}
