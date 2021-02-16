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

public class MyService extends Service implements ClipboardManager.OnPrimaryClipChangedListener{
    private static final String LOG_TAG = MyService.class.getSimpleName();
    private ClipboardManager mClipboardManager;
    private MyTextToSpeech mTextToSpeech;
    // 不懂为什么会调用两次，暂时先这么写
    private long lastTime = 0;


    /**
     * 用于焦点恢复时（进入app时读取剪切板）
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(mClipboardManager != null){
            onPrimaryClipChanged();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * TTS Init Listener
     */
    private TextToSpeech.OnInitListener listener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {

            if (status == TextToSpeech.SUCCESS) {
                SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.class.getName(), Context.MODE_PRIVATE);
                // 设置音调,1.0是常规， 大是女声，小是男声，1.2声音还不错！
                float pitch = sharedPreferences.getFloat("pitch", 1.0f);
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
                    if (mTextToSpeech.isSpeaking()) {
                        mTextToSpeech.stop();
                    }
                    mTextToSpeech.speak(TextToSpeech.QUEUE_FLUSH, null, "");
                } else {
                    Toast.makeText(MyService.this, R.string.unknow_error_and_staus_code_is + status, Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(MyService.this, "Failed init, status code is " + status, Toast.LENGTH_LONG).show();
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
        if(mClipboardManager == null){
            Toast.makeText(this, "Force close because of @Nullable clipboard manager.", Toast.LENGTH_LONG).show();
            throw new RuntimeException("Force close because of @Nullable clipboard manager.");
        }
        mClipboardManager.addPrimaryClipChangedListener(this);
    }

    /**
     * 剪切板监听器的回调
     */
    @Override
    public void onPrimaryClipChanged() {
        // this method always called twice, so make a check
        long now = System.currentTimeMillis();
        if (now - lastTime < 1000) {
            lastTime = now;
            return;
        }
        lastTime = now;
        if (mClipboardManager != null && mClipboardManager.hasPrimaryClip()
                && Objects.requireNonNull(mClipboardManager.getPrimaryClip()).getItemCount() > 0) {
            // 获取复制、剪切的文本内容
            CharSequence content =
                    mClipboardManager.getPrimaryClip().getItemAt(0).getText();
            Log.d(LOG_TAG, "The content of clipboard: " + content);
            mTextToSpeech = new MyTextToSpeech(MyService.this, listener, content);
        }
    }

    /**
     * 注销监听，避免内存泄漏。
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mClipboardManager != null ) {
            mClipboardManager = null;
        }
        if (mTextToSpeech != null) {
            mTextToSpeech.shutdown();
            mTextToSpeech = null;
        }
    }
}
