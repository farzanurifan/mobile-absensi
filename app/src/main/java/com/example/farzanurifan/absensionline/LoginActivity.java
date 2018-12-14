package com.example.farzanurifan.absensionline;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    EditText id_user, password_user;
    Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Absensi Online");

        id_user = (EditText) findViewById(R.id.id_user);
        password_user = (EditText) findViewById(R.id.password_user);

        signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String idUser = id_user.getText().toString();
                String password = password_user.getText().toString();
                if(idUser.equals("")) {
                    id_user.setError("kolom ini harus diisi");
                }
                else if(password.equals("")) {
                    password_user.setError("kolom ini harus diisi");
                }
                else {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("idUser", idUser);
                    intent.putExtra("password", password);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

