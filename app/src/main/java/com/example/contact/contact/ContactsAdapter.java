package com.example.contact.contact;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
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
    private static final int SMS_PERMISSION_CODE = 101;
    private List<Contact> contacts;
    private List<Contact> filteredContacts;
    private Context context;

    private String messageNumber;
    private String phoneNumber;

    public ContactsAdapter(Context context, List<Contact> contacts, String messageNumber, String phoneNumber) {
        this.context = context;
        this.contacts = contacts;
        this.messageNumber = messageNumber;
        this.phoneNumber = phoneNumber;
        this.filteredContacts = new ArrayList<>(contacts);
        sortContacts();
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
            showMessageDialog(contact.getPhone());
        });

        holder.buttonPhone.setOnClickListener(v -> {
            String message =contact.getPhone() + "#";
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
        if (phoneNumber.equals("empty")) {Toast.makeText(context, "У вас не добавлен номер шлюза в настройках.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(context, "SMS отправлено", Toast.LENGTH_SHORT).show();
        }
    }
}
