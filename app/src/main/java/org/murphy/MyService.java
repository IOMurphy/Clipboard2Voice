package org.murphy;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;
import java.util.Objects;

public class MyService extends Service implements TextToSpeech.OnInitListener {
    private ClipboardManager mClipboardManager;
    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener;
    private TextToSpeech mTextToSpeech;
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    @Override
    public void onCreate()
    {
        mTextToSpeech = new TextToSpeech(this, this);
        registerClipEvents();
    }

    /**
     * 注册剪切板复制、剪切事件监听
     */
    private void registerClipEvents() {
        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mOnPrimaryClipChangedListener  = new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                if (mClipboardManager.hasPrimaryClip()
                        && Objects.requireNonNull(mClipboardManager.getPrimaryClip()).getItemCount() > 0) {
                    // 获取复制、剪切的文本内容
                    CharSequence content =
                            mClipboardManager.getPrimaryClip().getItemAt(0).getText();
                    Log.d("TAG", "复制、剪切的内容为：" + content);
                    speak(content);
                }
            }
        };
        mClipboardManager.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
    }

    /**
     * 说
     * @param text
     */
    public void speak(CharSequence text) {
        if (!mTextToSpeech.isSpeaking()) {
//            Bundle bundle = new Bundle();
//            bundle.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME,1.0f);
            //朗读，注意这里三个参数的added in API level 4   四个参数的added in API level 21
//            mTextToSpeech.speak(mBinding.editText.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
            mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "");
        }
    }
    /**
     * 初始化语音播放
     * @param status
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS){
            // 设置音调,1.0是常规
            mTextToSpeech.setPitch(1.0f);
            //设定语速 ，默认1.0正常语速
            mTextToSpeech.setSpeechRate(1.0f);
            int result = mTextToSpeech.setLanguage(Locale.CHINA);
            Log.i("status", result + "");
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(this, "请设置Google TTS", Toast.LENGTH_LONG).show();
            }else if(result > 0){
                Toast.makeText(this, "初始化成功，请将软件加到保护名单里面，不然会被杀掉！", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "未知错误，状态码为：" + status, Toast.LENGTH_LONG).show();
            }
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
        mTextToSpeech = null;
    }
}
