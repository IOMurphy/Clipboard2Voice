package org.murphy;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

public class MyTextToSpeech extends TextToSpeech {

    private CharSequence speechText;

    /**
     * MyTextToSpeech is disposable because of long time No-Responding, so add speech text to constructor.
     * @param context
     * @param onInitListener
     * @param speechText The text of waiting speech
     */
    public MyTextToSpeech(Context context, OnInitListener onInitListener, CharSequence speechText){
        super(context,onInitListener);
        this.speechText = speechText;
    }

    public int speak(int queueMode, Bundle params, String utteranceId) {

        return super.speak(speechText, queueMode, params, utteranceId);
    }
}
