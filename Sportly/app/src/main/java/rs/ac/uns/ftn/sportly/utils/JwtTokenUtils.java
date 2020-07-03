package rs.ac.uns.ftn.sportly.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class JwtTokenUtils {

    private static String MyPrefrences = "Sportly.xml";
    private static Integer Mode = Context.MODE_PRIVATE;

    public static boolean saveJwtToken(Long userId, String name, String email, String jwtToken, Context context){
        try {
            SharedPreferences sharedpreferences = context.getSharedPreferences(MyPrefrences, Mode);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("jwt", jwtToken);
            editor.putLong("userId",userId);
            editor.putString("name",name);
            editor.putString("email",email);
            editor.commit();
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static boolean removeJwtToken(Context context){
        try {
            SharedPreferences sharedpreferences = context.getSharedPreferences(MyPrefrences, Mode);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.remove("jwt");
            editor.remove("userId");
            editor.remove("name");
            editor.remove("email");
            editor.commit();
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static String getJwtToken(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPrefrences, Mode);
        return sharedpreferences.getString("jwt","DEFAULT");
    }

    public static Long getUserId(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPrefrences, Mode);
        return sharedpreferences.getLong("userId",-1);
    }

    public static String getName(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPrefrences, Mode);
        return sharedpreferences.getString("name","Name");
    }

    public static String getEmail(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPrefrences, Mode);
        return sharedpreferences.getString("email","Email");
    }
}
