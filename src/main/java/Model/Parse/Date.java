package Model.Parse;

import java.time.Month;

public class Date {

    private static final String[]  dateArr = {"january" ,"jan" ,"february" ,"feb" ,"march" ,"mar" ,"april" ,"apr" ,
            "may" ,"june" ,"jun" ,"july" ,"jul" ,"august" ,"aug" ,"september" ,"sep" ,
            "october" ,"oct" ,"november" ,"nov" ,"december" ,"dec"};


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

    private static boolean isMonth(String targetValue) {
        targetValue=targetValue.toLowerCase();
        for(String s: dateArr){
            if(s.equals(targetValue))
            return true;
        }
        return false;
    }

    private static boolean isShortMonth(String token) {
        token = token.toLowerCase();
        return token.length() == 3 && (!token.equals("may"));

    }

    private static String toLongMonth(String targetValue){
        targetValue=targetValue.toLowerCase();
        for (String aDateArr : dateArr) {
            if (aDateArr.contains(targetValue) && aDateArr.length() > 3) {
                return aDateArr;
            }
        }
        return "";
    }

    private static boolean isYear(String targetValue) {
        return targetValue.length() == 4 && allDigits(targetValue);
    }
    private static boolean allDigits(String s) {
        return s.replaceAll("\\d", "").equals("");
    }

    private static boolean isDay(String s){
        return s.matches("^(([0]?[1-9])|([1-2][0-9])|(3[01]))$");
    }
}