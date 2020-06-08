package rs.ac.uns.ftn.sportly.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class JwtTokenUtils {

    private static String MyPrefrences = "Sportly.xml";
    private static Integer Mode = Context.MODE_PRIVATE;

    public static boolean saveJwtToken(String jwtToken, Context context){
        try {
            SharedPreferences sharedpreferences = context.getSharedPreferences(MyPrefrences, Mode);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("jwt", jwtToken);
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
}
