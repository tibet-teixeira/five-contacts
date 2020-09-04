package tibet.fivecontacts.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import tibet.fivecontacts.R;
import tibet.fivecontacts.model.User;

public class UpdateUserProfile extends AppCompatActivity {

    EditText nameRegister;
    EditText emailRegister;
    EditText loginRegister;
    EditText passwordRegister;
    Switch keepConnected;
    Button updateUserProfile;
    Button cancelUpdateUserProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_profile);

        nameRegister = (EditText) findViewById(R.id.nameRegister);
        emailRegister = (EditText) findViewById(R.id.emailRegister);
        loginRegister = (EditText) findViewById(R.id.loginRegister);
        passwordRegister = (EditText) findViewById(R.id.passwordRegister);
        keepConnected = (Switch) findViewById(R.id.keepConnected);
        updateUserProfile = (Button) findViewById(R.id.updateUserProfile);
        cancelUpdateUserProfile = (Button) findViewById(R.id.cancelUpdateUserProfile);

        loadingUserData();

        updateUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameString = nameRegister.getText().toString();
                String emailString = emailRegister.getText().toString();
                String loginString = loginRegister.getText().toString();
                String passwordString = passwordRegister.getText().toString();
                boolean keepConnectedValue = keepConnected.isChecked();

                SharedPreferences userSaved = getSharedPreferences("user",
                        Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = userSaved.edit();

                editor.putString("name", nameString);
                editor.putString("email", emailString);
                editor.putString("login", loginString);
                editor.putString("password", passwordString);
                editor.putBoolean("keepConnected", keepConnectedValue);

                editor.commit();

                User user = new User(nameString, loginString, passwordString, emailString);

                Intent intent = new Intent(UpdateUserProfile.this, ContactList.class);
                intent.putExtra("user", user);
                startActivity(intent);

            }
        });

        cancelUpdateUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadingUserData() {
        SharedPreferences userSaved = getSharedPreferences("user",
                Activity.MODE_PRIVATE);

        String nameSaved = userSaved.getString("name", "");
        String emailSaved = userSaved.getString("email", "");
        String loginSaved = userSaved.getString("login", "");
        String passwordSaved = userSaved.getString("password", "");
        boolean keepConnectedSaved = userSaved.getBoolean("keepConnected", false);

        nameRegister.setText(nameSaved);
        emailRegister.setText(emailSaved);
        loginRegister.setText(loginSaved);
        passwordRegister.setText(passwordSaved);
        keepConnected.setChecked(keepConnectedSaved);
    }

}