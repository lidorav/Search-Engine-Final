package Model.PartA.Parse;

import Model.PartA.Index.Dictionary;

/**
 * Class that represent only text tokens
 */
 class Text {

    /**
     *  Converting a given token to term by uppercase or lowercase by defined conditions
     * @param index the index in the token list
     * @param token the text token to be converted
     * @return
     */
    public static String parseText(int index, String token){
        token = token.replace("!","");
        if(token.contains("\"") && token.length()>1)
            token = token.replace("\"","");
        String upperToken = token.toUpperCase();
        String lowerToken = token.toLowerCase();
        //check if first letter is uppercased
        if(Character.isUpperCase(token.charAt(0))) {
            //check if uppercased word exist in dictionary, if so return uppercased
            if (Dictionary.checkExist(upperToken)||Parser.checkExist(upperToken)) {
                return upperToken;
            }
            else {
                //check if lowered cased exist in the dictionary, if so return lowercased
                if (Dictionary.checkExist(lowerToken)||Parser.checkExist(lowerToken))
                    return lowerToken;
                //if none if the cases exist = dictionary doesn't contain this word return uppercased
                else
                    return upperToken;
            }
        }
        else {
            //check if uppercase cased exist in the dictionary, if so replace to lowercase in dictionary and return it
            if (Dictionary.checkExist(upperToken)) {
                Dictionary.replaceTerm(upperToken, lowerToken);
                return lowerToken;
            }
            if (Parser.checkExist(upperToken)) {
                Parser.replaceTerm(upperToken, lowerToken);
                return lowerToken;
            }
            //check if lowered cased exist in the dictionary, if so return lowercased
            //if (Dictionary.checkExist(lowerToken) || Parser.checkExist())
              //  return lowerToken;
                //if none if the cases exist = dictionary doesn't contain this word return lowercase
            //else {
                return lowerToken;
            //}
        }
    }
}
