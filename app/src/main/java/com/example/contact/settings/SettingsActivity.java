package com.example.contact.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.example.contact.R;

public class SettingsActivity extends AppCompatActivity {

    private Button saveButton;
    private Button cancelButton;
    private EditText messageNumber;
    private EditText phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.closeButton);
        messageNumber = findViewById(R.id.input1);
        phoneNumber = findViewById(R.id.input2);

        loadSavedValues();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSavePhoneNumber();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeWindow();
            }
        });
    }

    private void setSavePhoneNumber() {
        String value1 = messageNumber.getText().toString();
        String value2 = phoneNumber.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences("gateway", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("messageNumber", value1);
        editor.putString("phoneNumber", value2);
        editor.apply();

        closeWindow();
    }

    private void closeWindow() {
        finish();
    }

    private void loadSavedValues() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String savedValue1 = sharedPreferences.getString("messageNumber", "");
        String savedValue2 = sharedPreferences.getString("phoneNumber", "");

        messageNumber.setText(savedValue1);
        phoneNumber.setText(savedValue2);
    }
}