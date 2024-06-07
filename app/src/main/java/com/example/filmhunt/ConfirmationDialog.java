package com.example.filmhunt;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class ConfirmationDialog extends Dialog {

    private TextView dialogTitle, dialogMessage;
    private Button confirmButton, cancelButton;

    public ConfirmationDialog(@NonNull Context context, String field, String currentValue, String newValue, ConfirmAction onConfirm) {
        super(context);

        // Inflate the custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_confirmation, null);
        setContentView(dialogView);

        // Dynamic dialog content
        dialogTitle = dialogView.findViewById(R.id.dialog_title);
        dialogMessage = dialogView.findViewById(R.id.dialog_message);
        dialogTitle.setText("Confirm Update");
        dialogMessage.setText("Are you sure you want to update your " + field + " from " + currentValue + " to " + newValue + "?");

        // Button click listeners
        confirmButton = dialogView.findViewById(R.id.dialog_confirm);
        cancelButton = dialogView.findViewById(R.id.dialog_cancel);
        confirmButton.setOnClickListener(v -> {
            onConfirm.execute();
            dismiss();
        });
        cancelButton.setOnClickListener(v -> dismiss());

        // Set the dialog to be non-cancelable
        setCancelable(false);
    }
}

