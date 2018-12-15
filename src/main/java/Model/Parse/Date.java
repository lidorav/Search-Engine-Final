package Model.Parse;

import java.time.Month;

/**
 * Class that responsible of parsing Date phrase
 */
public class Date {

    private static final String[] dateArr = {"january" ,"jan" ,"february" ,"feb" ,"march" ,"mar" ,"april" ,"apr" ,
            "may" ,"june" ,"jun" ,"july" ,"jul" ,"august" ,"aug" ,"september" ,"sep" ,
            "october" ,"oct" ,"november" ,"nov" ,"december" ,"dec"};

    /**
     * parsing function for different date patterns
     * @param index
     * @param str
     * @return legal token
     */
    public static String dateParse (int index , String str) {
        String nextToken = Parser.getTokenFromList(index + 1);

        //DD Month pattern
        if (str.chars().allMatch(Character::isDigit) && isMonth(nextToken)) {
            if(isShortMonth(nextToken))
                nextToken=toLongMonth(nextToken);
            Parser.index++;
            return Month.valueOf(nextToken.toUpperCase()).getValue() + "-" + str;
        }
        //Month DD pattern
        if(isMonth(str) && isDay(nextToken)){
            if(isShortMonth(str))
                str=toLongMonth(str);
            Parser.index++;
            return Month.valueOf(str.toUpperCase()).getValue() + "-" + nextToken;
        }
        //YYYY MM pattern
        if(isMonth(str) && isYear((nextToken))){
            if(isShortMonth(str))
                str=toLongMonth(str);
            Parser.index++;
            return Month.valueOf(str.toUpperCase()).getValue() + "-" + nextToken;
        }
        return "";
    }

    /**
     * boolean method that checks if a given string is a known  Month
     * @param targetValue
     * @return true if string is a month
     */
    private static boolean isMonth(String targetValue) {
        targetValue=targetValue.toLowerCase();
        for(String s: dateArr){
            if(s.equals(targetValue))
            return true;
        }
        return false;
    }

    /**
     *boolean method that checks if a given string is a known short  Month
     * @param token
     * @return true if string is a short month
     */
    private static boolean isShortMonth(String token) {
        token = token.toLowerCase();
        return token.length() == 3 && (!token.equals("may"));

    }

    /**
     * replace the short description to a Month to a long month writing
     * @param targetValue
     * @return long month writing
     */
    private static String toLongMonth(String targetValue){
        targetValue=targetValue.toLowerCase();
        for (String aDateArr : dateArr) {
            if (aDateArr.contains(targetValue) && aDateArr.length() > 3) {
                return aDateArr;
            }
        }
        return "";
    }

    /**
     * boolean function that checks if a given string is a year
     * @param targetValue
     * @return true if it a year of 4 chars
     */
    private static boolean isYear(String targetValue) {
        return targetValue.length() == 4 && allDigits(targetValue);
    }

    /**
     * boolean function if a given string is all digits
     * @param s
     * @return true if only digits on string
     */
    private static boolean allDigits(String s) {
        return s.replaceAll("\\d", "").equals("");
    }

    /**
     * boolean function that checks if a given string is a day in the range of 0-31
     * @param s
     * @return true if in the range above
     */
    private static boolean isDay(String s){
        return s.matches("^(([0]?[1-9])|([1-2][0-9])|(3[01]))$");
    }
}