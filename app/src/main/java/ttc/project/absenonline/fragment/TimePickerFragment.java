package ttc.project.absenonline.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Fikry-PC on 7/14/2017.
 */

public class TimePickerFragment extends DialogFragment {
    final static String time_minute_key = "minute";
    final static String time_hour_key = "hour";
    TimePickerDialog.OnTimeSetListener mOnTimeSetListener;
    public void setOnDateSetListener(TimePickerDialog.OnTimeSetListener onTimeSetListener){
        this.mOnTimeSetListener = onTimeSetListener;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), mOnTimeSetListener, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

}