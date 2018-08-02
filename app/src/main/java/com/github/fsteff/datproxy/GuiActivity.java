package com.github.fsteff.datproxy;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

public class GuiActivity extends AppCompatActivity {
    Intent mServiceIntent;
    DiscoveryProxyService mService;
    Context ctx;

    public Context getCtx() {
        return ctx;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_gui);

        mService = new DiscoveryProxyService();
        mServiceIntent = new Intent(this, DiscoveryProxyService.class);

        if(! isMyServiceRunning(mService.getClass())){
            startService(mServiceIntent);
        }

        final SharedPreferences pref = ctx.getSharedPreferences("datproxy", 0);
        final Switch restart = findViewById(R.id.switch_background);
        restart.setChecked(pref.getBoolean("restart", true));
        restart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean val) {
                pref.edit().putBoolean("restart", val).commit();
                Log.i(GuiActivity.class.getSimpleName(), "restart button was set to " + val);
                if(val && ! isMyServiceRunning(mService.getClass())){
                    startService(mServiceIntent);
                }
            }
        });

        final Switch autostart = findViewById(R.id.switch_autostart);
        autostart.setChecked(pref.getBoolean("boot", true));
        autostart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean val) {
                Log.i(GuiActivity.class.getSimpleName(), "autostart button was set to " + val);
                pref.edit().putBoolean("boot", val).commit();
            }
        });

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    @Override
    protected void onDestroy(){
        if(! ctx.getSharedPreferences("datproxy", 0).getBoolean("restart", true))
            stopService(mServiceIntent);
        super.onDestroy();
    }
}
