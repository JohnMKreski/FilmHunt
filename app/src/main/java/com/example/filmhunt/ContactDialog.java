package com.example.filmhunt;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class ContactDialog extends Dialog {

    TextView contactTitle, contactMessage;
    EditText contactInput;
    Button sendButton, cancelButton;

    public ContactDialog(@NonNull Context context, ConfirmAction onConfirm) {
        super(context);

        // Inflate the custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.contact, null);
        setContentView(contactView);

        // Dynamic dialog content
        contactTitle = contactView.findViewById(R.id.contact_title);
        contactMessage = contactView.findViewById(R.id.contact_message);
        contactInput = contactView.findViewById(R.id.message_input);

        // Button click listeners
        sendButton = contactView.findViewById(R.id.contact_send);
        cancelButton = contactView.findViewById(R.id.contact_cancel);
        sendButton.setOnClickListener(v -> {

            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", "live.smtp.mailtrap.io");
            properties.put("mail.smtp.port", "587");

            Authenticator authenticator = new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("api", "4286e60303b64d4c39a662e2c21fa686");
                }
            };
            Session session = Session.getInstance(properties, authenticator);

            try {
                //create a MimeMessage object
                Message message = new MimeMessage(session);
                //set From email field
                message.setFrom(new InternetAddress("demomailtrap.com"));
                //set To email field
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse("camnc2003@gmail.com"));
                //set email subject field
                message.setSubject("New FilmHunt USer Message");
                //set the content of the email message
                message.setText("");
                //send the email message
                Transport.send(message);

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }


            dismiss();
        });
        cancelButton.setOnClickListener(v -> dismiss());

        // Set the dialog to be non-cancelable
        setCancelable(true);
    }

    public interface ConfirmAction {
        void execute(String message);
    }
}
