package com.example.skillshop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
        submit.setEnabled(false);

        // button is not functional when user has not completed all fields
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {
                submit.setEnabled(false);
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                for (EditText et  : new EditText[] {etUsername,
                        etPassword, etZipCode}) {
                    try {
                        et.getText();
                    } catch (NumberFormatException e) {
                        // Disable button, show error label, etc.
                        submit.setEnabled(false);
                        return;
                    }
                }
                submit.setEnabled(true);
            }
        };

        etUsername.addTextChangedListener(watcher);
        etPassword.addTextChangedListener(watcher);
        etZipCode.addTextChangedListener(watcher);

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ParseUser user = new ParseUser();
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                final String zipCode = etZipCode.getText().toString();

                //  additional check that all fields hold user info
                if (username.trim().length()==0 || password.trim().length()==0 || zipCode.trim().length()==0){
                    Log.i("Signup", "Username is "+username+". Password is "+password+". zipcode is "+zipCode);
                    Toast.makeText(SignupActivity.this, "All fields must be filled", Toast.LENGTH_LONG).show();
                }
                else if(zipCode.trim().length()!=5 ){
                    Toast.makeText(SignupActivity.this, "Zipcode must be correct length", Toast.LENGTH_LONG).show();
                }
                else {

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


