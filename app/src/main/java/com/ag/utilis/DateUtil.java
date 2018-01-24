package com.ag.utilis;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;import java.text.DateFormat;
import java.util.TimeZone;


public class DateUtil  implements Serializable {

    public static void main(String args[]) throws ParseException {
        Date date1 = getDate("22-01-2018");
        Date date2 = new Date();
        System.out.println("ago2 : " + ago2(date1));
//        System.out.println("Date 1 : " + date1);
//        System.out.println("Date 2 : " + date2);
//        System.out.println("Date2 Milis : " + date2.getTime());
//        System.out.println("Date2 2 : " + new Date(date2.getTime()));
//        System.out.println("DifferenceInMonths : " + DifferenceInMonths(date1, date2));
//        System.out.println("DifferenceInYears : " + DifferenceInYears(date1, date2));
//        System.out.println("DifferenceInDays : " + DifferenceInDays(date1, date2));
//        System.out.println("DifferenceInHours : " + DifferenceInHours(date1, date2));
//        System.out.println("DifferenceInMinutes : " + DifferenceInMinutes(date1, date2));
//        System.out.println("DifferenceInSeconds : " + DifferenceInSeconds(date1, date2));
//        System.out.println("DifferenceInMilliseconds : " + DifferenceInMilliseconds(date1, date2));
//        System.out.println("GetTimeInMilliseconds : " + GetTimeInMilliseconds(date2));
    }



    public static String ago(Date date1) {
        Date date2 = new Date();
        int years = DifferenceInYears(date1, date2).intValue();
        if (years >= 1){
            if(years > 1)
                return years + " yrs ago";
            return "A yr ago";
        }
        int months = DifferenceInMonths(date1, date2).intValue();
        if(months >= 1) {
            if(months > 1)
                return months + " mnths ago";
            return "Last month";
        }
        int days = DifferenceInDays(date1, date2).intValue();
        if(days >= 1) {
            if(days >= 21)
                return 3 + " weeks ago";
            if(days >= 14)
                return 2 + " weeks ago";
            if(days >= 7)
                return "Last week";
            if(days > 1)
                return days + " days ago";
            return "Yesterday";
        }
        int hours = DifferenceInHours(date1, date2).intValue();
        if(hours >= 1) {
            if(hours > 1)
                return hours + " hrs ago";
            return "An hr ago";
        }
        int minutes = DifferenceInMinutes(date1, date2).intValue();
        if(minutes >= 1) {
            if(minutes > 1)
                return minutes + " mins ago";
            return "A min ago";
        }

        /*int seconds = DifferenceInSeconds(date1, date2).intValue();
        if(seconds >= 1) {
            if(seconds > 1)
                return seconds + " seconds ago";
            return "a second ago";
        }*/

        else {
            return "Just now";
        }
    }

    public static String ago2(Date date1) {
        Date date2 = new Date();
        int day = DifferenceInDays(date1, date2).intValue();
        if (day <= 1){
            SimpleDateFormat month_day = new SimpleDateFormat("hh:mm");
            return month_day.format(date1);
        }
        if (day <= 2){
            SimpleDateFormat month_day = new SimpleDateFormat("hh:mm");
            return "Yesterday " + month_day.format(date1);
        }
        else {
            SimpleDateFormat month_day = new SimpleDateFormat("d/MMM hh:mm");
            return month_day.format(date1);
        }
    }

    public static Double DifferenceInMonths(Date date1, Date date2) {
        return DifferenceInYears(date1, date2) * 12;
    }

    public static Double DifferenceInYears(Date date1, Date date2) {
        Double days = DifferenceInDays(date1, date2);
        return days / 365.2425;
    }

    public static Double DifferenceInDays(Date date1, Date date2) {
        return DifferenceInHours(date1, date2) / 24.0;
    }

    public static Double DifferenceInHours(Date date1, Date date2) {
        return DifferenceInMinutes(date1, date2) / 60.0;
    }

    public static Double DifferenceInMinutes(Date date1, Date date2) {
        return DifferenceInSeconds(date1, date2) / 60.0;
    }

    public static Double DifferenceInSeconds(Date date1, Date date2) {
        return DifferenceInMilliseconds(date1, date2) / 1000.0;
    }

    public static long DifferenceInMilliseconds(Date date1, Date date2) {
        return Math.abs(GetTimeInMilliseconds(date1)
                - GetTimeInMilliseconds(date2));
    }

    public static long GetTimeInMilliseconds(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getTimeInMillis() + cal.getTimeZone().getOffset(cal.getTimeInMillis());
    }

    public static String addMonthsToDate(String theDate, int monthsToAdd) throws ParseException{
        String dt = theDate;  // Start date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(sdf.parse(dt));
        c.add(Calendar.MONTH, monthsToAdd);  // number of months to add
        return dt = sdf.format(c.getTime());  // dt is now the new date
    }

    public static Date addYearsToDate(Date date, int years) {
        Date dateToSubtract = date; //do this to avoid overriding the date provided
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateToSubtract);
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR)+years);
        return cal.getTime();
    }

    public static Date getDate(Date date, int day) {
        Date current = date; //do this to avoid overriding the date provided
        Calendar cal = Calendar.getInstance();
        cal.setTime(current);
        cal.set(Calendar.DAY_OF_MONTH,  day);
        return cal.getTime();
    }

    public static Date addMonthsToDate(Date date, int months) {
        Date dateToSubtract = date; //do this to avoid overriding the date provided
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateToSubtract);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)+months);
        return cal.getTime();
    }


    public static Date getDateAgoOrTo(Date currentDate, boolean future, int months){
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        if(future)
            cal.add(Calendar.MONTH, months);
        else
            cal.add(Calendar.MONTH, (months*-1));
        return cal.getTime();
    }

    public static Date firstDateNextMonth(Date date){
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(date);
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        if (cal.get(Calendar.MONTH) == Calendar.DECEMBER) {
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
        }
        return cal.getTime();
    }

    public static int dayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public static int getMonthsBetweenDates(Date startDate, Date endDate) {
        //This method comptutes to the decimal the number of months between fromDate to endDate
        if (startDate.getTime() > endDate.getTime()) {
            Date temp = startDate;
            startDate = endDate;
            endDate = temp;
        }
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);

        int yearDiff = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        int monthsBetween = endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH) + (12 * yearDiff);

        if (endCalendar.get(Calendar.DAY_OF_MONTH) >= startCalendar.get(Calendar.DAY_OF_MONTH))
            monthsBetween = monthsBetween + 1;
        return monthsBetween;
    }

    public static Date getStartDate(Date asAt, int interval) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(asAt);
        cal.add(Calendar.MONTH, -1 * interval);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    public String getMonths(int num){
        String months[]= {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<num;i++){
            sb.append("'"+months[i]+"'");
            if(i!=num-1){
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public static int getYear(Date date){
        SimpleDateFormat df = new SimpleDateFormat("yyyy");
        if(date!=null)
            return Integer.parseInt(df.format(date));
        else
            return 0;
    }

    public static int getMonthNumber(Date date){
        SimpleDateFormat df = new SimpleDateFormat("M");
        if(date!=null)
            return  Integer.parseInt(df.format(date));
        else
            return 0;
    }

    public static Date getEndMonthDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));

        return c.getTime();
    }

    public static Date getFirstMonthDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
        return c.getTime();
    }

    public static Date getDate(String dateString) throws ParseException {
        DateFormat df = new SimpleDateFormat("dd-MM-yy");
        return df.parse(dateString);
    }

}