package models;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interfaces.APICallback;
import okhttp3.Call;
import okhttp3.Callback;
import responses.HistoryResponseItem;
import responses.RegisterResponse;
import responses.Response;

public class User {
    public int id;
    public String names;
    @SerializedName("last_names")
    public String lastNames;
    public String email;

    public User(String names, String lastNames, String email) {
        this.names = names;
        this.lastNames = lastNames;
        this.email = email;
    }

    public String getFullName() {
        return String.format("%s %s", this.names, this.lastNames);
    }

    public void register(String password, APICallback<Response<RegisterResponse>> cb) {
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
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
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

    public void fetchHistory(APICallback<Response<List<HistoryResponseItem>>> cb) {
        API.get(String.format("/users/%d/history", this.id), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                cb.onFailure(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                Response<List<HistoryResponseItem>> r = new Response(response.code() == 200);
                String jsonString = response.body().string();
                if (!r.success) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        r.message = jsonObject.getString("message");
                    } catch (JSONException e) {
                        r.message = e.getMessage();
                    }
                } else {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<HistoryResponseItem>>() {}.getType();
                    r.body = gson.fromJson(jsonString, listType);
                }

                cb.onResponse(r, call, response);
            }
        });
    }
}
