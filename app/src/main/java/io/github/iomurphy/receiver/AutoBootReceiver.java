package io.github.iomurphy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import java.util.Objects;

import io.github.iomurphy.constants.ActionConst;
import io.github.iomurphy.service.TTSService;

public class AutoBootReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();

        if (Objects.equals(action, "android.intent.action.BOOT_COMPLETED")) {
            startService(context);
        }else if (Objects.equals(action, "android.net.wifi.WIFI_STATE_CHANGED")){ //WIFI状态改变，包括一下两项
            startService(context);
        }if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
            startService(context);
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
            startService(context);
        } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
            startService(context);
        } else if (Objects.equals(intent.getAction(), WifiManager.NETWORK_STATE_CHANGED_ACTION)) {//wifi连接上与否
            startService(context);
        } else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {//wifi打开与否
            startService(context);
        } else if (ActionConst.IOMURPHY_IN_FOCUS_SEND_MESSAGE_ACTION.equals(intent.getAction())){ // 在焦点内
            startService(context);
        }
    }
    public void startService(Context context){

        Intent intent = new Intent(context , TTSService.class);
        // 启动指定Server
        context.startService(intent);
    }
}