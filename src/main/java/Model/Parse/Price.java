package Model.Parse;

/**
 * Class that responsible of parsing Percentage phrase (extends ANumbers)
 */
public class Price extends ANumbers {

    /**
     * price parse function for different pattern of Dollar
     * @param index
     * @param firstToken
     * @return a legal Dollar combined term
     */
    public static String parsePrice(int index, String firstToken){
        String fraction = "";
        String secToken = Parser.getTokenFromList(index+1).toLowerCase();
        String res;
        if(firstToken.contains("$")){
            res = firstToken.replace("$","");
            res = convertToMillions(res, secToken);
            return res + " Dollars";
        }
        if(secToken.contains("dollar")){
            res = convertToMillions(firstToken, secToken);
            Parser.index++;
            return res + " Dollars";
        }
        String thirdToken = Parser.getTokenFromList(index+2).toLowerCase();
        if(thirdToken.contains("dollar")){
            res = convertToMillions(firstToken,secToken);
            Parser.index++;
            if(secToken.matches("\\d+/\\d+")) {
                fraction = " "+secToken;
                Parser.index++;
            }
            return res + fraction + " Dollars";
        }
        String forthToken = Parser.getTokenFromList(index+3).toLowerCase();
        if(forthToken.contains("dollar")){
            res = convertToMillions(firstToken,secToken);
            Parser.index = Parser.index + 2;
            if(secToken.matches("\\d+/\\d+")) {
                fraction = " "+secToken;
                Parser.index++;
            }
            return res + fraction + " Dollars";
        }

        return "";
    }

    /**
     * convert To Millions / BILLION /TRILLION
     * @param str
     * @param nextStr
     * @return a legal number term
     */
    private static String convertToMillions(String str, String nextStr) {
        final long MILLION = 1000000L;
        final long BILLION = 1000000000L;
        final long TRILLION = 1000000000000L;
        try {
            double number = Double.parseDouble(str);
            if(nextStr.equals("trillion")){
                str = number * (TRILLION/MILLION) + " M";
                Parser.index++;
                return str;
            }
            if(nextStr.equals("billion") || nextStr.equals("bn")){
                str = number * (BILLION/MILLION) + " M";
                Parser.index++;
                return str;
            }
            if(nextStr.equals("million") || nextStr.equals("m")){
                str = number + " M";
                Parser.index++;
                return str;
            }
            if (number >= MILLION)
                return number / MILLION + " M";
            return str;
        }catch (Exception e){
            return "";
        }
    }
}
