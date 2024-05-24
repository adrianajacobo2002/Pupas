package sv.edu.catolica.pupas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btnLogin = (Button) findViewById(R.id.btnGoLogin);
        btnRegister = (Button) findViewById(R.id.btnGoRegister);

    }

    public void  StartLogin(View v){
        Intent ventana = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(ventana);
    }

    public void  StartRegister(View v){
        Intent ventana = new Intent(StartActivity.this, RegisterActivity.class);
        startActivity(ventana);
    }
}