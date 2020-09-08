package tibet.fivecontacts.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;

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
    Set<String> saveContacts;
    User user;
    Intent thisIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_profile);

        thisIntent = this.getIntent();
        Bundle params = thisIntent.getExtras();
        if (params != null) {
            user = (User) params.getSerializable("user");
        }

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
                editor.putStringSet("phoneNumber", saveContacts);

                editor.commit();

                user.setName(nameString);
                user.setLogin(loginString);
                user.setPassword(passwordString);
                user.setEmail(emailString);
                user.setKeepConnected(keepConnectedValue);
                user.setSaveContacts(saveContacts);

                Intent intent = new Intent(UpdateUserProfile.this, CallContact.class);
                intent.putExtra("user", user);
                startActivity(intent);

                finish();

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
        nameRegister.setText(user.getName());
        emailRegister.setText(user.getEmail());
        loginRegister.setText(user.getLogin());
        passwordRegister.setText(user.getPassword());
        keepConnected.setChecked(user.isKeepConnected());
        saveContacts = user.getSaveContacts();
    }

}