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

import java.io.IOException;

import helpers.LoaderDialog;
import helpers.PersistentData;
import interfaces.APICallback;
import models.User;
import okhttp3.Call;
import okhttp3.Response;
import responses.RegisterResponse;

public class RegisterActivity extends AppCompatActivity {
    public Button btnRegister;

    public EditText etNames, etLastNames, etEmail, etPassword;
    public TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.etNames = findViewById(R.id.etNames);
        this.etLastNames = findViewById(R.id.etLastNames);
        this.etEmail = findViewById(R.id.etEmail);
        this.etPassword = findViewById(R.id.etPassword);

        this.btnRegister = findViewById(R.id.btnRegister);
        this.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBtnRegisterClick();
            }
        });

        this.tvLogin = findViewById(R.id.tvLogin);
        this.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleTvLoginClick();
            }
        });
    }

    public void handleBtnRegisterClick() {
        try {
            this.btnRegister.setEnabled(false);

            String names = this.etNames.getText().toString();
            String lastNames = this.etLastNames.getText().toString();
            String email = this.etEmail.getText().toString();
            String password = this.etPassword.getText().toString();
            User newUser = new User(names, lastNames, email);

            if (names.isEmpty() || lastNames.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill all inputs to complete register", Toast.LENGTH_SHORT).show();
                this.btnRegister.setEnabled(true);
                return;
            }

            LoaderDialog loaderDialog = new LoaderDialog(this);
            loaderDialog.start();
            newUser.register(password, new APICallback<responses.Response<RegisterResponse>>() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                }

                @Override
                public void onResponse(responses.Response<RegisterResponse> ResponseObject, @NonNull Call call, @NonNull Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loaderDialog.dismiss();

                            if (!ResponseObject.success) {
                                Toast.makeText(RegisterActivity.this, ResponseObject.message, Toast.LENGTH_LONG).show();
                                btnRegister.setEnabled(true);
                                return;
                            }

                            PersistentData pd = new PersistentData(RegisterActivity.this);
                            pd.setAuthToken(ResponseObject.body.authToken);

                            Intent intent = new Intent(RegisterActivity.this, SplashScreen.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            });
        } catch (Exception e) {
            Snackbar.make(findViewById(R.id.register_layout), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    public void handleTvLoginClick() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}