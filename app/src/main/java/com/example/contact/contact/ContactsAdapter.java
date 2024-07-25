package com.example.contact.contact;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.contact.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

    private List<Contact> contacts;
    private List<Contact> filteredContacts;

    public ContactsAdapter(List<Contact> contacts) {
        this.contacts = contacts;
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
    }

    @Override
    public int getItemCount() {
        return filteredContacts.size();
    }

    public void filterContacts(String query) {
        filteredContacts.clear();
        if (query == null || query.isEmpty()) {
            filteredContacts.addAll(contacts); // Добавляем все контакты, если запрос пустой
        } else {
            for (Contact contact : contacts) {
                if (contact.getName().toLowerCase().contains(query.toLowerCase()) ||
                        contact.getPhone().contains(query)) {
                    filteredContacts.add(contact);
                }
            }
        }
        sortContacts(); // Сортируем после фильтрации
        notifyDataSetChanged();
    }

    private void sortContacts() {
        Collections.sort(filteredContacts, (contact1, contact2) ->
                contact1.getName().compareToIgnoreCase(contact2.getName())); // Сортировка по имени
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView contactName;
        TextView contactPhoneNumber;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contactName);
            contactPhoneNumber = itemView.findViewById(R.id.contactPhoneNumber);
        }
    }
}