package models;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import enums.PARTY_STATES;
import interfaces.APICallback;
import okhttp3.Call;
import okhttp3.Callback;
import responses.CreatePartyResponse;
import responses.JoinPartyResponse;
import responses.Response;

public class Party {
    public int id;
    public String code;
    public PARTY_STATES state;
    public List<Participant> participants;

    public Party(int id) {
        this.id = id;
    }

    public void finish(APICallback<Response> cb) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", this.id);
        API.post("/parties/finish", body, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                cb.onFailure(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                Response r = new Response(response.code() == 200);
                cb.onResponse(r, call, response);
            }
        });
    }

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

    public static void join(String code, int userId, APICallback<Response<JoinPartyResponse>> cb) {
        Map<String, Object> body = new HashMap<>();
        body.put("userId", userId);
        body.put("partyCode", code);
        API.post("/parties/join", body, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                cb.onFailure(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                try {
                    Response r = new Response(response.code() == 200);
                    if (r.success) {
                        int partyId = new JSONObject(response.body().string()).getInt("partyId");
                        JoinPartyResponse joinPartyResponse = new JoinPartyResponse(partyId);
                        r.body = joinPartyResponse;
                    }
                    cb.onResponse(r, call, response);
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

    public static void updatePrices(int partyId, List<UpdatePrice> prices, APICallback<Response> cb) {
        String uri = String.format("/parties/%d/prices", partyId);
        API.patch(uri, prices, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                cb.onFailure(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                Response r = new Response(response.code() == 200);
                if (!r.success) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        r.message = jsonObject.getString("message");
                    } catch (JSONException e) {
                        r.message = e.getMessage();
                    }
                }
                cb.onResponse(r, call, response);
            }
        });
    }
}
