package com.github.fsteff.datproxy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class ServiceRestarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent){
        SharedPreferences pref = ctx.getSharedPreferences("datproxy", 0);
        boolean isRestart = intent.hasExtra("restart");
        boolean restart = pref.getBoolean("restart", true);
        boolean boot = pref.getBoolean("boot", true);
        Log.i(ServiceRestarter.class.getSimpleName(), "got intent: " + intent.toString());
        if(restart || (!isRestart && boot)) {
            Log.i(ServiceRestarter.class.getSimpleName(), "restarting service");
            ctx.startService(new Intent(ctx, DiscoveryProxyService.class));
        }
    }
}
