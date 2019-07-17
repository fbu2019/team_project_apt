package com.example.skillshop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignupActivity extends AppCompatActivity {

    public static String userId;
    public static String userName;

    TextView signupMessage;
    EditText etUsername;
    EditText etPassword;
    EditText etZipCode;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupMessage = findViewById(R.id.signUpMessage);
        etUsername = findViewById(R.id.emailAddress);
        etZipCode = findViewById(R.id.zipCode);

        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                nextActivity();
            }
        });
    }

    private void nextActivity() {

            Intent main = new Intent(SignupActivity.this, LoginActivity.class);
            main.putExtra("usernam", etUsername.getText() );
            main.putExtra("zipCode", etZipCode.getText());
            startActivity(main);
        }

    }


