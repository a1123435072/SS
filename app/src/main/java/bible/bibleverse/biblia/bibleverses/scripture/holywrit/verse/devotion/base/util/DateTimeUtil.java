package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.App;

public class DateTimeUtil {
    static ThreadLocal<DateFormat> mMediumDateFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return android.text.format.DateFormat.getMediumDateFormat(App.context);
        }
    };

    static ThreadLocal<DateFormat> mTimeFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return android.text.format.DateFormat.getTimeFormat(App.context);
        }
    };

    public static int nowDateTime() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    /**
     * Convert Date to unix time
     */
    public static int toInt(Date date) {
        return (int) (date.getTime() / 1000);
    }

    /**
     * Convert unix time to Date
     */
    public static Date toDate(int date) {
        return new Date((long) date * 1000);
    }


    public static String getLocaleTime4Display(Date date) {
        return mTimeFormat.get().format(date);
    }

    public static String getOffsetDateString(String originalDateString, int offset) {
        try {
            // Create a date formatter using your format string
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

            // Parse the given date string into a Date object.
            // Note: This can throw a ParseException.
            Date myDate = dateFormat.parse(originalDateString);

            // Use the Calendar class to subtract one day
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(myDate);
            calendar.add(Calendar.DAY_OF_YEAR, offset);

            // Use the date formatter to produce a formatted date string
            Date previousDate = calendar.getTime();
            String result = dateFormat.format(previousDate);

            return result;
        } catch (Exception e) {
            return originalDateString;
        }
    }

    public static String getLocaleDateStr4Display(Date date) {
//        return mMediumDateFormat.get().format(date);
        return dateToMMMdString(date);
    }

    public static String getLocaleDateStr4Display(String originalString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        ParsePosition pos = new ParsePosition(0);
        java.util.Date date = formatter.parse(originalString, pos);

        return getLocaleDateStr4Display(date);
    }

    public static String getLocaleDateStr4Display(long paramLong) {
        return getLocaleDateStr4Display(new Date(paramLong));
    }

    private static String dateToMMMdString(Date time) {
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("MMM dd");
        String ctime = formatter.format(time);

        return ctime;
    }

    public static String formatDate2MMMd(long paramLong) {
        return new SimpleDateFormat("MMM dd").format(new Date(paramLong));
    }

    public static String getDateStr4ApiRequest(long paramLong) {
        return new SimpleDateFormat("yyyyMMdd").format(new Date(paramLong));
    }

    public static String getHour(long paramLong) {
        return new SimpleDateFormat("HH").format(new Date(paramLong));
    }

    public static String getMinute(long paramLong) {
        return new SimpleDateFormat("mm").format(new Date(paramLong));
    }

    public static String getDay(long paramLong) {
        return new SimpleDateFormat("dd", Locale.ENGLISH).format(new Date(paramLong));
    }

    public static String getMonth(long paramLong) {
        return new SimpleDateFormat("MM", Locale.ENGLISH).format(new Date(paramLong));
    }

    public static int covertToReminderTime(long timestamp) {
        // input: timestamp    out:  1024 , mean 10:24
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return hour * 100 + minute;
    }

    public static String reminderTimeToDisplay(int reminderTime) {
        String format = "%02d";
        return String.format(format, reminderTime / 100) + ":" + String.format(format, reminderTime % 100);
    }

    public static int[] parseReminderTime(int reminderTime) {
        return new int[]{reminderTime / 100, reminderTime % 100};
    }

    public static String getPointFormatDateStr(long paramLong) {
        return new SimpleDateFormat("yyyy.MM.dd").format(new Date(paramLong));
    }

    public static String getPointFormatDateStr(int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        return getPointFormatDateStr(calendar.getTimeInMillis());
    }

    public static int getDayFromPointFormat(String pointFormatDate) {
        Calendar calendar = getCalendarFromPointFormat(pointFormatDate);
        if (calendar != null) {
            return calendar.get(Calendar.DAY_OF_MONTH);
        }
        return 0; // default
    }

    public static Calendar getCalendarFromPointFormat(String pointFormatDate) {
        try {
            // Create a date formatter using your format string
            DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");

            // Parse the given date string into a Date object.
            // Note: This can throw a ParseException.
            Date myDate = dateFormat.parse(pointFormatDate);

            // Use the Calendar class to subtract one day
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(myDate);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);

            return calendar;
        } catch (Exception e) {
            return null;
        }
    }

    public static int getTodayDate() {
        String dateStr = getDateStr4ApiRequest(System.currentTimeMillis());
        int date = Integer.parseInt(dateStr);
        return date;
    }

    public static int[] getTodayYMD() {
        long date = getTodayDate();
        int year = (int) (date / 10000);
        int month = (int) ((date % 10000) / 100);
        int day = (int) (date % 100);
        return new int[]{
                year, month, day
        };
    }

    private static String[] mMonthShortName = DateFormatSymbols.getInstance(Locale.ENGLISH).getShortMonths();

    public static String getShortMonthName(int month) {
        return (month < mMonthShortName.length + 1) ? mMonthShortName[month - 1] : "ERR";
    }

    public static String[] getTimeAndUnitFromMillisecond(long millisecond) {
        long sec = millisecond / 1000;
        if (sec < 60) {
            return new String[]{
                    String.valueOf(sec),
                    "Sec"
            };
        }
        long min = sec / 60;
        if (min < 60) {
            return new String[]{
                    String.valueOf(min),
                    "Min"
            };
        }
        long hour = min / 60;
        if (hour < 24) {
            return new String[]{
                    String.valueOf(hour),
                    "Hour"
            };
        }
        long day = hour / 24;
        return new String[]{
                String.valueOf(day),
                "Day"
        };
    }

    public static int[] getDateArray(long date) {
        int year = (int) (date / 10000);
        int month = (int) ((date % 10000) / 100);
        int day = (int) (date % 100);
        return new int[]{
                year, month, day
        };
    }

    public static Calendar getDateCalendar(long date) {
        int[] dateArray = getDateArray(date);
        Calendar calendar = Calendar.getInstance();
        calendar.set(dateArray[0], dateArray[1] - 1, dateArray[2]);
        return calendar;
    }

    public static String getDateWithOffset(String curDate, int offset) {
        try {
            long curDateLong = Long.parseLong(curDate);
            return String.valueOf(getDateWithOffset(curDateLong, offset));
        } catch (Exception e) {

        }
        return "";
    }

    public static long getDateWithOffset(long curDate, int offset) {
        Calendar calendar = getDateCalendar(curDate);
        calendar.add(Calendar.DAY_OF_YEAR, offset);
        return formatDate(calendar);
    }

    public static long formatDate(int year, int month, int day) {
        return year * 10000 + month * 100 + day;
    }

    public static long formatDate(Calendar calendar) {
        if (calendar == null) {
            return 0;
        }
        return formatDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
        );
    }
}
