package com.github.fsteff.datproxy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.fsteff.DiscoveryServer;

import java.util.concurrent.atomic.AtomicBoolean;


public class DiscoveryProxyService extends Service {
    private DiscoveryServer server;
    public static final AtomicBoolean running = new AtomicBoolean(false);

    public DiscoveryProxyService(){
        if(! running.getAndSet(true)) {
            Log.i(DiscoveryProxyService.class.getSimpleName(), "starting service...");
            try {
                server = new DiscoveryServer();
                server.start();
            }catch (Exception e){
                Log.e(DiscoveryProxyService.class.getSimpleName(), e.getMessage());
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy(){
        if(running.get() && server != null) {
            Log.i(DiscoveryProxyService.class.getSimpleName(), "shutting down service...");
            running.set(false);
            server.shutdown();
            Intent bi = new Intent(getResources().getString(R.string.intent_name));
            bi.putExtra("restart", true);
            sendBroadcast(bi);
        }
    }
}
