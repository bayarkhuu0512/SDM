package com.skytel.sdp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.skytel.sdp.R;

public class ConfirmDialog extends DialogFragment {

    OnDialogConfirmListener mListener;

    public interface OnDialogConfirmListener {
        public void onPositiveButton();

        public void onNegativeButton();
    }

    public void registerCallback(OnDialogConfirmListener dc) {
        mListener = dc;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");
        int message = getArguments().getInt("message");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        //    alertDialog.setIcon(R.drawable.ic_info);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mListener != null) {
                            mListener.onPositiveButton();
                        }
                    }
                }
        );
        alertDialog.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mListener != null) {
                            mListener.onNegativeButton();
                        }
                    }
                }
        );

        return alertDialog.create();
    }

}
