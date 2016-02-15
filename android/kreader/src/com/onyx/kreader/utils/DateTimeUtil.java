/**
 * 
 */
package com.onyx.kreader.utils;

import android.content.Context;
import android.text.format.DateFormat;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author joy
 *
 */
public class DateTimeUtil
{
    public static final SimpleDateFormat DATE_FORMAT_YYYYMMDD_HHMMSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    public static final SimpleDateFormat DATE_FORMAT_YYYYMMDD_HHMM = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    public static final SimpleDateFormat DATE_FORMAT_YYYYMMDD = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    
    /**
     * format the time according to the current locale and the user's 12-/24-hour clock preference
     * 
     * @param context
     * @return
     */
    public static String getCurrentTimeString(Context context) {
        return DateFormat.getTimeFormat(context).format(new Date());
    }
    
    public static String formatDate(Date date) {
    	if (date == null) {
    		return "";
    	}
		return DATE_FORMAT_YYYYMMDD_HHMMSS.format(date);
	}
    
    public static String formatDate(Date date, SimpleDateFormat simpleDateFormat) {
        if (date == null) {
            return "";
        }
        return simpleDateFormat.format(date);
    }
    

}
