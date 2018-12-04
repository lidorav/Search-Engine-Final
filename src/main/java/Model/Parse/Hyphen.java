package Model.Parse;

public class Hyphen {

    public static String parseHyphen(int index, String token) {
        String res = "";
        String part1;
        String part2;
        if (token.contains("-")) {
            if(token.charAt(0)=='-' || token.charAt(token.length()-1)=='-') {
                Parser.replaceToken(index, token.replace("-",""));
                return "";
            }
            if (token.matches(".*\\d+.*")) {
                String[] parts = token.split("-");
                if (parts[0].chars().allMatch(Character::isDigit)) {
                    part1 = ANumbers.parseNumber(index, parts[0]);
                }
                else{
                    part1 = parts[0];
                }
                if (parts[1].chars().allMatch(Character::isDigit))
                    part2 = ANumbers.parseNumber(index, parts[1]);
                else{
                    part2 = parts[1];
                }
                res = part1 + "-" + part2;
            }
            return res;
        }
        if(token.chars().allMatch(Character::isDigit)){
            String secToken = Parser.getTokenFromList(index+1);
            if(secToken.contains("-")){
                if(secToken.charAt(0)=='-' || secToken.charAt(secToken.length()-1)=='-') {
                    Parser.replaceToken(index, secToken.replace("-",""));
                    return "";
                }
                String[] parts = secToken.split("-");
                res = ANumbers.parseNumber(token,parts[0].toLowerCase());
                res = res + "-" + parts[1];
                return parseHyphen(index+1,res);
            }
        }
        if(token.toLowerCase().equals("between")){
            String thirdToken = Parser.getTokenFromList(index+2);
            if(thirdToken.toLowerCase().equals("and")){
                String secToken = Parser.getTokenFromList(index+1);
                if(secToken.chars().allMatch(Character::isDigit))
                    secToken = ANumbers.parseNumber(index, secToken);
                String forthToken = Parser.getTokenFromList(index+3);
                if(forthToken.chars().allMatch(Character::isDigit))
                    forthToken = ANumbers.parseNumber(index+3, forthToken);
                Parser.index = Parser.index + 3;
                return token + " " + secToken + " " + thirdToken + " " + forthToken;
            }
            String forthToken = Parser.getTokenFromList(index+3);
            if(forthToken.toLowerCase().equals("and")){
                String secToken = Parser.getTokenFromList(index+1);
                if(secToken.chars().allMatch(Character::isDigit))
                    secToken = ANumbers.parseNumber(index+1, secToken);
                String fifthToken = Parser.getTokenFromList(index+4);
                if(fifthToken.chars().allMatch(Character::isDigit))
                    fifthToken = ANumbers.parseNumber(index+4, fifthToken);
                Parser.index = Parser.index + 3;
                return token + " " + secToken + " " + forthToken + " " + fifthToken;
            }
        }
        return res;
    }
}
