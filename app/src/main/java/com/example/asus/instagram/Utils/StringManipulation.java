package com.example.asus.instagram.Utils;

//Yuan: used to manipulate some string...
public class StringManipulation {

    // replace "_" in user name with a space
    public static String condenseUsername(String userName){
        return userName.replace(" ","_");
    }

    // replace a space in user name with "_"
    public static String expandUsername(String userName){
        return userName.replace("_"," ");
    }

}
