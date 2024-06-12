package com.example.filmhunt;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class ReauthDialog extends Dialog {

    TextView dialogTitle, dialogMessage;
    EditText passwordInput;
    Button confirmButton, cancelButton;

    public ReauthDialog(@NonNull Context context, ConfirmAction onConfirm) {
        super(context);

        // Inflate the custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_reauth, null);
        setContentView(dialogView);

        // Dynamic dialog content
        dialogTitle = dialogView.findViewById(R.id.dialog_title);
        dialogMessage = dialogView.findViewById(R.id.dialog_message);
        passwordInput = dialogView.findViewById(R.id.password_input);

        // Button click listeners
        confirmButton = dialogView.findViewById(R.id.dialog_confirm);
        cancelButton = dialogView.findViewById(R.id.dialog_cancel);
        confirmButton.setOnClickListener(v -> {
            String password = passwordInput.getText().toString();
            onConfirm.execute(password);
            dismiss();
        });
        cancelButton.setOnClickListener(v -> dismiss());

        // Set the dialog to be non-cancelable
        setCancelable(false);
    }

    public interface ConfirmAction {
        void execute(String password);
    }
}
