package com.github.fredrik9000.firmadetaljer_android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class FilterChoiceDialog extends DialogFragment {

    private OnSelectDialogInteractionListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.select_search_filter);
        final CharSequence[] items = {
                getResources().getString(R.string.select_firmanavn),
                getResources().getString(R.string.select_org_number)
        };
        return builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                listener.onSelectDialogInteraction(item);
                dismiss();
            }
        }).create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSelectDialogInteractionListener) {
            listener = (OnSelectDialogInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSelectDialogInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnSelectDialogInteractionListener {
        void onSelectDialogInteraction(int choice);
    }
}
