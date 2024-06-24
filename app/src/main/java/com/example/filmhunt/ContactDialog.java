package com.example.filmhunt;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class ContactDialog extends Dialog {

    TextView contactTitle, contactMessage;
    EditText contactInput;
    Button sendButton, cancelButton;

    public ContactDialog(@NonNull Context context, ConfirmAction onConfirm) {
        super(context);

        // Inflate the custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.dialog_contact, null);
        setContentView(contactView);

        // Dynamic dialog content
        contactTitle = contactView.findViewById(R.id.contact_title);
        contactMessage = contactView.findViewById(R.id.contact_message);
        contactInput = contactView.findViewById(R.id.message_input);

        // Button click listeners
        sendButton = contactView.findViewById(R.id.contact_send);
        cancelButton = contactView.findViewById(R.id.contact_cancel);
        sendButton.setOnClickListener(v -> {
            String message = contactInput.getText().toString();
            if (contactInput.getText().toString().isEmpty()) {

                Toast.makeText(context, "Please enter a message", Toast.LENGTH_SHORT).show();

            } else{
                onConfirm.execute(message);
            }

            dismiss();

        });
        cancelButton.setOnClickListener(v -> dismiss());
        setCancelable(true);
    }

    public interface ConfirmAction {
        void execute(String message);
    }
}
