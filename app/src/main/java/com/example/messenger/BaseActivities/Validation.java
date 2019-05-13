package com.example.messenger.BaseActivities;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation
{
    static private Pattern pattern;
    static private Matcher matcher;

    private static final String USERNAME_PATTERN = "^([a-zA-Z'àâéèêôùûçÀÂÉÈÔÙÛÇ\\s-]{1,40})$";
    private static final String TEXT_PATTERN = "^([a-zA-Z'àâéèêôùûçÀÂÉÈÔÙÛÇ\\s-]{1,40})$";
    private static final String EMAIL_PATTERN = "^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}$";
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";

    static public boolean UsernameValidator(String username)
    {
        pattern = Pattern.compile(USERNAME_PATTERN);
        matcher = pattern.matcher(username);

        return matcher.matches();
    }

    static public boolean PasswordValidator(String text)
    {
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(text);

        return matcher.matches();
    }

    static public boolean EmailValidator(String email)
    {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);

        return matcher.matches();
    }

    static public boolean TextValidator(String text)
    {
        pattern = Pattern.compile(TEXT_PATTERN);
        matcher = pattern.matcher(text);

        return matcher.matches();
    }

    static public boolean ValidationRequiredFields(String Champ)
    {

        Log.i("validation", "Valeur Champ = " + Champ);
        if(Champ.equals(""))
        {
            return false;
        }
        else {
            return true;
        }
    }
    static public boolean ValidationPassWord(String text_1, String text_2)
    {
        if(text_1.matches(text_2))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
