package Model.Parse;

public class Quotation {

    public static String parseQuotation(int index, String token){
        String nextToken = "";
        StringBuilder res= new StringBuilder();
        int i=1;
        if(token.charAt(0)=='"') {
            if (token.charAt(token.length() - 1) == '"') {
                res = new StringBuilder(token.replace("\"", ""));
                return res.toString().toLowerCase();
            } else {
                res = new StringBuilder(token.substring(1));
                Parser.replaceToken(index, res.toString());
                while (!nextToken.contains("\"")) {
                    if(i>=10)
                        return "";
                    nextToken = Parser.getTokenFromList(index + i);
                    res.append(" ").append(nextToken);
                    i++;
                }
                nextToken = nextToken.substring(0, nextToken.length() - 1);
                Parser.replaceToken(index + i - 1, nextToken);
                Parser.index--;
                res = new StringBuilder((res.substring(0, res.length() - 1)).toLowerCase());
            }
        }
        return res.toString();
    }
}
