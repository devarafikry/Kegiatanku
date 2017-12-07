package ttc.project.absenonline.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by Fikry-PC on 12/5/2017.
 */

public class DatePickerFragment extends DialogFragment
        {

    DatePickerDialog.OnDateSetListener mOnDateSetListener;
    public void setOnDateSetListener(DatePickerDialog.OnDateSetListener onDateSetListener){
        this.mOnDateSetListener = onDateSetListener;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), mOnDateSetListener, year, month, day);
    }

}