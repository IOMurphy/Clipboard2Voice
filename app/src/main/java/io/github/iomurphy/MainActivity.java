package io.github.iomurphy;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.util.ULocale;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.iomurphy.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import io.github.iomurphy.constants.ActionConst;
import io.github.iomurphy.receiver.AutoBootReceiver;
import io.github.iomurphy.service.TTSService;
import io.github.iomurphy.util.SharedPreferencesUtil;
import io.github.iomurphy.util.TTSEngine;


public class MainActivity extends AppCompatActivity {

    private EditText editRate;
    private EditText editPitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent startIntent = new Intent(MainActivity.this, TTSService.class);
        //startService启动形式
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(ActionConst.IOMURPHY_IN_FOCUS_SEND_MESSAGE_ACTION);
        registerReceiver(new AutoBootReceiver(), filter);
        startService(startIntent);
        initData();
    }


    /**
     * 设置数据源
     */
    private void initData() {
        /**
         * 下拉框
         */
        Spinner localeSpinner = findViewById(R.id.localeSpinner);
        Locale[] availableLocales = Locale.getAvailableLocales();
        final List<Locale> locales = Arrays.asList(availableLocales);
        SpinnerAdapter adapter =
                new ArrayAdapter<Locale>(this, android.R.layout.simple_spinner_item, locales);
        localeSpinner.setAdapter(adapter);
        localeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Locale locale = locales.get(position);
                SharedPreferencesUtil.setLocale(MainActivity.this, locale.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(MainActivity.this, "没有选中任何东西", Toast.LENGTH_SHORT).show();
            }
        });
        int i = 0;
        for (Locale l : locales) {
            if (l.toString().equals(SharedPreferencesUtil.getLocale(this))) {
                localeSpinner.setSelection(i);
                break;
            }
            i++;
        }
        /**
         * 速率
         */
        editRate = findViewById(R.id.editRate);
        float speechRate = SharedPreferencesUtil.getSpeechRate(this);
        editRate.setText(String.format("%s", speechRate));
        /**
         * 语调
         */
        editPitch = findViewById(R.id.editPitch);
        float speechPitch = SharedPreferencesUtil.getPitch(this);
        editPitch.setText(String.format("%s", speechPitch));
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindListener();
    }

    public void bindListener() {
        Button applyBt = findViewById(R.id.applyBt);
        ImageButton repeatBt = findViewById(R.id.repeatBt);
        applyBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.setPitch(MainActivity.this, getSpeechPitch());
                SharedPreferencesUtil.setSpeechRate(MainActivity.this, getSpeechRate());
                Toast.makeText(MainActivity.this, R.string.OK, Toast.LENGTH_LONG).show();
            }
        });
        repeatBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boardCastClipBoard();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        boardCastClipBoard();
    }

    /**
     * 通过广播唤醒service
     */
    private void boardCastClipBoard() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent intent = new Intent();
            intent.setAction(ActionConst.IOMURPHY_IN_FOCUS_SEND_MESSAGE_ACTION);
            // 设置接收的receiver的路径
            intent.setComponent(new ComponentName(AutoBootReceiver.class.getPackage().getName(), AutoBootReceiver.class.getName()));
            // 设置为下面这样才可以，getPackageName获取到的是applicationId的包名，可能和使用类获取的包名不一样
            intent.setComponent(new ComponentName(getPackageName(), AutoBootReceiver.class.getName()));
            sendBroadcast(intent);
        }
    }

    /**
     * 从控件中获得tts速率值
     *
     * @return
     */
    public float getSpeechRate() {
        if (editRate.getText() == null || "".equals(editRate.getText().toString())) {
            return 0.0f;
        }
        return Float.parseFloat(editRate.getText().toString());
    }


    /**
     * 从控件中获得声调输入
     *
     * @return
     */
    public float getSpeechPitch() {
        if (editPitch.getText() == null || "".equals(editPitch.getText().toString())) {
            return 0.0f;
        }
        return Float.parseFloat(editPitch.getText().toString());
    }
}
