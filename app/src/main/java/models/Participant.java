package models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import interfaces.APICallback;
import okhttp3.Call;
import okhttp3.Callback;
import responses.Response;

public class Participant {
    public int id;
    @SerializedName("user_id")
    public int userId;
    public String names;
    @SerializedName("last_names")
    public String lastNames;
    public double total;
    @SerializedName("is_host")
    public boolean isHost;

    public String getFullName() {
        return String.format("%s %s", this.names, this.lastNames);
    }

    public static void updateDrink(int partyId, int participantId, Double price, APICallback<Response> cb) {
        String uri = String.format("/parties/%d/participant/%d/drink", partyId, participantId);
        Map<String, Object> body = new HashMap<>();
        body.put("price", price);
        API.patch(uri, body, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                cb.onFailure(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                String jsonString = response.body().string();
                cb.onResponse(new Response(response.code() == 200), call, response);
            }
        });
    }
}
