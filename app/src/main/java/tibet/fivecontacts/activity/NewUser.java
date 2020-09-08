package tibet.fivecontacts.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;

import tibet.fivecontacts.R;
import tibet.fivecontacts.model.User;

public class NewUser extends AppCompatActivity {

    EditText nameRegister;
    EditText emailRegister;
    EditText loginRegister;
    EditText passwordRegister;
    Button registerButton;

    boolean firstAccessName = true;
    boolean firstAccessEmail = true;
    boolean firstAccessLogin = true;
    boolean firstAccessPassword = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        nameRegister = (EditText) findViewById(R.id.nameRegister);
        emailRegister = (EditText) findViewById(R.id.emailRegister);
        loginRegister = (EditText) findViewById(R.id.loginRegister);
        passwordRegister = (EditText) findViewById(R.id.passwordRegister);
        registerButton = (Button) findViewById(R.id.createRegister);

        nameRegister.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (firstAccessName) {
                    firstAccessName = false;
                    nameRegister.setText("");
                }

                return false;
            }
        });

        emailRegister.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (firstAccessEmail) {
                    firstAccessEmail = false;
                    emailRegister.setText("");
                }

                return false;
            }
        });

        loginRegister.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (firstAccessLogin) {
                    firstAccessLogin = false;
                    loginRegister.setText("");
                }

                return false;
            }
        });

        passwordRegister.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (firstAccessPassword) {
                    firstAccessPassword = false;
                    passwordRegister.setText("");
                    passwordRegister.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    );
                }

                return false;
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((nameRegister != null) && (emailRegister != null) && (loginRegister != null) && (passwordRegister != null)) {
                    String nameString = nameRegister.getText().toString();
                    String emailString = emailRegister.getText().toString();
                    String loginString = loginRegister.getText().toString();
                    String passwordString = passwordRegister.getText().toString();

                    // TODO: salvar mais de um usuario no SharedPreferences
                    if (!(firstAccessName || firstAccessEmail || firstAccessLogin || firstAccessPassword)) {
                        if (!((nameString.trim().equals(""))
                                || (emailString.trim().equals(""))
                                || (loginString.trim().equals(""))
                                || (passwordString.trim().equals("")))) {

                            SharedPreferences saveUser = getSharedPreferences("user", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = saveUser.edit();

                            editor.putString("name", nameString);
                            editor.putString("email", emailString);
                            editor.putString("login", loginString);
                            editor.putString("password", passwordString);
                            editor.putBoolean("keepConnected", false);
                            editor.putStringSet("phoneNumber", new HashSet<String>());

                            editor.commit();

                            Toast.makeText(NewUser.this, R.string.user_successfully_registered,
                                    Toast.LENGTH_LONG).show();
//
                            finish();

                        } else {
                            Toast.makeText(NewUser.this, R.string.empty_field,
                                    Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(NewUser.this, R.string.empty_field,
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(NewUser.this, R.string.null_field,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}