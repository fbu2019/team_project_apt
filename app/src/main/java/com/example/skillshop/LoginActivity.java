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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.Login;
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

    ProfileTracker mProfileTracker;

    LoginButton fbLoginButton;
    TextView welcomeMessage;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseUser currentUser = ParseUser.getCurrentUser();
        FacebookSdk.sdkInitialize(getApplicationContext());
        //Profile profile = Profile.getCurrentProfile();


        // for testing
        login("guacamole","123");

        if (currentUser != null) {
            //  continue to next activity if user previously logged in and user has submitted location
            Intent i = new Intent(LoginActivity.this, FragmentHandler.class);
            startActivity(i);

        }

        //  if user has closed app during signing up without logging out, app will resume at SignupActivity
        if (currentUser == null && Profile.getCurrentProfile() != null) {
            Intent i = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(i);
        }

        else {
            setContentView(R.layout.activity_login);

            fbLoginButton = (LoginButton) findViewById(R.id.login_button);
            welcomeMessage = findViewById(R.id.welcomeMessage);

            callbackManager = CallbackManager.Factory.create();
            checkLoginStatus();

            FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {

                @Override
                public void onSuccess(LoginResult loginResult) {
                    if (Profile.getCurrentProfile() == null) {
                        mProfileTracker = new ProfileTracker() {
                            @Override
                            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                                Log.v("facebook - profile", currentProfile.getFirstName());
                                Log.e("LoginActivity", "made it here");
                                nextActivity(currentProfile);
                                mProfileTracker.stopTracking();
                            }
                        };
                    } else {
                        Profile profile = Profile.getCurrentProfile();
                        nextActivity(profile);
                        Log.v("facebook - profile", profile.getFirstName());
                    }
                }

                @Override
                public void onCancel() {
                    setResult(RESULT_CANCELED);
                }

                @Override
                public void onError(FacebookException error) {

                }
            };
            fbLoginButton.registerCallback(callbackManager, callback);
        }
    }

    private void login(String username, String password) {

        Log.i("LoginActivity", "Reachhed login method");
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.d("LoginActivity", "Login successful");
                    final Intent intent = new Intent(LoginActivity.this, FragmentHandler.class);
                    Log.i("LoginActivity", "Reached login success");
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("LoginActivity", "Login failure");
                    e.printStackTrace();

                    //  continues to sign up activity if does not recognize facebook user
                    Intent main = new Intent(LoginActivity.this, SignupActivity.class);
                    startActivity(main);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, @Nullable Intent intent) {
        callbackManager.onActivityResult(requestCode, responseCode, intent);
        super.onActivityResult(requestCode, responseCode, intent);
    }

    private void loadUserProfile(AccessToken newAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");
                    String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void checkLoginStatus() {
        if (AccessToken.getCurrentAccessToken() != null) {
            loadUserProfile(AccessToken.getCurrentAccessToken());
        }
    }

    private void nextActivity(Profile profile) {
        Log.i("LoginActivity", "Reached nextActivity");
        if (profile != null) {
            userId = profile.getId();
            userName = profile.getFirstName() + " " + profile.getLastName();
            login(userId, userId);
        }
        Log.i("LoginActivity", "Profile is null");
    }
}