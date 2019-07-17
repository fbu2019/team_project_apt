package com.example.skillshop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

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
        etPassword = findViewById(R.id.etPassword);
        etZipCode = findViewById(R.id.zipCode);

        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ParseUser user = new ParseUser();

                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                final String zipCode = etZipCode.getText().toString();

                user.setUsername(username);
                user.setPassword(password);
                user.put("zipCode", zipCode);

                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            login(username, password);
                        } else {
                            Log.d("SignUpActivity", "Sign up failed");
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void login(String username, String password) {

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.d("SignUpActivity", "Login successful");
                    final Intent intent = new Intent(SignupActivity.this, FragmentHandler.class);
                    startActivity(intent);
                    finish();
                }   else {
                    Log.e("SignUpActivity", "Login failure");
                    e.printStackTrace();
                    finish();
                }
            }
        });
    }

}


