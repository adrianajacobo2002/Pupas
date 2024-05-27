package models;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import interfaces.APICallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import responses.RegisterResponse;

public class User {
    public int id;
    public String names;
    public String lastNames;
    public String email;

    public User(String names, String lastNames, String email) {
        this.names = names;
        this.lastNames = lastNames;
        this.email = email;
    }

    public User(int id, String names, String lastNames, String email) {
        this.id = id;
        this.names = names;
        this.lastNames = lastNames;
        this.email = email;
    }

    public void register(String password, APICallback<responses.Response<RegisterResponse>> cb) {
        if (this.id != 0) throw new RuntimeException("You cannot register an existing user");

        Map<String, Object> body = new HashMap<>();
        body.put("names", this.names);
        body.put("last_names", this.lastNames);
        body.put("email", this.email);
        body.put("password", password);
        API.post("/users/sign-up", body, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                cb.onFailure(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    responses.Response<RegisterResponse> r = new responses.Response<>(response.code() == 201);
                    if (r.success) {
                        JSONObject json = new JSONObject(response.body().string());
                        String token = json.getString("auth_token");
                        RegisterResponse rr = new RegisterResponse(token);
                        r.body = rr;
                    } else {
                        if (response.code() == 400) {
                            r.message = "User already exists";
                        } else {
                            r.message = "Unexpected error. Try again later";
                        }
                    }
                    cb.onResponse(r, call, response);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
