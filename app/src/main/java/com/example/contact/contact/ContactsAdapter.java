package com.example.contact.contact;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.contact.R;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {
    private List<Contact> contacts;
    private List<Contact> filteredContacts;
    private Context context;

    private String messageNumber;
    private String phoneNumber;

    public ContactsAdapter(Context context, List<Contact> contacts) {
        this.context = context;
        this.contacts = contacts;
        loadSettings();
        this.filteredContacts = new ArrayList<>(contacts);
        sortContacts();
    }

    private void loadSettings() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("gateway", Context.MODE_PRIVATE);
        this.messageNumber = sharedPreferences.getString("messageNumber", "empty");
        this.phoneNumber = sharedPreferences.getString("phoneNumber", "empty");
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = filteredContacts.get(position);
        holder.contactName.setText(contact.getName());
        holder.contactPhoneNumber.setText(contact.getPhone());

        holder.buttonMessage.setOnClickListener(v -> {
            loadSettings();
            showMessageDialog(editPhoneNumber(contact.getPhone()));
        });

        holder.buttonPhone.setOnClickListener(v -> {
            loadSettings();
            String message = editPhoneNumber(contact.getPhone()) + "#";
            sendSMS(phoneNumber, message);
        });
    }

    @Override
    public int getItemCount() {
        return filteredContacts.size();
    }

    public void filterContacts(String query) {
        filteredContacts.clear();
        if (query == null || query.isEmpty()) {
            filteredContacts.addAll(contacts);
        } else {
            for (Contact contact : contacts) {
                if (contact.getName().toLowerCase().contains(query.toLowerCase()) ||
                        contact.getPhone().contains(query)) {
                    filteredContacts.add(contact);
                }
            }
        }
        sortContacts();
        notifyDataSetChanged();
    }

    private void sortContacts() {
        Collections.sort(filteredContacts, (contact1, contact2) ->
                contact1.getName().compareToIgnoreCase(contact2.getName()));
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView contactName;
        TextView contactPhoneNumber;
        Button buttonMessage;
        Button buttonPhone;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contactName);
            contactPhoneNumber = itemView.findViewById(R.id.contactPhoneNumber);
            buttonMessage = itemView.findViewById(R.id.buttonMessage);
            buttonPhone = itemView.findViewById(R.id.buttonCall);
        }
    }

    private void showMessageDialog(String phone) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Введите сообщение");

        final EditText input = new EditText(context);
        builder.setView(input);

        builder.setPositiveButton("Отправить", (dialog, which) -> {
            String message = phone + "* " + input.getText().toString();
            sendSMS(messageNumber, message);
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void sendSMS(String phoneNumber, String message) {
        if (phoneNumber.equals("empty")) {
            Toast.makeText(context, "У вас не добавлен номер шлюза в настройках.", Toast.LENGTH_SHORT).show();
            return;
        }

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        Toast.makeText(context, "SMS отправлено", Toast.LENGTH_SHORT).show();

    }

    private String editPhoneNumber(String phoneNumber) {
        if (phoneNumber.length() == 11) {
            return phoneNumber;
        }
        phoneNumber = phoneNumber.replaceAll("[^0-9]", "");

        if (phoneNumber.startsWith("7")) {
            phoneNumber = phoneNumber.substring(1);
            phoneNumber = "8" + phoneNumber;
        }

        return phoneNumber;
    }
}
