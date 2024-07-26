package com.example.contact.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.contact.R;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;


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

        messageNumber.addTextChangedListener(new ValidationTextWatcher(messageNumber));
        phoneNumber.addTextChangedListener(new ValidationTextWatcher(phoneNumber));

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

        saveButton.setEnabled(false);
        saveButton.setBackgroundColor(Color.GRAY);
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
        SharedPreferences sharedPreferences = getSharedPreferences("gateway", MODE_PRIVATE);
        String savedValue1 = sharedPreferences.getString("messageNumber", "");
        String savedValue2 = sharedPreferences.getString("phoneNumber", "");

        if (savedValue1.isEmpty()) {
            messageNumber.setHint("Введите номер сообщения");
        } else {
            messageNumber.setHint("Сохраненый номер = " + savedValue1);
        }

        if (savedValue2.isEmpty()) {
            phoneNumber.setHint("Введите номер телефона");
        } else {
            phoneNumber.setHint("Сохраненый номер = " + savedValue2);
        }
    }

    private class ValidationTextWatcher implements TextWatcher {
        private final EditText editText;

        ValidationTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            validateInput();
        }

        @Override
        public void afterTextChanged(Editable s) {}

        private void validateInput() {
            String inputMessageNumber = messageNumber.getText().toString();
            String inputPhoneNumber = phoneNumber.getText().toString();

            boolean isMessageNumberValid = inputMessageNumber.length() == 11;
            boolean isPhoneNumberValid = inputPhoneNumber.length() == 11;

            if (isMessageNumberValid) {
                messageNumber.setTextColor(Color.GREEN);
            } else {
                messageNumber.setTextColor(Color.RED);
            }

            if (isPhoneNumberValid) {
                phoneNumber.setTextColor(Color.GREEN);
            } else {
                phoneNumber.setTextColor(Color.RED);
            }

            if (isMessageNumberValid && isPhoneNumberValid) {
                saveButton.setEnabled(true);
                saveButton.setBackgroundColor(Color.GREEN);
            } else {
                saveButton.setEnabled(false);
                saveButton.setBackgroundColor(Color.GRAY);
            }
        }
    }
}