package com.example.contact;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contact.contact.Contact;
import com.example.contact.contact.ContactsAdapter;
import com.example.contact.settings.SettingsActivity;

import android.Manifest;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CONTACTS = 1;
    private RecyclerView recyclerView;
    private ContactsAdapter adapter;
    private List<Contact> contacts;
    private ImageButton settingsButton;

    private String messageNumber;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkContactsPermission();

        settingsButton = findViewById(R.id.settingsButton);
        settingsButton = findViewById(R.id.settingsButton);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingsActivity();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        contacts = loadContacts();
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filterContacts(newText);
                return true;
            }
        });

        loadSettings();

        adapter = new ContactsAdapter(contacts);
        recyclerView.setAdapter(adapter);
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void loadSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("gateway", MODE_PRIVATE);
        messageNumber = sharedPreferences.getString("messageNumber", "empty");
        phoneNumber = sharedPreferences.getString("phoneNumber", "empty");
    }


    private List<Contact> loadContacts() {
        List<Contact> contacts = new ArrayList<>();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        try (Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null) {
                Log.d("Contacts Count", "Total contacts found: " + cursor.getCount());
                while (cursor.moveToNext()) {
                    int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                    if (nameIndex != -1 && phoneIndex != -1) {
                        String name = cursor.getString(nameIndex);
                        String phoneNumber = cursor.getString(phoneIndex);
                        contacts.add(new Contact(name, phoneNumber));
                        Log.d("Contact Loaded", "Name: " + name + ", Phone: " + phoneNumber);
                    } else {
                        Log.e("Cursor Error", "Column index not found");
                    }
                }
            } else {
                Log.e("Cursor Error", "Cursor is null");
            }
        }

        return contacts;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CONTACTS && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadContacts();
        }
    }

    private void checkContactsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Разрешение не предоставлено, запрашиваем его
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_CODE_CONTACTS);
        } else {
            // Разрешение уже предоставлено, загружаем контакты
            loadContacts();
        }
    }
}