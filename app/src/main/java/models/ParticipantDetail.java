package models;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import interfaces.APICallback;
import okhttp3.Call;
import okhttp3.Callback;
import responses.ParticipantDetailResponse;
import responses.Response;

public class ParticipantDetail {
    public int id;
    public String type;
    public int amount;
    public Double total;

    public static void fetch(int partyId, int participantId, APICallback<Response<ParticipantDetailResponse>> cb) {
        API.get(String.format("/parties/%d/participant/%d", partyId, participantId), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                cb.onFailure(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                Gson gson = new Gson();
                String jsonString = response.body().string();
                ParticipantDetailResponse participantDetailResponse =
                        gson.fromJson(jsonString, ParticipantDetailResponse.class);
                Response<ParticipantDetailResponse> r =
                        new Response(response.code() == 200, "", participantDetailResponse);
                cb.onResponse(r, call, response);
            }
        });
    }

    public static void addPupusa(int amount, int partyId, int pupusaId, int participantId, Double price, APICallback<Response> cb) {
        Map<String, Object> body = new HashMap<>();
        body.put("amount", amount);
        body.put("partyId", partyId);
        body.put("pupusaId", pupusaId);
        body.put("participantId", participantId);
        body.put("price", price);
        API.post("/parties/pupusas", body, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                cb.onResponse(new Response(response.code() == 201), call, response);
            }
        });
    }
}
