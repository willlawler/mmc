package com.willlawler.mmc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class whoWonFragment extends DialogFragment {

    public static whoWonFragment newInstance(String[] namesArray) {
        Bundle args = new Bundle();
        args.putStringArray("namesArray", namesArray);
        whoWonFragment frag = new whoWonFragment();
        frag.setArguments(args);
        return frag;
    }



    public interface NoticeDialogListener {

        void onClick(DialogFragment dialog, int which);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle mAregs = getArguments();
        String[] namesArray = mAregs.getStringArray("namesArray");


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Who won the last game?")
                .setItems(namesArray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onClick(whoWonFragment.this, which);
                        // The 'which' argument contains the index position
                        // of the selected item
                    }
                });
        return builder.create();
    }

}
