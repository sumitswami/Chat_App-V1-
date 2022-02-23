package com.example.chat;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class MessageAdapter extends ArrayAdapter<friendlymessage> {
    //Encryption key
    private byte encryptionKey[] = {9, 115, 51, 86, 105, 4, -31, -23, -68, 88, 17, 20, 3, -105, 119, -53};

    //Cryptography (AES)
    private Cipher cipher, decipher;
    private SecretKeySpec secretSpecKey;

    public MessageAdapter (Context context,int resource , List<friendlymessage> objects) {
        super(context, resource,objects);
    }


    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {

        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_message, parent, false);
        }


        ImageView photoImageView = (ImageView) convertView.findViewById(R.id.photoImageView);
        TextView messageTextView = (TextView) convertView.findViewById(R.id.messageTextView);
        TextView authorTextView = (TextView) convertView.findViewById(R.id.nameTextView);

        friendlymessage message = getItem(position);

        boolean isPhoto = message.getPhotoUrl() != null;
        if (isPhoto) {
            messageTextView.setVisibility(View.GONE);
            photoImageView.setVisibility(View.VISIBLE);
            Glide.with(photoImageView.getContext())
                    .load(message.getPhotoUrl())
                    .into(photoImageView);
        } else {
            messageTextView.setVisibility(View.VISIBLE);
            photoImageView.setVisibility(View.GONE);
            try {
                messageTextView.setText(AESDecryptionMethod(message.getText()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        try {
            authorTextView.setText(AESDecryptionMethod(message.getName()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        return convertView;
    }


    private String AESDecryptionMethod(String encryptedMessage) throws UnsupportedEncodingException {
        //Initialize cryptography components
        try {
            cipher = Cipher.getInstance("AES");
            decipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        secretSpecKey = new SecretKeySpec(encryptionKey,"AES");

        byte[] encryptedByte = encryptedMessage.getBytes("ISO-8859-1");
        byte[] decryptionByte;
        String decryptedMessage = null;

        try {
            decipher.init(Cipher.DECRYPT_MODE, secretSpecKey);
            decryptionByte = decipher.doFinal(encryptedByte);
            decryptedMessage = new String(decryptionByte);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return decryptedMessage;
    }

}
