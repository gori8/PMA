package rs.ac.uns.ftn.sportly.ui.register;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.dto.UserDTO;
import rs.ac.uns.ftn.sportly.service.SportlyServerServiceUtils;
import rs.ac.uns.ftn.sportly.ui.login.LoginActivity;
import rs.ac.uns.ftn.sportly.utils.JwtTokenUtils;

public class RegisterActivity extends AppCompatActivity {
    private String Gender = "Male";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button cancelButton = findViewById(R.id.register_cancel_button);
        setCancelButtonClickEvent(cancelButton);

        Button createButton = findViewById(R.id.register_create_button);
        setCreateButtonClickEvent(createButton);
    }

    private void goToLoginActivity(){
        RegisterActivity registerActivity = (RegisterActivity) this;
        Intent intent = new Intent(registerActivity, LoginActivity.class);
        registerActivity.startActivity(intent);
    }

    private void setCancelButtonClickEvent(Button registerButton){
        // Register a callback to respond to the user
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.register_cancel_button:
                        goToLoginActivity();
                        break;
                }
            }
        });
    }

    private void setCreateButtonClickEvent(Button registerButton){
        // Register a callback to respond to the user
        registerButton.setOnClickListener(new View.OnClickListener() {
            private boolean Validate(){
                EditText email = (EditText) findViewById(R.id.register_email);
                EditText password = (EditText) findViewById(R.id.register_password);
                EditText rep_password = (EditText) findViewById(R.id.register_repeat_password);
                EditText name = (EditText) findViewById(R.id.register_name);
                EditText surname = (EditText) findViewById(R.id.register_surname);

                String email_txt = email.getText().toString();
                String password_txt = password.getText().toString();
                String rep_password_txt = rep_password.getText().toString();
                String name_txt = name.getText().toString();
                String surname_txt = surname.getText().toString();


                if (!password_txt.equals(rep_password_txt)) {
                    return false;
                }

                if (email_txt.trim().equals("") || password_txt.trim().equals("") || rep_password_txt.trim().equals("") || name_txt.trim().equals("") || surname_txt.trim().equals("")){
                    return false;
                }

                return true;
            }
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.register_create_button:
                        if(Validate()){
                            EditText email = (EditText) findViewById(R.id.register_email);
                            EditText password = (EditText) findViewById(R.id.register_password);
                            EditText rep_password = (EditText) findViewById(R.id.register_repeat_password);
                            EditText name = (EditText) findViewById(R.id.register_name);
                            EditText surname = (EditText) findViewById(R.id.register_surname);

                            String email_txt = email.getText().toString();
                            String password_txt = password.getText().toString();
                            String rep_password_txt = rep_password.getText().toString();
                            String name_txt = name.getText().toString();
                            String surname_txt = surname.getText().toString();

                            UserDTO userDTO = new UserDTO();
                            userDTO.setIme(name_txt);
                            userDTO.setPrezime(surname_txt);
                            userDTO.setEmail(email_txt);
                            userDTO.setPassword(password_txt);

                            Call<UserDTO> call = SportlyServerServiceUtils.sportlyServerService.standardRegister(userDTO);
                            call.enqueue(new Callback<UserDTO>() {
                                @Override
                                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                                    if (response.code() == 200) {

                                        UserDTO userDTO = response.body();
                                        System.out.println("Standard register in success");
                                        showCreatePopup();

                                    } else {
                                        Log.d("REZ", "Meesage recieved: " + response.code());
                                        goToLoginActivity();
                                    }
                                }

                                @Override
                                public void onFailure(Call<UserDTO> call, Throwable t) {
                                    Log.d("REZ", t.getMessage() != null ? t.getMessage() : "error");
                                    goToLoginActivity();
                                }
                            });
                        }
                        else{
                            showErrorMessage("Validation failed. Please try again.");
                        }
                        break;
                }
            }
        });
    }

    private void showCreatePopup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(RegisterActivity.this);
        alert.setMessage("You account has been created.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener(){

                    public void onClick(DialogInterface dialog, int which) {
                        goToLoginActivity();
                    }
                });

        AlertDialog alert1 = alert.create();
        alert1.show();
    }

    private void showErrorMessage(String message) {
        TextView tv = (TextView) findViewById(R.id.register_message);
        tv.setText(message);
    }
}
