package tibet.fivecontacts.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import tibet.fivecontacts.R;

public class ContactList extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    ListView contactList;
    BottomNavigationView bot_nav_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        contactList = (ListView) findViewById(R.id.contactList);
        bot_nav_view = (BottomNavigationView) findViewById(R.id.bot_nav_view);
        bot_nav_view.setOnNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.contactList:

                break;
            case R.id.pickContact:
                intent = new Intent(ContactList.this, PickContact.class);
                startActivity(intent);
                break;
            case R.id.updateUserProfile:
                intent = new Intent(ContactList.this, UpdateUserProfile.class);
                startActivity(intent);
                break;
        }
        return true;
    }


}
