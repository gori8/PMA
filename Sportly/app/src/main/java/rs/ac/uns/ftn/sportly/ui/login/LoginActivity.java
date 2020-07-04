package rs.ac.uns.ftn.sportly.ui.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.FaceDetector;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rs.ac.uns.ftn.sportly.MainActivity;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.dto.FacebookRequestDTO;
import rs.ac.uns.ftn.sportly.dto.GoogleRequestDTO;
import rs.ac.uns.ftn.sportly.dto.JwtAuthenticationRequest;
import rs.ac.uns.ftn.sportly.dto.UserDTO;
import rs.ac.uns.ftn.sportly.service.SportlyServerServiceUtils;
import rs.ac.uns.ftn.sportly.sync.SyncDataService;
import rs.ac.uns.ftn.sportly.ui.register.RegisterActivity;
import rs.ac.uns.ftn.sportly.utils.JwtTokenUtils;
import rs.ac.uns.ftn.sportly.utils.SportlyUtils;

public class LoginActivity extends AppCompatActivity {
    public static String signInMethod = "None";
    public static String userEmail = "None";

    //----------GOOGLE----------
    public static final String GOOGLE = "Google";
    private static final int RC_SIGN_IN = 1;
    SignInButton signInButton;
    public static GoogleSignInClient mGoogleSignInClient;

    //----------FACEBOOOK----------
    public static final String FACEBOOK = "Facebook";
    private static final String EMAIL = "email";
    private static final String AUTH_TYPE = "rerequest";
    private CallbackManager mCallbackManager;

    //----------EMAIL----------
    public static final int EMAIL_SIGN_IN = 135;
    public static final String EMAIL_ACCOUNT = "Account";

    //---------FIREBASE---------
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase;


    public static String SYNC_DATA_FINISHED= "sync_finished";


