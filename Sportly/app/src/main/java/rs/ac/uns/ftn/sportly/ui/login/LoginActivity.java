package rs.ac.uns.ftn.sportly.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.SneakyThrows;
import rs.ac.uns.ftn.sportly.MainActivity;
import rs.ac.uns.ftn.sportly.R;

public class LoginActivity extends AppCompatActivity {
    public static String signInMethod = "None";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //----------GOOGLE----------
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton = findViewById(R.id.sign_in_button);
        setGoogleButtonText(signInButton, "Sign up with Google");
        setGoogleButtonClickEvent(signInButton, mGoogleSignInClient);

        //----------FACEBOOOK----------
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton mLoginButton = findViewById(R.id.login_button);

        //set permissions for facebook account, we can ask for friends, photos...
        mLoginButton.setPermissions(Arrays.asList(EMAIL));
        mLoginButton.setAuthType(AUTH_TYPE);
        setFacebookButtonClickEvent(mLoginButton);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //----------GOOGLE----------
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(this);

        //----------FACEBOOOK----------
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedInFacebook = accessToken != null && !accessToken.isExpired();

        if(googleAccount != null){
            // Signed in successfully with google, show authenticated UI.
            goToMainActivityIfLoginSuccess(GOOGLE);
        }else if(isLoggedInFacebook){
            // Signed in successfully with facebook, show authenticated UI.
            goToMainActivityIfLoginSuccess(FACEBOOK);
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
        }else{
            //----------FACEBOOOK----------
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    //----------HELPER-FUNCTIONS----------
    private void goToMainActivityIfLoginSuccess(String method){
        //set sign in method
        signInMethod = method;

        //change view
        LoginActivity loginActivity = (LoginActivity) this;
        Intent intent = new Intent(loginActivity, MainActivity.class);
        loginActivity.startActivity(intent);
    }

    private void showErrorMessageIfLoginFail(String message){
        TextView tv = (TextView)findViewById(R.id.sign_in_message);
        tv.setText(message);
    }

    private void loadingView(){
        //on sign in view hide buttons
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setVisibility(View.GONE);

        LoginButton mLoginButton = findViewById(R.id.login_button);
        mLoginButton.setVisibility(View.GONE);

        //on sign in view show loading
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

    }

    //----------GOOGLE-FUNCTIONS----------
    private void handleSignInResultGoogle(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            System.out.println("Google sign in success");
            goToMainActivityIfLoginSuccess(GOOGLE);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            System.out.println(e.getMessage());
            System.out.println("Google sign in error");
            showErrorMessageIfLoginFail("Sign in with Google failed. Please try again.");
        }
    }

    private void setGoogleButtonText(SignInButton signInButton, String text){
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(text);
                tv.setTextSize(17);

                return;
            }
        }
    }

    private List<String> returnGoogleSignInParameters(GoogleSignInAccount account){
        List<String> data = new ArrayList<>();
        //All parameters that we want
        data.add(account.getEmail());

        return data;
    }

    private void setGoogleButtonClickEvent(SignInButton signInButton, GoogleSignInClient mGoogleSignInClient){
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

    //----------FACEBOOOK-FUNCTIONS----------
    private List<String> returnFacebookSignInParameters(AccessToken accessToken){
        List<String> data = new ArrayList<>();
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @SneakyThrows
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                //All parameters that we want
                String email = object.getString(EMAIL);
                data.add(email);
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "email");
        request.setParameters(parameters);
        request.executeAsync();
        return data;
    }

    private void setFacebookButtonClickEvent(LoginButton mLoginButton){
        // Register a callback to respond to the user
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                goToMainActivityIfLoginSuccess(FACEBOOK);
                setResult(RESULT_OK);
                System.out.println("Facebook sign in success");
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
}