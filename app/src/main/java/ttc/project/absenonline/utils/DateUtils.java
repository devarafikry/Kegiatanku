package ttc.project.absenonline.utils;

import android.content.Context;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import ttc.project.absenonline.R;

/**
 * Created by Fikry-PC on 12/5/2017.
 */

public class DateUtils {
    public static String getFriendlyAllDateAndTime(Context context, Long date, Long startTime, Long endTime){
        String startTimeText = getFriendlyTime(startTime);
        String endTimeText = getFriendlyTime(endTime);
        String dateText = getFriendlyDate(context, date);

        return dateText +" jam "+startTimeText+" hingga "+endTimeText;
    }

    public static String getFriendlyTimeStartAndEnd(Long startTime, Long endTime){
        String startTimeText = getFriendlyTime(startTime);
        String endTimeText = getFriendlyTime(endTime);
        return startTimeText+" - "+endTimeText;
    }

    public static String getFriendlyTime(Long time){
        int hours = (int) TimeUnit.MILLISECONDS.toHours(time);
        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(time);

        String sHours, sMinutes;
        if (hours < 10){
            sHours = "0"+String.valueOf(hours);
        } else{
            sHours = String.valueOf(hours);
        }

        if (minutes%60 <10){
            sMinutes = "0"+String.valueOf(minutes%60);
        } else{
            sMinutes = String.valueOf(minutes%60);
        }
        return sHours+":"+sMinutes;
    }

    public static String getFriendlyDate(Context c, Long date){
        Calendar calendar = Calendar.getInstance();
        long millis = date;
        calendar.setTimeInMillis(millis);

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        String monthString = new DateFormatSymbols().getMonths()[mMonth-1];

        return String.format(c.getResources().getString(R.string.date),
                String.valueOf(mDay),
                monthString,
                String.valueOf(mYear));
    }
}
