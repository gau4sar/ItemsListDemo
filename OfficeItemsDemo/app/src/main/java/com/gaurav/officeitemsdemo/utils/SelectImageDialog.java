package com.gaurav.officeitemsdemo.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;


/**
 * Created by Gaurav on 10/8/2016.
 */
public class SelectImageDialog extends DialogFragment {

    public SelectedItemListener listener;

    public SelectImageDialog() {
    }

    @SuppressLint("ValidFragment")
    public SelectImageDialog(SelectedItemListener selectedItemListener) {
        this.listener = selectedItemListener;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Take Photo")) {
                listener.manageItemSelected(true);
                dialog.dismiss();
            } else if (items[item].equals("Choose from Library")) {
                listener.manageItemSelected(false);
                dialog.dismiss();
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    public interface SelectedItemListener {
        void manageItemSelected(boolean result);
    }
}
