package com.example.skillshop;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    public static String userId;
    public static String userName;
    LoginButton fbLoginButton;
    Button signUpButton;
    Button loginButton;
    //  Button testButton;
    TextView welcomeMessage;
    EditText etUsernameInput;
    EditText etPasswordInput;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) {
            //  continue to next activity if user previously logged in
            Intent i = new Intent(LoginActivity.this, FragmentHandler.class);
            startActivity(i);

        } else {
            setContentView(R.layout.activity_login);

            welcomeMessage = findViewById(R.id.welcomeMessage);
            //  testButton = findViewById(R.id.continueNext);
            loginButton = findViewById(R.id.loginButton);
            etUsernameInput = findViewById(R.id.etUsername);
            etPasswordInput = findViewById(R.id.etPassword);

            callbackManager = CallbackManager.Factory.create();
            fbLoginButton = (LoginButton) findViewById(R.id.login_button);
            fbLoginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
            checkLoginStatus();

            fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    nextActivity(Profile.getCurrentProfile());
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {

                }
            });

            // loginButton is not functional until user has completed all fields
            loginButton.setEnabled(false);
            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int
                        count, int after) {
                    loginButton.setEnabled(false);
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    for (EditText et  : new EditText[] {etUsernameInput,
                            etPasswordInput}) {
                        try {
                            et.getText();
                        } catch (NumberFormatException e) {
                            // Disable button, show error label, etc.
                            loginButton.setEnabled(false);
                            return;
                        }
                    }
                    loginButton.setEnabled(true);
                }
            };

            etUsernameInput.addTextChangedListener(watcher);
            etPasswordInput.addTextChangedListener(watcher);

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String username = etUsernameInput.getText().toString();
                    final String password = etPasswordInput.getText().toString();
                    Log.i("Login Activity", username);
                    Log.i("Login Activity", password);

                    if (username.trim().length()==0 || password.trim().length()==0){
                        Log.i("Signup", "Username is "+username+". Password is "+password+".");
                        Toast.makeText(LoginActivity.this, "All fields must be filled", Toast.LENGTH_LONG).show();
                    } else {
                        login(username, password);
                    }
                }
            });

            signUpButton = findViewById(R.id.signUpButton);
            signUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent main = new Intent(LoginActivity.this, SignupActivity.class);
                    startActivity(main);
                }
            });

            /*
            testButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    continueToMain();
                }
            });
            */
        }
    }

    private void login(String username, String password) {

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.d("LoginActivity", "Login successful");
                    final Intent intent = new Intent(LoginActivity.this, FragmentHandler.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("LoginActivity", "Login failure");
                    Toast.makeText(LoginActivity.this, "Incorrect username or password", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int responseCode, @Nullable Intent intent) {
        callbackManager.onActivityResult(requestCode, responseCode, intent);
        super.onActivityResult(requestCode, responseCode, intent);
    }


    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            if(currentAccessToken==null)
            {
                //txtName.setText("");
                //txtEmail.setText("");
                //circleImageView.setImageResource(0);
                Toast.makeText(LoginActivity.this,"User Logged out",Toast.LENGTH_LONG).show();
            }
            else
                loadUserProfile(currentAccessToken);
        }
    };

    private void loadUserProfile(AccessToken newAccessToken)
    {
        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response)
            {
                try {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");
                    String image_url = "https://graph.facebook.com/"+id+ "/picture?type=normal";

                    /*
                    txtEmail.setText(email);
                    txtName.setText(first_name +" "+last_name);
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.dontAnimate();

                    Glide.with(MainActivity.this).load(image_url).into(circleImageView);
                    */
                    //  implement later

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields","first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void checkLoginStatus()
    {
        if(AccessToken.getCurrentAccessToken()!=null)
        {
            loadUserProfile(AccessToken.getCurrentAccessToken());
        }
    }

    //  Passes intent to move app to MainActivity
    private void nextActivity(Profile profile) {

        if (profile != null){
            //TODO - Determine if this is the best way to bundle
            Intent main = new Intent (LoginActivity.this, FragmentHandler.class);

            main.putExtra("name", profile.getFirstName()); //   retrieving and putting profile attributes
            main.putExtra("surname", profile.getLastName());
            main.putExtra("id", profile.getId());
            main.putExtra("imageUrl", profile.getProfilePictureUri(200, 200).toString());

            userId = profile.getId();
            userName = profile.getFirstName() + " " + profile.getLastName();
            startActivity(main);
        }
    }

    private void continueToMain(){
        Intent main = new Intent (LoginActivity.this, FragmentHandler.class);
        userId = "1234";
        userName = "Test User";
        startActivity(main);
    }
}