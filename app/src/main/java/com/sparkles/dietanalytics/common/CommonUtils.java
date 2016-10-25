package com.sparkles.dietanalytics.common;

import java.util.Calendar;

/**
 * Created by pavanibaradi on 10/13/16.
 */
public class CommonUtils {

    public static String getTodaysDate(){
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_MONTH)+"-"+String.valueOf(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.YEAR);
    }
}
