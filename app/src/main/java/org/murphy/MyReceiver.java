package org.murphy;

import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;

import java.util.Objects;

public class MyReceiver extends BroadcastReceiver
{
    private ClipboardManager mClipboardManager;
    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener;
    private TextToSpeech mTextToSpeech;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED"))
        {
            Intent intent1 = new Intent(context , MyService.class);
            // 启动指定Server
            context.startService(intent1);
        }else if (Objects.equals(intent.getAction(), "android.net.wifi.WIFI_STATE_CHANGED"))
        {
            Intent intent1 = new Intent(context , MyService.class);
            // 启动指定Server
            context.startService(intent1);
        }
    }
}