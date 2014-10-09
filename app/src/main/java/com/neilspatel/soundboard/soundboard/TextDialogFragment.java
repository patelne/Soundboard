package com.neilspatel.soundboard.soundboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.io.File;

/**
 * Created by Neil on 10/2/2014.
 */
public class TextDialogFragment extends DialogFragment {
    String TAG = "Rename Dialog";
    File mFile;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_rename, null);
        builder.setView(view);

        /*------------------------------------------
        Accept user input handler
        ------------------------------------------*/
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Dialog OK");

                /*------------------------------------------
                Return the text that was entered in the dialog
                ------------------------------------------*/
                EditText editText = (EditText)view.findViewById(R.id.dialog_filename);
                boolean success = ((SoundBoard) (TextDialogFragment.this.getActivity())).onDialogOk(editText.getText().toString());
                dismiss();
            }
        });

        /*------------------------------------------
        Cancel handler
        ------------------------------------------*/
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Dialog Cancel");
                /*------------------------------------------
                Don't need to do anything. Just quit.
                ------------------------------------------*/
            }
        });

        return builder.create();
    }
}
