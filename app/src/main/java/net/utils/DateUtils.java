package net.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by mont's on 4/8/2017.
 */
public class DateUtils {


    public static String formatToYesterdayOrToday_detail(String date) throws ParseException {
        Date dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);

        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))
        {
            return "TODAY";
        }
        else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR))
        {

            return "YESTERDAY";
        }
        else
        {
            SimpleDateFormat date_format = new SimpleDateFormat("dd MMM, yyyy");
            String str_date = date_format.format(dateTime);
            return str_date+"";
        }
    }

}
