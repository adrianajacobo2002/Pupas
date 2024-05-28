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
import responses.CreatePartyResponse;
import responses.Response;

public class Party {
    public int id;
    public String code;
    public User host;

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
}