    private BroadcastReceiver syncDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(LoginActivity.SYNC_DATA_FINISHED)){

                String authType = intent.getStringExtra("authType");

                goToMainActivityIfLoginSuccess(authType);

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //----------GOOGLE----------
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("985432247508-ku5dtbds3eul9j9mf3mdrhlr6fhborho.apps.googleusercontent.com")
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton = findViewById(R.id.sign_in_button);
        setGoogleButtonText(signInButton, "Continue with Google");
        setGoogleButtonClickEvent(signInButton, mGoogleSignInClient);

        //----------FACEBOOOK----------
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton mLoginButton = findViewById(R.id.login_button);

        //set permissions for facebook account, we can ask for friends, photos...
        mLoginButton.setPermissions(Arrays.asList(EMAIL));
        mLoginButton.setAuthType(AUTH_TYPE);
        setFacebookButtonClickEvent(mLoginButton);

        //----------EMAIL----------
        Button emailLogInButton = findViewById(R.id.log_in_with_email_button);
        setEmailButtonClickEvent(emailLogInButton);

        //----------REGISTER----------
        Button registerButton = findViewById(R.id.register_button);
        setRegisterButtonClickEvent(registerButton);

        String errorMsg = getIntent().getStringExtra("errorMsg");
        if(errorMsg != null){
            showErrorMessageIfLoginFail(errorMsg);
        }
    }

    @SneakyThrows
    @Override
    protected void onStart() {
        super.onStart();

        if (SportlyUtils.getConnectivityStatus(this) == 0) {
            goToMainActivityIfLoginSuccess("");
        }
        //----------GOOGLE----------
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(this);

        //----------FACEBOOOK----------
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedInFacebook = accessToken != null && !accessToken.isExpired();

        //----------EMAIL----------
        //to do, sacuvati zadnji login pa iscitati

        if (googleAccount != null) {
            loadingView();
            // Signed in successfully with google, show authenticated UI.
            goToMainActivityIfLoginSuccess(GOOGLE);
            //userEmail = googleAccount.getEmail();

            //postGoogleToken(googleAccount.getEmail(), googleAccount.getIdToken());

            //System.out.println("GOOGLE: " + userEmail);
        } else if (isLoggedInFacebook) {
            // Signed in successfully with facebook, show authenticated UI.

            loadingView();

            goToMainActivityIfLoginSuccess(FACEBOOK);
            /*
            Map<String, String> facebookInfo = null;
            try {
                facebookInfo = returnFacebookSignInParameters(accessToken);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            userEmail = facebookInfo.get("email");
            System.out.println("FACEBOOK: " + userEmail);
             */
            //postFacebookToken(accessToken.getUserId(), accessToken.getToken());
        }
    }

    @Override
    public void onBackPressed() {
        if(findViewById(R.id.loadingPanel).getVisibility() == View.VISIBLE && signInMethod.equals(FACEBOOK)){
            LoginActivity.mGoogleSignInClient.signOut();
            LoginManager.getInstance().logOut();
            restartLoginActivityWithoutMsg();
        }else {
            LoginActivity.mGoogleSignInClient.signOut();
            LoginManager.getInstance().logOut();
            restartLoginActivityWithoutMsg();
            finishAffinity();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadingView();

        if (requestCode == RC_SIGN_IN) {
            //----------GOOGLE----------
            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResultGoogle(task);
        } else {
            //----------FACEBOOOK----------
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
            //goToMainActivityIfLoginSuccess(FACEBOOK);
        }
    }

    //----------HELPER-FUNCTIONS----------
    private void goToMainActivityIfLoginSuccess(String method) {
        //set sign in method
        signInMethod = method;

        //change view
        LoginActivity loginActivity = (LoginActivity) this;
        Intent intent = new Intent(loginActivity, MainActivity.class);
        loginActivity.startActivity(intent);
    }

    private void showErrorMessageIfLoginFail(String message) {
        TextView tv = (TextView) findViewById(R.id.sign_in_message);
        tv.setText(message);
    }

    private void loadingView() {
        //on sign in view hide buttons
        TextView title = findViewById(R.id.login_title);
        title.setVisibility(View.GONE);

        TextView about = findViewById(R.id.login_about);
        about.setVisibility(View.GONE);

        EditText email = findViewById(R.id.login_email);
        email.setVisibility(View.GONE);

        EditText password = findViewById(R.id.login_password);
        password.setVisibility(View.GONE);

        Button emailLogin = findViewById(R.id.log_in_with_email_button);
        emailLogin.setVisibility(View.GONE);

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setVisibility(View.GONE);

        LoginButton mLoginButton = findViewById(R.id.login_button);
        mLoginButton.setVisibility(View.GONE);

        TextView message = findViewById(R.id.sign_in_message);
        message.setVisibility(View.GONE);

        TextView registerLabel = findViewById(R.id.register_txt);
        registerLabel.setVisibility(View.GONE);

        Button register = findViewById(R.id.register_button);
        register.setVisibility(View.GONE);

        //on sign in view show loading
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

    }

    //----------GOOGLE-FUNCTIONS----------
    private void handleSignInResultGoogle(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            System.out.println("GOOGLE EMAIL: " + account.getEmail());
            System.out.println("GOOGLE ID: " + account.getId());
            System.out.println("GOOGLE ID TOKEN: " + account.getIdToken());
            System.out.println("GOOGLE SERVER AUTH CODE: " + account.getServerAuthCode());

            postGoogleToken(account.getEmail(), account.getIdToken());

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            System.out.println(e.getMessage());
            System.out.println("Google sign in error");
            showErrorMessageIfLoginFail("Sign in with Google failed. Please try again.");
        }
    }

    private void setGoogleButtonText(SignInButton signInButton, String text) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(text);
                tv.setTextSize(17);
                tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
                return;
            }
        }
    }

    private List<String> returnGoogleSignInParameters(GoogleSignInAccount account) {
        List<String> data = new ArrayList<>();
        //All parameters that we want
        data.add(account.getEmail());

        return data;
    }

    private void setGoogleButtonClickEvent(SignInButton signInButton, GoogleSignInClient mGoogleSignInClient) {
        // Register a callback to respond to the user
        signInButton.setOnClickListener(new View.OnClickListener() {
            private void signIn() {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                }
            }
        });
    }

    private void postGoogleToken(String email, String idToken) {
        GoogleRequestDTO dto = new GoogleRequestDTO();
        dto.setEmail(email);
        dto.setIdToken(idToken);

        Call<UserDTO> call = SportlyServerServiceUtils.sportlyServerService.postGoogleToken(dto);
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.code() == 200) {
                        /*
                        TODO: Save to DB
                         */

                    GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(LoginActivity.this);
                    if(googleAccount == null)
                        return;

                    UserDTO userDTO = response.body();

                    System.out.println("Google sign in success");
                    userEmail = userDTO.getEmail();
                    JwtTokenUtils.saveJwtToken(userDTO.getId(), userDTO.getIme() + " " + userDTO.getPrezime(), userDTO.getEmail(), userDTO.getToken(), LoginActivity.this);

                    Log.i("GOOGLE SIGN IN", "User ID: " + userDTO.getId());

                    FirebaseMessaging.getInstance().subscribeToTopic(userDTO.getId().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    registerUserOnFirebase(userDTO, GOOGLE);
                                }
                            });

                } else {
                    Log.d("REZ", "Meesage recieved: " + response.code());
                    LoginActivity.mGoogleSignInClient.signOut();
                    restarLoginActivity();
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Log.d("REZ", t.getMessage() != null ? t.getMessage() : "error");
                LoginActivity.mGoogleSignInClient.signOut();
                restarLoginActivity();
            }
        });
    }

    //----------FACEBOOOK-FUNCTIONS----------
    private Map<String, String> returnFacebookSignInParameters(AccessToken accessToken) throws ExecutionException, InterruptedException {
        Map<String, String> data = null;

        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @SneakyThrows
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                //All parameters that we want
                System.out.println("onCompleted jsonObject: " + object);
                System.out.println("onCompleted response: " + response);
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "email");
        request.setParameters(parameters);
        GraphRequestAsyncTask task = request.executeAsync();
        String json = task.get().get(0).getRawResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            data = objectMapper.readValue(json, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return data;
    }

    private void setFacebookButtonClickEvent(LoginButton mLoginButton) {
        // Register a callback to respond to the user
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @SneakyThrows
            @Override
            public void onSuccess(LoginResult loginResult) {
                //goToMainActivityIfLoginSuccess(FACEBOOK);
                signInMethod = FACEBOOK;
                setResult(RESULT_OK);
                System.out.println("Facebook sign in success");

                AccessToken accessToken = loginResult.getAccessToken();
                System.out.println("FACEBOOK TOKEN: " + accessToken.getToken());
                System.out.println("FACEBOOK USER ID: " + accessToken.getUserId());
                /*
                Map<String, String> facebookInfo = null;
                try {
                    facebookInfo = returnFacebookSignInParameters(accessToken);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                userEmail = facebookInfo.get("email");
                System.out.println("FACEBOOK EMAIL: " + userEmail);*/

                postFacebookToken(accessToken.getUserId(), accessToken.getToken());
            }

            @Override
            public void onCancel() {
                setResult(RESULT_CANCELED);
                System.out.println("Facebook sign in cancel");
            }

            @Override
            public void onError(FacebookException e) {
                // Handle exception
                System.out.println("Facebook sign in error");
                System.out.println(e.getMessage());
                showErrorMessageIfLoginFail("Sign in with Facebook failed. Please try again.");
            }
        });
    }

    private void postFacebookToken(String userId, String token) {
        FacebookRequestDTO dto = new FacebookRequestDTO();
        dto.setUserId(userId);
        dto.setToken(token);

        Call<UserDTO> call = SportlyServerServiceUtils.sportlyServerService.postFacebookToken(dto);
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.code() == 200) {
                        /*
                        TODO: Save to DB
                         */
                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                    boolean isLoggedInFacebook = accessToken != null && !accessToken.isExpired();
                    if(isLoggedInFacebook == false)
                        return;

                    UserDTO userDTO = response.body();

                    System.out.println("Facebook sign in success");
                    userEmail = userDTO.getEmail();
                    JwtTokenUtils.saveJwtToken(userDTO.getId(), userDTO.getIme() + " " + userDTO.getPrezime(), userDTO.getEmail(), userDTO.getToken(), LoginActivity.this);


                    Log.i("FACEBOOK SIGN IN", "User ID: " + userDTO.getId());

                    FirebaseMessaging.getInstance().subscribeToTopic(userDTO.getId().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    registerUserOnFirebase(userDTO, FACEBOOK);
                                }
                            });


                } else {
                    Log.d("REZ", "Meesage recieved: " + response.code());
                    LoginManager.getInstance().logOut();
                    restarLoginActivity();
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Log.d("REZ", t.getMessage() != null ? t.getMessage() : "error");
                LoginManager.getInstance().logOut();
                restarLoginActivity();
            }
        });
    }

    //----------EMAIL-FUNCTIONS----------
    private void setEmailButtonClickEvent(Button signInButton) {
        // Register a callback to respond to the user
        signInButton.setOnClickListener(new View.OnClickListener() {
            private void signIn() {
                EditText email = (EditText) findViewById(R.id.login_email);
                EditText password = (EditText) findViewById(R.id.login_password);
                String email_txt = email.getText().toString();
                String password_txt = password.getText().toString();

                //TEMP VALIDATION
                if (email_txt.trim().equals("") || password_txt.trim().equals("")) {
                    System.out.println("Email sign in error");
                    showErrorMessageIfLoginFail("Please fill data correctly.");
                    return;
                }

                JwtAuthenticationRequest request = new JwtAuthenticationRequest();
                request.setUsername(email_txt);
                request.setPassword(password_txt);

                loadingView();

                Call<UserDTO> call = SportlyServerServiceUtils.sportlyServerService.standardLogin(request);
                call.enqueue(new Callback<UserDTO>() {
                    @Override
                    public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                        if (response.code() == 200) {
                        /*
                        TODO: Save to DB
                         */
                            UserDTO userDTO = response.body();

                            System.out.println("Standard sign in success");
                            userEmail = userDTO.getEmail();
                            JwtTokenUtils.saveJwtToken(userDTO.getId(), userDTO.getIme() + " " + userDTO.getPrezime(), userDTO.getEmail(), userDTO.getToken(), LoginActivity.this);

                            Log.i("STANDARD SIGN IN", "User ID: " + userDTO.getId());

                            FirebaseMessaging.getInstance().subscribeToTopic(userDTO.getId().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            registerUserOnFirebase(userDTO, EMAIL);
                                        }
                                    });

                        } else {
                            Log.d("REZ", "Meesage recieved: " + response.code());
                            JwtTokenUtils.removeJwtToken(LoginActivity.this);
                            restarLoginActivity();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserDTO> call, Throwable t) {
                        Log.d("REZ", t.getMessage() != null ? t.getMessage() : "error");
                        JwtTokenUtils.removeJwtToken(LoginActivity.this);
                        restarLoginActivity();
                    }
                });

            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.log_in_with_email_button:
                        //hide keyboard
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.RESULT_UNCHANGED_SHOWN);

                        signIn();
                        break;
                }
            }
        });
    }

    //----------REGISTER-FUNCTIONS----------

    private void goToRegisterActivity() {
        LoginActivity loginActivity = (LoginActivity) this;
        Intent intent = new Intent(loginActivity, RegisterActivity.class);
        loginActivity.startActivity(intent);
    }

    private void setRegisterButtonClickEvent(Button registerButton) {
        // Register a callback to respond to the user
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.register_button:
                        goToRegisterActivity();
                        break;
                }
            }
        });
    }

    public void onForgotPasswordClick(View v) {
        //click on forgot pasword
        EditText email = findViewById(R.id.login_email);
        String emailString = email.getText().toString();

        String toastMsg = "";
        if (emailString.equals("")) {
            toastMsg = "Please enter email";
        } else {
            toastMsg = "We sent an email to " + emailString;
        }

        Context context = getApplicationContext();
        CharSequence text = toastMsg;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void registerUserOnFirebase(UserDTO user, String authType) {

        String id = user.getId().toString();

        if (user.isFirstLogin()) {

            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(id);

            String device_token = FirebaseInstanceId.getInstance().getToken();

            HashMap<String, String> userMap = new HashMap<>();
            userMap.put("name", user.getIme() + " " + user.getPrezime());
            userMap.put("status", "Hi there I'm using Lapit Chat App.");
            userMap.put("image", "default");
            userMap.put("thumb_image", "default");
            userMap.put("device_token", device_token);

            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {

                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {


                        Intent serviceIntent = new Intent(LoginActivity.this, SyncDataService.class);
                        serviceIntent.putExtra("flagStarted","LoginActivity");
                        serviceIntent.putExtra("authType",authType);
                        startService(serviceIntent);

                    }

                }
            });

        } else {

            Intent serviceIntent = new Intent(LoginActivity.this, SyncDataService.class);
            serviceIntent.putExtra("flagStarted","LoginActivity");
            serviceIntent.putExtra("authType",authType);
            startService(serviceIntent);

        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SYNC_DATA_FINISHED);

        this.registerReceiver(this.syncDataReceiver, filter);
    }


    public void onPause() {
        super.onPause();

        this.unregisterReceiver(this.syncDataReceiver);
    }

    public void restarLoginActivity(){
        Intent intent = getIntent();
        intent.putExtra("errorMsg", "Sign failed. Please try again.");
        finish();
        startActivity(intent);
    }

    public void restartLoginActivityWithoutMsg(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}