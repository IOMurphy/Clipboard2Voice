package io.github.iomurphy.util;

import android.content.Context;
import android.icu.util.ULocale;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

public class TTSEngine extends TextToSpeech {

    // TODO memory leak
    private static TTSEngine INSTANCE = null;
    private final Context context;


    private Integer STATUS = UN_INITIALIZE;
    public static final Integer FAILED = 1;
    public static final Integer UN_INITIALIZE = -1;
    public static final Integer SUCCESS = 0;

    public TTSEngine(Context context, OnInitListener listener) {
        super(context, listener);
        this.context = context;
    }


    public static synchronized TTSEngine getInstance(final Context context) {
        if (INSTANCE == null || INSTANCE.STATUS.equals(FAILED)) {
            synchronized (TTSEngine.class) {
                if (INSTANCE == null || INSTANCE.STATUS.equals(FAILED)) {
                    INSTANCE = new TTSEngine(context, new OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if (status == TextToSpeech.SUCCESS) {
                                INSTANCE.STATUS = SUCCESS;
                            } else {
                                INSTANCE.STATUS = FAILED;
                                Toast.makeText(context, "Failed init, status code is " + status, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                TTSEngine.class.notifyAll();
            }
        }
        return INSTANCE;
    }


    @Override
    public int speak(CharSequence text, int queueMode, Bundle params, String utteranceId) {
        // 防止未初始化完成
        synchronized (TTSEngine.class) {
            // 放在这里是为了在更改之后可以看到效果
            setSpeechRate(SharedPreferencesUtil.getSpeechRate(context));
            setPitch(SharedPreferencesUtil.getPitch(context));
            setLanguage(Locale.forLanguageTag(SharedPreferencesUtil.getLocale(context)));
        }
        if(STATUS.equals(SUCCESS)) {
            return super.speak(text, queueMode, params, utteranceId);
        }else{
            Toast.makeText(context, "Failed init!", Toast.LENGTH_LONG).show();
            return ERROR;
        }
    }
}
