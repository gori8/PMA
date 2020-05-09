package rs.ac.uns.ftn.sportly.ui.register;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.ui.login.LoginActivity;

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

    public void onRadioButtonRegisterClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.register_radio_male:
                if (checked)
                    Gender = "Male";
                    break;
            case R.id.register_radio_female:
                if (checked)
                    Gender = "Female";
                    break;
        }
    }

    private void showErrorMessageIfRegisterFail(String message){
        TextView tv = (TextView)findViewById(R.id.register_message);
        tv.setText(message);
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
                boolean retVal = true;
                //validacija
                return retVal;
            }
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.register_create_button:
                        if(Validate()){
                            showCreatePopup();
                        }
                        else{
                            showErrorMessageIfRegisterFail("Validation failed. Please try again.");
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
}
