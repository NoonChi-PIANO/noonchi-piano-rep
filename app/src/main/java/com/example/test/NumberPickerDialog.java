package com.example.test;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.NumberPicker;

import androidx.fragment.app.DialogFragment;

public class NumberPickerDialog extends DialogFragment {
    private NumberPicker.OnValueChangeListener valueChangeListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final NumberPicker numberPicker = new NumberPicker(getActivity());

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(10);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Value");
        builder.setMessage("Choose a number :");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                valueChangeListener.onValueChange(numberPicker,
                        numberPicker.getValue(), numberPicker.getValue());
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                valueChangeListener.onValueChange(numberPicker,
                        numberPicker.getValue(), numberPicker.getValue());
            }
        });

        builder.setView(numberPicker);
        return builder.create();
    }


}
