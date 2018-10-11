package com.saganet.encuestas;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by Oblinaty on 22/10/2016.
 */
public class TimeSet extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {


    public TimeSet() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }


    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        if(DataUpload.UPDATE_PREGUNTAS_COMPONENT){
            Preguntas.mYear=year;
            Preguntas.mMes=month;
            Preguntas.mDay=day;
            Preguntas.updateDisplay();
        }else {
            AddContactoFragment.mYear=year;
            AddContactoFragment.mMes=month;
            AddContactoFragment.mDay=day;
            AddContactoFragment.updateDisplay();
        }
    }
}
