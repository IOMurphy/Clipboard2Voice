package io.github.iomurphy.service;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;


import java.util.Locale;
import java.util.Objects;

import io.github.iomurphy.util.SharedPreferencesUtil;
import io.github.iomurphy.util.TTSEngine;

public class TTSService extends Service implements ClipboardManager.OnPrimaryClipChangedListener {
    private static final String LOG_TAG = TTSService.class.getSimpleName();
    private ClipboardManager mClipboardManager;
    // 不懂为什么会调用两次，暂时先这么写
    private long lastTime = 0;


    /**
     * 用于焦点恢复时（进入app时读取剪切板）
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (mClipboardManager != null) {
            onPrimaryClipChanged();
        }
        return super.onStartCommand(intent, flags, startId);
    }


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
        if (mClipboardManager == null) {
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
            // 放在这里是为了在更改之后可以看到效果
            float speechRate = SharedPreferencesUtil.getSpeechRate(this);
            float pitch = SharedPreferencesUtil.getPitch(this);
            Locale locale = Locale.forLanguageTag(SharedPreferencesUtil.getLocale(this));
            TTSEngine instance = TTSEngine.getInstance(this);
            if (instance != null) {
                int code = instance.speak(content, speechRate, pitch, locale);
                if (code == TextToSpeech.ERROR) {
                    Toast.makeText(this, "Failed to speak, code is " + code + ".", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 注销监听，避免内存泄漏。
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mClipboardManager != null) {
            mClipboardManager = null;
        }
        TTSEngine.shutdownNow();
    }
}
