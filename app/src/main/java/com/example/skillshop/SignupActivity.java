package com.example.skillshop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.ProfileTracker;

public class SignupActivity extends AppCompatActivity {

    public static String userId;
    public static String userName;

    TextView signupMessage;
    EditText etEmail;
    EditText etZipCode;
    Button submit;

    AccessTokenTracker accessTokenTracker;
    ProfileTracker profileTracker;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //fbLoginButton = findViewById(R.id.login_button);
        signupMessage = findViewById(R.id.signUpMessage);
        etEmail = findViewById(R.id.emailAddress);
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
            main.putExtra("email", etEmail.getText() );
            main.putExtra("zipCode", etZipCode.getText());
            startActivity(main);
        }

    }


