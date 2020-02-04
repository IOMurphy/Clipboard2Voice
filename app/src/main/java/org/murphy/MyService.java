package org.murphy;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;
import java.util.Objects;

public class MyService extends Service {
    private static final String LOG_TAG = MyService.class.getSimpleName();
    private ClipboardManager mClipboardManager;
    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener;
    private TextToSpeech mTextToSpeech;
    private CharSequence content;
    // 不懂为什么会调用两次，暂时先这么写
    private long lastTime = 0;
    private TextToSpeech.OnInitListener listener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {

            if (status == TextToSpeech.SUCCESS) {
                SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.class.getName(), Context.MODE_PRIVATE);
                // 设置音调,1.0是常规， 大是女声，小是男声，1.2声音还不错！
                float pitch = sharedPreferences.getFloat("pitch", 1.2f);
                //设定语速 ，默认1.0正常语速
                float speechRate = sharedPreferences.getFloat("rate", 0.7f);
                mTextToSpeech.setPitch(pitch);
                mTextToSpeech.setSpeechRate(speechRate);

                int result = mTextToSpeech.setLanguage(Locale.CHINA);
                Log.i(LOG_TAG, "TextToSpeech status is " + status);
                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(MyService.this, R.string.set_TTS, Toast.LENGTH_LONG).show();
                } else if (result > 0) {
//                    Toast.makeText(MyService.this, R.string.initialization_succeessful, Toast.LENGTH_LONG).show();
                    speak();
                } else {
                    Toast.makeText(MyService.this, R.string.unknow_error_and_staus_code_is + status, Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        registerClipEvents();
    }

    /**
     * 注册剪切板复制、剪切事件监听
     */
    private void registerClipEvents() {
        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mOnPrimaryClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
            // TODO I don't now why this method always called twice
            @Override
            public void onPrimaryClipChanged() {
                long now = System.currentTimeMillis();
                if (now - lastTime < 1000) {
                    lastTime = now;
                    return;
                }
                lastTime = now;
                if (mClipboardManager.hasPrimaryClip()
                        && Objects.requireNonNull(mClipboardManager.getPrimaryClip()).getItemCount() > 0) {
                    // 获取复制、剪切的文本内容
                    CharSequence content =
                            mClipboardManager.getPrimaryClip().getItemAt(0).getText();
                    Log.d(LOG_TAG, "The content of clipboard: " + content);
                    mTextToSpeech = new TextToSpeech(MyService.this, listener);
                    MyService.this.content = content;
                }
            }
        };
        mClipboardManager.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
    }

    /**
     * 说
     */
    public void speak() {
        if (mTextToSpeech != null) {
            if (mTextToSpeech.isSpeaking()) {
                mTextToSpeech.stop();
            }
            mTextToSpeech.speak(this.content, TextToSpeech.QUEUE_FLUSH, null, "");
        } else {
            Toast.makeText(this, R.string.text_to_speak_is_not_work_properly, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 注销监听，避免内存泄漏。
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mClipboardManager != null && mOnPrimaryClipChangedListener != null) {
            mClipboardManager.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener);
            mClipboardManager = null;
        }
        if (mTextToSpeech != null) {
            mTextToSpeech.shutdown();
            mTextToSpeech = null;
        }
    }
}
