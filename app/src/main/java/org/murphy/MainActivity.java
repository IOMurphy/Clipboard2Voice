package org.murphy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipboardManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private ClipboardManager mClipboardManager;
    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener;
    private TextToSpeech mTextToSpeech;
    private TextView textView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        registerClipEvents();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mTextToSpeech = new TextToSpeech(this, this);
    }

    public void speak(CharSequence text) {
        if (mTextToSpeech != null && !mTextToSpeech.isSpeaking()) {
//            Bundle bundle = new Bundle();
//            bundle.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME,1.0f);
            //朗读，注意这里三个参数的added in API level 4   四个参数的added in API level 21
//            mTextToSpeech.speak(mBinding.editText.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
            mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "");
        }
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
                        && mClipboardManager.getPrimaryClip().getItemCount() > 0) {
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
                textView.setText("数据丢失或者不受支持，请下载Goolge TTS Engine, 并在设置-》语言与输入法-》其他输入设置中找到首选TTS引擎，并设置为Google TTS");
            }else{
                textView.setText("初始化成功，请将软件加到保护名单里面，不然会被杀掉！");
            }
        }
    }

    /**
     * 注销监听，避免内存泄漏。
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mClipboardManager != null && mOnPrimaryClipChangedListener != null) {
            mClipboardManager.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener);
        }
    }
}
