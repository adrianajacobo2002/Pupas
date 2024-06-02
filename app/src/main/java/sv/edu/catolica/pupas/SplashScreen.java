package sv.edu.catolica.pupas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import helpers.PersistentData;
import interfaces.APICallback;
import models.API;
import models.Pupusa;
import models.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SplashScreen extends AppCompatActivity {
    PersistentData persistentData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        this.persistentData = new PersistentData(this);
        String authToken = this.persistentData.getAuthToken();
        if (authToken.isEmpty()) {
            goToLoginRegisterScreen();
            return;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("auth_token", authToken);
        API.post("/users/login-by-token", body, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());

                            Gson gson = new Gson();
                            User user = gson.fromJson(jsonObject.getString("user"), User.class);
                            int currentPartyId = jsonObject.getInt("currentPartyId");

                            PersistentData persistentData = new PersistentData(SplashScreen.this);
                            persistentData.saveObject("user", user);
                            persistentData.setCurrentPartyId(currentPartyId);

                            populatePupusas();
                        } catch (JSONException e) {
                            goToLoginRegisterScreen();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
    }

    public void populatePupusas() {
        Pupusa.fetchDefaults(new APICallback<Pupusa[]>() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(Pupusa[] ResponseObject, @NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        persistentData.saveObject("defaultPupusas", ResponseObject);
                        goToMainScreen();
                    }
                });
            }
        });
    }

    public void goToLoginRegisterScreen() {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}