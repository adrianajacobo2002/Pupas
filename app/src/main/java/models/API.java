package models;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class API {
    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType ApplicationJSONType = MediaType.parse("application/json");
    private static final String host = "https://35d3-179-51-60-153.ngrok-free.app";

    private API() {}

    public static void post(String uri, Map<String, Object> body, Callback cb) {
        String bodyJSON = new JSONObject(body).toString();
        Request r = new Request.Builder()
                .url(host + uri)
                .post(RequestBody.create(bodyJSON, ApplicationJSONType))
                .build();
        Call call = client.newCall(r);
        call.enqueue(cb);
    }
}
