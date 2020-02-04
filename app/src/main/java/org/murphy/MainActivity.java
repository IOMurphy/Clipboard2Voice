package org.murphy;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



public class MainActivity extends AppCompatActivity{

    private EditText editRate;
    private EditText editPitch;
    private Button testBt;
    private Button applyBt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editRate = findViewById(R.id.editRate);
        editPitch = findViewById(R.id.editPitch);
        testBt = findViewById(R.id.testBt);
        applyBt = findViewById(R.id.applyBt);
        Intent startIntent = new Intent(MainActivity.this, MyService.class);
        //startService启动形式
        startService(startIntent);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(new MyReceiver(), filter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindListener();
    }

    public void bindListener() {
        testBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Did't implement now", Toast.LENGTH_LONG).show();
            }
        });
        applyBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSpeechPitch(getSpeechPitch());
                setSpeechRate(getSpeechRate());
                Toast.makeText(MainActivity.this, R.string.OK, Toast.LENGTH_LONG).show();
            }
        });
    }

    public float getSpeechRate() {
        if (editRate.getText() == null || "".equals(editRate.getText().toString())) {
            return 0.0f;
        }
        return Float.parseFloat(editRate.getText().toString());
    }

    public void setSpeechRate(float rate) {
        if (rate == 0.0f) {
            return;
        }
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.class.getName(), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("rate", rate);
        editor.apply();
    }

    public float getSpeechPitch() {
        if (editPitch.getText() == null || "".equals(editPitch.getText().toString())) {
            return 0.0f;
        }
        return Float.parseFloat(editPitch.getText().toString());
    }

    public void setSpeechPitch(float pitch) {
        if (pitch == 0.0f) {
            return;
        }
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.class.getName(), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("pitch", pitch);
        editor.apply();
    }
}
