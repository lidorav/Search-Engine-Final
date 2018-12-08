package Model.Parse;

/**
 * new rule class the takes a combo prhase ( big letter sequence ) and parse it to terms
 */
public class Combo {

    /**
     * a parsing function that create a combo token
     * @param index
     * @param token
     * @return combo token
     */
    public static String parseCombo(int index, String token) {
        StringBuilder res;
        int counter =1;
        if (token.contains("!"))
            return "";
        if(Character.isUpperCase(token.charAt(0))){
            res = new StringBuilder(token);
            String nextToken = (Parser.getTokenFromList(index+1));
            while (Character.isUpperCase(nextToken.charAt(0))){
                res.append(" ").append(nextToken);
                Parser.replaceToken(index + counter , nextToken + "!");
                counter ++;
                nextToken = (Parser.getTokenFromList(index+counter));
            }
            if(counter>1) {
                Parser.replaceToken(index, token + "!");
                Parser.index--;
                //return a lower case word
                return res.toString().toLowerCase();
            }
        }
        return "";
    }


}
