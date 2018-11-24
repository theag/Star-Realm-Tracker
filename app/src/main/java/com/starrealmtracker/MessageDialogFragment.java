package com.starrealmtracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Created by nbp184 on 2017/05/04.
 */
public class MessageDialogFragment extends DialogFragment {

    public static final String MESSAGE = "message";
    public static final String TITLE = "title";


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getArguments().getString(MESSAGE))
                .setTitle(getArguments().getString(TITLE));
        return builder.create();
    }
}
