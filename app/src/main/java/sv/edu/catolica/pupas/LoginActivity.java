package sv.edu.catolica.pupas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import helpers.Helper;
import helpers.LoaderDialog;
import helpers.PersistentData;
import models.API;
import models.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.etEmail = findViewById(R.id.etLoginEmail);
        this.etPassword = findViewById(R.id.etLoginPassword);
        this.btnLogin = findViewById(R.id.btnLogin);
        this.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        this.tvGoToRegister = findViewById(R.id.tvGoToRegister);
        this.tvGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSingUp();
            }
        });
    }

    public void login() {
        Helper.hideKeyboard(this);

        String email = this.etEmail.getText().toString();
        String password = this.etPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.all_inputs_required), Toast.LENGTH_SHORT).show();
            return;
        }

        LoaderDialog loaderDialog = new LoaderDialog(this);
        loaderDialog.start();

        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        API.post("/users/login", body, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loaderDialog.dismiss();
                        String message = e.getMessage();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loaderDialog.dismiss();
                        if (response.code() != 200) {
                            Toast.makeText(LoginActivity.this, getString(R.string.incorrect_credentials), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            Gson gson = new Gson();
                            JSONObject json = new JSONObject(response.body().string());
                            User user = gson.fromJson(json.getString("user"), User.class);

                            PersistentData persistentData = new PersistentData(LoginActivity.this);
                            persistentData.saveObject(getString(R.string.user_sp_key), user);

                            String auth_token = json.getJSONObject("user").getString("auth_token");
                            persistentData.setAuthToken(auth_token);

                            int currentPartyId = json.getInt("currentPartyId");
                            if (currentPartyId > 0)
                                persistentData.setCurrentPartyId(currentPartyId);

                            goHome();
                        } catch (IOException e) {
                            Snackbar.make(findViewById(R.id.loginLayout), String.format("IOException: %s", e.getMessage()), Snackbar.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            Snackbar.make(findViewById(R.id.loginLayout), String.format("JSONException: %s", e.getMessage()), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private void goHome(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToSingUp() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
}