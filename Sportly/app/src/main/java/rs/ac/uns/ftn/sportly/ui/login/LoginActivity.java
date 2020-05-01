package rs.ac.uns.ftn.sportly.ui.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import rs.ac.uns.ftn.sportly.MainActivity;
import rs.ac.uns.ftn.sportly.R;

public class LoginActivity extends AppCompatActivity {
    SignInButton signInButton;
    private static final int RC_SIGN_IN = 1;
    public static GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton = findViewById(R.id.sign_in_button);
        setGoogleButtonText(signInButton, "Sign up with Google");
        setGoogleButtonClickEvent(signInButton, mGoogleSignInClient);

    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null){
            // Signed in successfully, show authenticated UI.
            goToMainActivityIfLoginSuccess(account);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            goToMainActivityIfLoginSuccess(account);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            System.out.println("signInResult:failed code=" + e.getStatusCode());
            showErrorMessageIfLoginFail("Sign in with Google failed. Please try again.");
        }
    }

    private void goToMainActivityIfLoginSuccess(GoogleSignInAccount account){
        System.out.println(account.getEmail());

        LoginActivity loginActivity = (LoginActivity) this;
        Intent intent = new Intent(loginActivity, MainActivity.class);
        loginActivity.startActivity(intent);
    }

    private void showErrorMessageIfLoginFail(String message){
        TextView tv = (TextView)findViewById(R.id.sign_in_message);
        tv.setText(message);
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

    private void setGoogleButtonClickEvent(SignInButton signInButton, GoogleSignInClient mGoogleSignInClient){
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
}
