package models;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interfaces.APICallback;
import okhttp3.Call;
import okhttp3.Callback;
import responses.CreatePartyResponse;
import responses.Response;

public class Party {
    public int id;
    public String code;
    public List<Participant> participants;

    public static void start(String name, int hostId, APICallback<Response<CreatePartyResponse>> cb) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("host_id", hostId);
        API.post("/parties", body, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                cb.onFailure(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    int partyId = jsonObject.getInt("id");
                    CreatePartyResponse createPartyResponse = new CreatePartyResponse(partyId);
                    Response<CreatePartyResponse> responseObject =
                            new Response<>(
                                    response.code() == 201,
                                    "Party created successfully",
                                    createPartyResponse
                            );
                    cb.onResponse(responseObject, call, response);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static void fetchParty(int id, APICallback<Response<Party>> cb) {
        API.get(String.format("/parties/%d", id), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                cb.onFailure(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                Gson gson = new Gson();
                String jsonString = response.body().string();
                Party party = gson.fromJson(jsonString, Party.class);
                Response<Party> r = new Response(response.code() == 200);
                r.body = party;
                cb.onResponse(r, call, response);
            }
        });
    }
}
