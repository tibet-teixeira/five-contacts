package tibet.fivecontacts.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import java.util.List;

import tibet.fivecontacts.R;
import tibet.fivecontacts.model.Contact;
import tibet.fivecontacts.model.User;

public class CallContact extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    public static final int PERMISSIONS_REQUEST_CALL_CONTACT = 2;
    ListView contactCallList;
    BottomNavigationView bot_nav_view;
    ContactAdapter dataAdapter = null;
    List<Contact> contactsInfoList;
    List<Contact> selectedContacts;
    User user;
    boolean permissionCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_contact);

        Intent intent = this.getIntent();
        Bundle params = intent.getExtras();
        if (params != null) {
            user = (User) params.getSerializable("user");
        }

        contactCallList = (ListView) findViewById(R.id.contactCallList);

        bot_nav_view = (BottomNavigationView) findViewById(R.id.bot_nav_view);
        bot_nav_view.setOnNavigationItemSelectedListener(this);

        requestContactPermission();
        requestCallPermission();
        contactCallList.setAdapter(dataAdapter);

        contactCallList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(R.string.select).
                        setItems(R.array.call_options, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        Intent call = new Intent(Intent.ACTION_CALL);
                                        call.setData(Uri.parse("tel: " + selectedContacts.get(position).getPhoneNumber()));

                                        if (ActivityCompat.checkSelfPermission(CallContact.this,
                                                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                            startActivity(call);
                                        } else {
                                            requestCallPermission();
                                        }
                                        break;

                                    case 1:
                                        Intent dial = new Intent(Intent.ACTION_DIAL);
                                        dial.setData(Uri.parse("tel: " + selectedContacts.get(position).getPhoneNumber()));
                                        startActivity(dial);
                                        break;
                                }
                            }
                        });
                builder.show();

            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.contactList:
                break;

            case R.id.pickContact:
                intent = new Intent(CallContact.this, PickContact.class);
                intent.putExtra("user", user);
                startActivity(intent);
                break;

            case R.id.updateUserProfile:
                intent = new Intent(CallContact.this, UpdateUserProfile.class);
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
            verifySavedContacts();
        }
    }

    public void requestCallPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.CALL_PHONE)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.necessary_access_phone);
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setMessage(R.string.allow_access_phone);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{android.Manifest.permission.CALL_PHONE},
                                PERMISSIONS_REQUEST_CALL_CONTACT);
                    }
                });

                builder.show();

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.CALL_PHONE},
                        PERMISSIONS_REQUEST_CALL_CONTACT);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContacts();
                verifySavedContacts();
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
            contactCallList.setAdapter(dataAdapter);
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
            return selectedContacts.size();
        }

        @Override
        public Contact getItem(int i) {
            return selectedContacts.get(i);
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

            Contact contactsInfo = (Contact) selectedContacts.get(position);

            contactName.setText(contactsInfo.getDisplayName());
            contactPhoneNumber.setText(contactsInfo.getPhoneNumber());

            return view;
        }
    }
}
