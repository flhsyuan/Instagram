package com.example.asus.instagram.Utils;

//Yuan: used to manipulate some string...
public class StringManipulation {

    // replace "_" in user name with a space
    public static String condenseUsername(String userName){
        return userName.replace(" ","_");
    }

    // replace a space in user name with "_"
    public static String expandUsername(String userName){ return userName.replace("_"," "); }

    public static String getTags(String string){
        if(string.indexOf("#") > 0){
            StringBuilder sb = new StringBuilder();
            char[] charArray = string.toCharArray();
            boolean foundWord = false;
            for(char c : charArray){
                if(c == '#'){
                    foundWord = true;
                    sb.append(c);
                }else{
                    if(foundWord){
                        sb.append(c);
                    }
                }
                if(c == ' '){
                    foundWord = false;
                }
            }
            String s = sb.toString().replace(" ","").replace("#", ",#");
            return s.substring(1,s.length());
        }
        return string;
    }

}
