package tibet.fivecontacts.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import tibet.fivecontacts.R;
import tibet.fivecontacts.model.User;

public class MainActivity extends AppCompatActivity {

    EditText login;
    EditText password;
    Switch keepConnected;
    Button loginButton;
    Button registerButton;
    TextView forgotPassword;

    boolean firstAccessLogin = true;
    boolean firstAccessPassword = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = (EditText) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);
        keepConnected = (Switch) findViewById(R.id.keepConnected);
        loginButton = (Button) findViewById(R.id.loginButton);
        registerButton = (Button) findViewById(R.id.registerButton);
        forgotPassword = (TextView) findViewById(R.id.forgotPassword);

        verifyKeepConnected();

        login.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (firstAccessLogin) {
                    firstAccessLogin = false;
                    login.setText("");
                }

                return false;
            }
        });

        password.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (firstAccessPassword) {
                    firstAccessPassword = false;
                    password.setText("");
                    password.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    );
                }

                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(firstAccessLogin || firstAccessPassword)) {

                    String loginString = login.getText().toString();
                    String passwordString = password.getText().toString();
                    boolean switchValue = keepConnected.isChecked();

                    if (!((loginString.trim().equals("")) || (passwordString.trim().equals("")))) {
                        SharedPreferences userSaved = getSharedPreferences("user",
                                Activity.MODE_PRIVATE);

                        String nameSaved = userSaved.getString("name", "");
                        String emailSaved = userSaved.getString("email", "");
                        String loginSaved = userSaved.getString("login", "");
                        String passwordSaved = userSaved.getString("password", "");
                        Set<String> contactsSaved = userSaved.getStringSet("phoneNumber", new HashSet<String>());

                        if ((loginSaved != null) && (passwordSaved != null)) {

                            if ((loginSaved.compareTo(loginString) == 0) && (passwordSaved.compareTo(passwordString) == 0)) {

                                SharedPreferences.Editor editor = userSaved.edit();

                                editor.putString("name", nameSaved);
                                editor.putString("email", emailSaved);
                                editor.putString("login", loginSaved);
                                editor.putString("password", passwordSaved);
                                editor.putBoolean("keepConnected", switchValue);
                                editor.putStringSet("phoneNumber", contactsSaved);

                                editor.commit();

                                User user = new User(nameSaved, loginSaved, passwordSaved, emailSaved, switchValue, contactsSaved);

                                Intent intent = new Intent(MainActivity.this,
                                        CallContact.class);
                                intent.putExtra("user", user);
                                startActivity(intent);

                            } else {
                                Toast.makeText(MainActivity.this, R.string.login_or_password_incorrect,
                                        Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Toast.makeText(MainActivity.this, R.string.login_or_password_null,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewUser.class);
                startActivity(intent);
            }
        });

    }

    private void verifyKeepConnected() {
        SharedPreferences userSaved = getSharedPreferences("user",
                Activity.MODE_PRIVATE);

        boolean keepConnectedSaved = userSaved.getBoolean("keepConnected", false);

        if (keepConnectedSaved) {

            String nameSaved = userSaved.getString("name", "");
            String emailSaved = userSaved.getString("email", "");
            String loginSaved = userSaved.getString("login", "");
            String passwordSaved = userSaved.getString("password", "");
            Set<String> contactsSaved = userSaved.getStringSet("phoneNumber", new HashSet<String>());

            User user = new User(nameSaved, loginSaved, passwordSaved, emailSaved, keepConnectedSaved, contactsSaved);

            Intent intent = new Intent(MainActivity.this,
                    CallContact.class);
            intent.putExtra("user", user);
            startActivity(intent);
        }
    }
}
