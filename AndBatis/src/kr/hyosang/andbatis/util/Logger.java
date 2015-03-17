package kr.hyosang.andbatis.util;

import android.util.Log;

public class Logger {
    private static final String TAG = "AndBatis";
    
    public static void i(String l) {
        Log.i(TAG, l);
    }
    
    public static void v(String l) {
        Log.v(TAG, l);
    }
    
    public static void d(String l) {
        Log.d(TAG, l);
    }
    
    public static void w(String l) {
        Log.w(TAG, l);
    }
    
    public static void w(Throwable th) {
        Log.w(TAG, Log.getStackTraceString(th));
    }

}
