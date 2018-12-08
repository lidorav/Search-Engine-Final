package Model.Parse;

/**
 * Class that responsible of parsing Percentage phrase (extends ANumbers)
 */
public class Percentage extends ANumbers {
    /**
     * parsing function for different percentage patterns
     * @param index
     * @param token
     * @return
     */
    public static String parsePercent(int index, String token){
        String res = "";
        if(token.contains("%"))
            return token;

        String nextToken = Parser.getTokenFromList(index+1).toLowerCase();
        if(nextToken.equals("percent") || nextToken.equals("percentage")) {
            res = parseNumber(index,token);
            res = res + "%";
            Parser.index++;
            return res;
        }

        String thirdToken = Parser.getTokenFromList(index+2).toLowerCase();
        if(thirdToken.equals("percent")||thirdToken.equals("percentage")){
            res = parseNumber(index,token);
            Parser.index++;
            res = res + "%";
            return res;
        }
        return res;
    }
}
