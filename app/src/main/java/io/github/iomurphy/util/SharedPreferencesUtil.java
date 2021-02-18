package io.github.iomurphy.util;

import android.content.Context;
import android.content.SharedPreferences;

import io.github.iomurphy.MainActivity;

public class SharedPreferencesUtil {

    public static final String SPEECH_RATE = "rate";
    public static final String SPEECH_PITCH = "pitch";
    public static final String SPEECH_LANGUAGE = "language";
    /**
     * 设置tts速率
     * @param context
     * @param rate
     */
    public static void setSpeechRate(Context context, float rate) {
        if (rate == 0.0f) {
            return;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.class.getName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(SPEECH_RATE, rate);
        editor.apply();
    }


    /**
     * 获得tts速率
     * @return
     */
    public static float getSpeechRate(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.class.getName(), Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(SPEECH_RATE, 1.0f);
    }

    /**
     * 设置声调
     * @param context
     * @param pitch
     */
    public static void setSpeechPitch(Context context, float pitch) {
        if (pitch == 0.0f) {
            return;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.class.getName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(SPEECH_PITCH, pitch);
        editor.apply();
    }


    /**
     * 获得声调
     * @param context
     * @return
     */
    public static float getSpeechPitch(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.class.getName(), Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(SPEECH_PITCH, 1.2f);
    }

    /**
     * 设置语言
     * @param context
     * @param locale
     */
    public static void setLocale(Context context, String locale) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.class.getName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SPEECH_LANGUAGE, locale);
        editor.apply();
    }


    /**
     * 获得语言
     * @param context
     * @return
     */
    public static String getLocale(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.class.getName(), Context.MODE_PRIVATE);
        return sharedPreferences.getString(SPEECH_LANGUAGE, "zh_CN_#Hans");
    }
}
