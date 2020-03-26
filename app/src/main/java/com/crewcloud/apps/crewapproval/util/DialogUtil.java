package com.crewcloud.apps.crewapproval.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crewcloud.apps.crewapproval.R;

public class DialogUtil {
    public interface OnAlertDialogViewClickEvent {
        void onOkClick(DialogInterface alertDialog);

        void onCancelClick();
    }

    public static void customAlertDialog(final Activity context, String title, String message, String okButton, String noButton, final OnAlertDialogViewClickEvent clickEvent) {
        // Build an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

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

        final AlertDialog dialog = builder.create();

        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickEvent != null) {
                    clickEvent.onOkClick(dialog);
                }
                dialog.dismiss();
            }
        });

        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickEvent != null) {
                    clickEvent.onCancelClick();
                }
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public static void customAlertDialog(final Activity context, String message, String okButton, String noButton, final OnAlertDialogViewClickEvent clickEvent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);

        builder.setView(dialogView);
        Button btn_positive = dialogView.findViewById(R.id.btn_yes);
        Button btn_negative = dialogView.findViewById(R.id.btn_no);
        TextView txtContent = dialogView.findViewById(R.id.txt_dialog_content);

        btn_negative.setText(noButton);
        if (noButton == null) {
            btn_negative.setVisibility(View.GONE);
        } else
            btn_positive.setText(okButton);
        txtContent.setText(message);

        final android.app.AlertDialog dialog = builder.create();

        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickEvent != null) {
                    clickEvent.onOkClick(dialog);
                }
                dialog.dismiss();
            }
        });

        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickEvent != null) {
                    clickEvent.onCancelClick();
                }
                dialog.cancel();
            }
        });

        dialog.show();

    }

    public static void oneButtonAlertDialog(final Activity context, String title, String message, String okButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);

        builder.setView(dialogView);

        Button btn_positive = dialogView.findViewById(R.id.btn_yes);
        Button btn_negative = dialogView.findViewById(R.id.btn_no);
        TextView txtTitle = dialogView.findViewById(R.id.txt_dialog_title);
        TextView txtContent = dialogView.findViewById(R.id.txt_dialog_content);

        btn_negative.setVisibility(View.GONE);
        btn_positive.setText(okButton);

        txtTitle.setText(title);
        txtContent.setText(message);

        final AlertDialog dialog = builder.create();
        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();

    }
}
