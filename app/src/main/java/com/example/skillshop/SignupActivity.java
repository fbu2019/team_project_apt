package com.example.skillshop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class SignupActivity extends AppCompatActivity {

    public static String userId;
    public static String userName;

    LoginButton fbLoginButton;
    TextView signupMessage;
    EditText etEmail;
    EditText etZipCode;

    AccessTokenTracker accessTokenTracker;
    ProfileTracker profileTracker;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        fbLoginButton = findViewById(R.id.login_button);
        signupMessage = findViewById(R.id.signUpMessage);
        etEmail = findViewById(R.id.emailAddress);
        etZipCode = findViewById(R.id.zipCode);

        //  handles login responses
        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            }
        };


        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                nextActivity(newProfile);
            }
        };


        accessTokenTracker.startTracking();
        profileTracker.startTracking();


        fbLoginButton = (LoginButton) findViewById(R.id.login_button);
        FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Profile profile = Profile.getCurrentProfile();
                //TODO: check for a way to check in database if user already exists nvm just use buttons
                nextActivity(profile);
                //Toast.makeText(getApplicationContext(), "Logging in", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                setResult(RESULT_CANCELED);
                finish();
            }

            @Override
            public void onError(FacebookException error) {

            }
        };
        fbLoginButton.setReadPermissions("user_friends"); //    allows to use/access FB friends - can be changed
        fbLoginButton.registerCallback(callbackManager, callback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        nextActivity(profile);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        callbackManager.onActivityResult(requestCode, responseCode, intent);
    }

    private void nextActivity(Profile profile) {
        if (profile != null) {
            Intent main = new Intent(SignupActivity.this, FragmentHandler.class);
            main.putExtra("name", profile.getFirstName());
            main.putExtra("surname", profile.getLastName());
            main.putExtra("id", profile.getId());
            main.putExtra("imageUrl", profile.getProfilePictureUri(200, 200).toString());

            //  TODO: save user email and zipcode here
            userId = profile.getId();
            userName = profile.getFirstName() + " " + profile.getLastName();
            startActivity(main);
        }

    }
}
