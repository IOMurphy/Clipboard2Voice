package io.github.iomurphy.util;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

public class TTSEngine extends TextToSpeech {

    private static TTSEngine INSTANCE = null;

    public static final Integer FAILED = 1;
    public static final Integer UN_INITIALIZE = -1;
    public static final Integer SUCCESS = 0;

    private static Integer status = UN_INITIALIZE;

    public TTSEngine(Context context, OnInitListener listener) {
        super(context, listener);
    }


    public static synchronized TTSEngine getInstance(final Context context) {
        if (INSTANCE == null || TTSEngine.status.equals(FAILED)) {
            synchronized (TTSEngine.class) {
                if (INSTANCE == null || TTSEngine.status.equals(FAILED)) {
                    INSTANCE = new TTSEngine(context, new OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if (status == TextToSpeech.SUCCESS) {
                                // NPE
                                TTSEngine.status = SUCCESS;
                            } else {
                                TTSEngine.status = FAILED;
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

    /**
     * 关闭
     */
    public static void shutdownNow() {
        synchronized (TTSEngine.class) {
            if (INSTANCE != null) {
                INSTANCE.shutdown();
            }
        }
    }

    /**
     * 说话
     *
     * @param text
     * @param speechRate
     * @param pitch
     * @param locale
     * @return
     */
    public int speak(CharSequence text, float speechRate, float pitch, Locale locale) {
        if (status.equals(SUCCESS)) {
            // 防止未初始化完成
            synchronized (TTSEngine.class) {
                // 放在这里是为了在更改之后可以看到效果
                setSpeechRate(speechRate);
                setPitch(pitch);
                setLanguage(locale);
            }
            return speak(text, TextToSpeech.QUEUE_FLUSH, null, "");
        } else {
            status = FAILED;
            return ERROR;
        }
    }

    @Override
    public int speak(CharSequence text, int queueMode, Bundle params, String utteranceId) {
        return super.speak(text, queueMode, params, utteranceId);
    }
}
