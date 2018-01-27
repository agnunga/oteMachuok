package com.ag.utilis;

/**
 * Created by agufed on 1/24/18.
 */

public class CommonUtil {
    /*public static String cutString(String s) {
        if(s.length() > 40){
            return s.substring(0, 39) + " ...";
        }
        return s;
    }*/
    public static String cutCount(String s) {
        try {
            if(Integer.parseInt(s) > 99){
                return "99+";
            } return s;
        }catch (NumberFormatException e){
            return "0";
        }
    }
    public static String cutCount(long s) {
        if(s > 99){
            return "99+";
        }
        return String.valueOf(s);
    }
}
