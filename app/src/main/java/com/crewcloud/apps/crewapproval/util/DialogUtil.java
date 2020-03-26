package com.crewcloud.apps.crewapproval.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crewcloud.apps.crewapproval.R;

/**
 * Created by Shazam_ORG on 3/26/2018.
 */

public class DialogUtil {
    public interface OnAlertDialogViewClickEvent {
        void onOkClick(DialogInterface alertDialog);

        void onCancelClick();
    }
    public static void customAlertDialog(final Activity context, String title, String message, String okButton, String noButton, final OnAlertDialogViewClickEvent clickEvent) {
        // Build an AlertDialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);

        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);

        // Set the custom layout as alert dialog view
        builder.setView(dialogView);

        // Get the custom alert dialog view widgets reference
        Button btn_positive = dialogView.findViewById(R.id.btn_yes);
        Button btn_negative = dialogView.findViewById(R.id.btn_no);
        TextView txtTitle = dialogView.findViewById(R.id.txt_dialog_title);
        TextView txtContent = dialogView.findViewById(R.id.txt_dialog_content);

        btn_negative.setText(noButton);
        btn_positive.setText(okButton);
        txtTitle.setText(title);
        txtContent.setText(message);

//        Button btn_neutral = (Button) dialogView.findViewById(R.id.dialog_neutral_btn);

        // Create the alert dialog
        final android.app.AlertDialog dialog = builder.create();

        // Set positive/yes button click listener
        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the alert dialog
                if (clickEvent != null) {
                    clickEvent.onOkClick(dialog);
                }
                dialog.dismiss();
            }
        });

        // Set negative/no button click listener
        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the alert dialog
                if (clickEvent != null) {
                    clickEvent.onCancelClick();
                }
                dialog.cancel();
            }
        });

        // Set cancel/neutral button click listener
//        btn_neutral.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Dismiss/cancel the alert dialog
//                dialog.cancel();
//                tv_message.setText("Cancel button clicked");
//            }
//        });

        // Display the custom alert dialog on interface
        dialog.show();

    }

    public static void customAlertDialog(final Activity context, String message, String okButton, String noButton, final OnAlertDialogViewClickEvent clickEvent) {
        // Build an AlertDialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);

        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);

        // Set the custom layout as alert dialog view
        builder.setView(dialogView);

        // Get the custom alert dialog view widgets reference
        Button btn_positive = (Button) dialogView.findViewById(R.id.btn_yes);
        Button btn_negative = (Button) dialogView.findViewById(R.id.btn_no);
//        TextView txtTitle = (TextView) dialogView.findViewById(R.id.txt_dialog_title);
        TextView txtContent = (TextView) dialogView.findViewById(R.id.txt_dialog_content);

        btn_negative.setText(noButton);
        if (noButton == null) {
            btn_negative.setVisibility(View.GONE);
        } else
            btn_positive.setText(okButton);
//        txtTitle.setText(title);
        txtContent.setText(message);

//        Button btn_neutral = (Button) dialogView.findViewById(R.id.dialog_neutral_btn);

        // Create the alert dialog
        final android.app.AlertDialog dialog = builder.create();

        // Set positive/yes button click listener
        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the alert dialog
                if (clickEvent != null) {
                    clickEvent.onOkClick(dialog);
                }
                dialog.dismiss();
            }
        });

        // Set negative/no button click listener
        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the alert dialog
                if (clickEvent != null) {
                    clickEvent.onCancelClick();
                }
                dialog.cancel();
            }
        });

        // Display the custom alert dialog on interface
        dialog.show();

    }

    public static void oneButtonAlertDialog(final Activity context, String title, String message, String okButton) {
        // Build an AlertDialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);

        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);

        // Set the custom layout as alert dialog view
        builder.setView(dialogView);

        // Get the custom alert dialog view widgets reference
        Button btn_positive = (Button) dialogView.findViewById(R.id.btn_yes);
        Button btn_negative = (Button) dialogView.findViewById(R.id.btn_no);
        TextView txtTitle = (TextView) dialogView.findViewById(R.id.txt_dialog_title);
        TextView txtContent = (TextView) dialogView.findViewById(R.id.txt_dialog_content);

        btn_negative.setVisibility(View.GONE);
        btn_positive.setText(okButton);

        txtTitle.setText(title);
        txtContent.setText(message);

//        Button btn_neutral = (Button) dialogView.findViewById(R.id.dialog_neutral_btn);

        // Create the alert dialog
        final android.app.AlertDialog dialog = builder.create();

        // Set negative/no button click listener
        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the alert dialog
//                if (clickEvent != null) {
//                    clickEvent.onCancelClick();
//                }
                dialog.cancel();
            }
        });

        // Display the custom alert dialog on interface
        dialog.show();

    }
}
