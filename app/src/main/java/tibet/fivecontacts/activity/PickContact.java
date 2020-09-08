package tibet.fivecontacts.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tibet.fivecontacts.R;
import tibet.fivecontacts.model.Contact;
import tibet.fivecontacts.model.User;

public class PickContact extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    ListView contactList;
    BottomNavigationView bot_nav_view;
    ContactAdapter dataAdapter = null;
    List<Contact> contactsInfoList;
    List<Contact> selectedContacts;
    Button save;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contacts);

        Intent intent = this.getIntent();
        Bundle params = intent.getExtras();
        if (params != null) {
            user = (User) params.getSerializable("user");
        }

        contactList = (ListView) findViewById(R.id.contactList);
        save = (Button) findViewById(R.id.save);
        bot_nav_view = (BottomNavigationView) findViewById(R.id.bot_nav_view);
        bot_nav_view.setOnNavigationItemSelectedListener(this);

        requestContactPermission();
        contactList.setAdapter(dataAdapter);

        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Set<String> saveContact = new HashSet<>();
                for (Contact contact : selectedContacts) {
                    saveContact.add(contact.getPhoneNumber());
                }

                user.setSaveContacts(saveContact);

                SharedPreferences userSaved = getSharedPreferences("user",
                        Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = userSaved.edit();

                editor.putString("name", user.getName());
                editor.putString("email", user.getEmail());
                editor.putString("login", user.getLogin());
                editor.putString("password", user.getPassword());
                editor.putBoolean("keepConnected", user.isKeepConnected());
                editor.putStringSet("phoneNumber", user.getSaveContacts());

                editor.commit();

                Intent intent = new Intent(PickContact.this, CallContact.class);
                intent.putExtra("user", user);
                startActivity(intent);
                finish();
            }
        });



        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!contactsInfoList.get(position).isSelected()) {
                    if (selectedContacts.size() < 5) {
                        selectedContacts.add(contactsInfoList.get(position));
                        contactsInfoList.get(position).setSelected(true);

                        TextView contactName = (TextView) view.findViewById(R.id.contactName);
                        TextView contactPhoneNumber = (TextView) view.findViewById(R.id.contactPhoneNumber);

                        contactName.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimaryLight));
                        contactPhoneNumber.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimaryLight));

                    } else {
                        Toast.makeText(PickContact.this, R.string.max_number_contacts_selected,
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    selectedContacts.remove(contactsInfoList.get(position));
                    contactsInfoList.get(position).setSelected(false);

                    TextView contactName = (TextView) view.findViewById(R.id.contactName);
                    TextView contactPhoneNumber = (TextView) view.findViewById(R.id.contactPhoneNumber);

                    contactName.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorSecondary));
                    contactPhoneNumber.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorSecondary));
                }
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.contactList:
                intent = new Intent(PickContact.this, CallContact.class);
                intent.putExtra("user", user);
                startActivity(intent);
                finish();
                break;

            case R.id.pickContact:
                break;

            case R.id.updateUserProfile:
                intent = new Intent(PickContact.this, UpdateUserProfile.class);
                intent.putExtra("user", user);
                startActivity(intent);
                break;
        }
        return true;
    }

    public void requestContactPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_CONTACTS)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.necessary_access_contacts);
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setMessage(R.string.allow_access_contact);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS},
                                PERMISSIONS_REQUEST_READ_CONTACTS);
                    }
                });

                builder.show();

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_CONTACTS},
                        PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        } else {
            getContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContacts();
            } else {
                Toast.makeText(this, R.string.disable_access_contact, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getContacts() {
        String contactId;
        String displayName;
        contactsInfoList = new ArrayList<>();

        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                    if (hasPhoneNumber > 0) {

                        Contact contactsInfo = new Contact();
                        contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        contactsInfo.setContactId(contactId);
                        contactsInfo.setDisplayName(displayName);

                        Cursor phoneCursor = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{contactId},
                                null);

                        if (phoneCursor.moveToNext()) {
                            String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                            contactsInfo.setPhoneNumber(phoneNumber);
                        }

                        phoneCursor.close();

                        contactsInfoList.add(contactsInfo);
                    }
                }
            }

            cursor.close();
            verifySavedContacts();
            dataAdapter = new ContactAdapter();
            contactList.setAdapter(dataAdapter);
        }
    }

    private void verifySavedContacts() {
        selectedContacts = new ArrayList<>();

        if (!user.getSaveContacts().isEmpty()) {
            for (Contact contact : contactsInfoList) {
                if (user.getSaveContacts().contains(contact.getPhoneNumber())) {
                    selectedContacts.add(contact);
                    contact.setSelected(true);
                }
            }
        }
    }

    private class ContactAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return contactsInfoList.size();
        }

        @Override
        public Contact getItem(int i) {
            return contactsInfoList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint({"ViewHolder", "InflateParams", "SetTextI18n"})
        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.contact_info, null);

            TextView contactName = (TextView) view.findViewById(R.id.contactName);
            TextView contactPhoneNumber = (TextView) view.findViewById(R.id.contactPhoneNumber);

            Contact contactsInfo = (Contact) contactsInfoList.get(position);

            contactName.setText(contactsInfo.getDisplayName());
            contactPhoneNumber.setText(contactsInfo.getPhoneNumber());

            if (contactsInfo.isSelected()) {
                contactName.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimaryLight));
                contactPhoneNumber.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimaryLight));
            }

            return view;
        }
    }
}
